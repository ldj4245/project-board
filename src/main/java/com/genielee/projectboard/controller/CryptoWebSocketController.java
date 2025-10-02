package com.genielee.projectboard.controller;

import com.genielee.projectboard.dto.response.MarketPrice;
import com.genielee.projectboard.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 실시간 시세 업데이트 WebSocket 컨트롤러
 * 코인판 스타일 실시간 기능 제공
 */
@Slf4j
@Controller
public class CryptoWebSocketController {

    private final MarketDataService marketDataService;
    private final SimpMessagingTemplate messagingTemplate;

    public CryptoWebSocketController(MarketDataService marketDataService, SimpMessagingTemplate messagingTemplate) {
        this.marketDataService = marketDataService;
        this.messagingTemplate = messagingTemplate;
    }

    // 활성 구독자 관리
    private final Set<String> activeSubscriptions = ConcurrentHashMap.newKeySet();
    private final Map<String, Long> lastUpdateTimes = new ConcurrentHashMap<>();

    /**
     * 클라이언트가 실시간 시세 구독 요청
     * /app/subscribe-prices
     */
    @MessageMapping("/subscribe-prices")
    public void subscribePrices(@Payload PriceSubscriptionRequest request) {
        String subscriptionKey = request.exchange() + ":" + request.symbol();
        activeSubscriptions.add(subscriptionKey);
        
        log.info("New price subscription: {}", subscriptionKey);
        
        // 즉시 현재 시세 전송
        marketDataService.getMarketPrice(request.exchange(), request.symbol(), request.quoteCurrency())
                .doOnNext(price -> sendPriceUpdate(subscriptionKey, price))
                .subscribe();
    }

    /**
     * 전체 주요 코인 구독 요청
     * /app/subscribe-major-coins
     */
    @MessageMapping("/subscribe-major-coins")
    public void subscribeMajorCoins() {
        List<String> majorCoins = List.of("BTC", "ETH", "XRP", "ADA", "SOL");
        List<String> exchanges = List.of("upbit", "bithumb", "binance");
        
        for (String coin : majorCoins) {
            for (String exchange : exchanges) {
                String subscriptionKey = exchange + ":" + coin;
                activeSubscriptions.add(subscriptionKey);
            }
        }
        
        log.info("Major coins subscription activated for {} coins", majorCoins.size());
    }

    /**
     * 3초마다 실시간 시세 업데이트 전송
     * 코인판 스타일: 빠른 실시간 업데이트
     */
    @Scheduled(fixedRate = 3000) // 3초마다
    public void sendRealTimePriceUpdates() {
        if (activeSubscriptions.isEmpty()) {
            return;
        }

        List<String> majorCoins = List.of("BTC", "ETH", "XRP", "ADA", "SOL");
        
        for (String coin : majorCoins) {
            // 업비트 (국내 대표)
            updateCoinPrice("upbit", coin, "KRW");
            
            // 바이낸스 (해외 대표)  
            updateCoinPrice("binance", coin, "USDT");
        }
    }

    /**
     * 개별 코인 시세 업데이트
     */
    private void updateCoinPrice(String exchange, String symbol, String quoteCurrency) {
        String subscriptionKey = exchange + ":" + symbol;
        
        if (!activeSubscriptions.contains(subscriptionKey)) {
            return;
        }

        marketDataService.getMarketPrice(exchange, symbol, quoteCurrency)
                .doOnNext(price -> sendPriceUpdate(subscriptionKey, price))
                .doOnError(error -> log.warn("Failed to update price for {}: {}", subscriptionKey, error.getMessage()))
                .subscribe();
    }

    /**
     * 시세 업데이트 전송
     */
    private void sendPriceUpdate(String subscriptionKey, MarketPrice price) {
        // 중복 업데이트 방지 (1초 이내 동일 요청 무시)
        long currentTime = System.currentTimeMillis();
        Long lastUpdate = lastUpdateTimes.get(subscriptionKey);
        
        if (lastUpdate != null && (currentTime - lastUpdate) < 1000) {
            return;
        }
        
        lastUpdateTimes.put(subscriptionKey, currentTime);
        
        // 토픽별로 전송
        messagingTemplate.convertAndSend("/topic/prices/" + subscriptionKey, price);
        messagingTemplate.convertAndSend("/topic/prices/all", price); // 전체 구독자용
        
        log.debug("Price update sent: {} = {}", subscriptionKey, price.price());
    }

    /**
     * 구독 해제
     * /app/unsubscribe-prices  
     */
    @MessageMapping("/unsubscribe-prices")
    public void unsubscribePrices(@Payload PriceSubscriptionRequest request) {
        String subscriptionKey = request.exchange() + ":" + request.symbol();
        activeSubscriptions.remove(subscriptionKey);
        lastUpdateTimes.remove(subscriptionKey);
        
        log.info("Price subscription removed: {}", subscriptionKey);
    }

    /**
     * WebSocket 구독 요청 DTO
     */
    public record PriceSubscriptionRequest(
            String exchange,
            String symbol,
            String quoteCurrency
    ) {}
}
