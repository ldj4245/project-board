package com.genielee.projectboard.controller;

import com.genielee.projectboard.domain.constant.BoardCategory;
import com.genielee.projectboard.dto.response.ArticleResponse;
import com.genielee.projectboard.dto.ArticleCommentDto;
import com.genielee.projectboard.service.ArticleService;
import com.genielee.projectboard.service.ArticleCommentService;
import com.genielee.projectboard.service.MarketDataService;
import com.genielee.projectboard.service.PremiumCalculationService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 코인판 스타일 게시판 컨트롤러
 * 카테고리별 게시판 페이지 제공
 */
@Controller
@RequiredArgsConstructor
public class CryptoBoardController {

    private final ArticleService articleService;
    private final ArticleCommentService articleCommentService;
    private final MarketDataService marketDataService;
    private final PremiumCalculationService premiumService;

    /**
     * 코인판 스타일 메인 페이지
     */
    @GetMapping("/crypto")
    public String cryptoMain(Model model) {
        // 최신글 (전체 상위 20개 - 생성일 기준)
        PageRequest latestPageRequest = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        Page<ArticleResponse> latestArticles = articleService.searchArticles(null, null, latestPageRequest)
                .map(ArticleResponse::from);
        
        // 인기글 (조회수 많은 상위 5개)
        PageRequest hotPageRequest = PageRequest.of(0, 5, Sort.by("viewCount").descending());
        Page<ArticleResponse> hotArticles = articleService.searchArticles(null, null, hotPageRequest)
                .map(ArticleResponse::from);
        
        // 최신 댓글 10개
        List<ArticleCommentDto> latestComments = articleCommentService.getLatestComments();
        
        // 게시판 카테고리 정보
        model.addAttribute("boardCategories", BoardCategory.values());
        model.addAttribute("latestArticles", latestArticles.getContent());
        model.addAttribute("hotArticles", hotArticles.getContent());
        model.addAttribute("latestComments", latestComments);
        
        return "crypto/main";
    }

    /**
     * 카테고리별 게시판 페이지
     */
    @GetMapping("/boards/{category}")
    public String categoryBoard(
            @PathVariable String category,
            @RequestParam(required = false) String coinSymbol,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        
        try {
            BoardCategory boardCategory = BoardCategory.valueOf(category.toUpperCase());
            
            // 카테고리별 게시글 조회 - 임시로 빈 페이지
            var articles = org.springframework.data.domain.Page.<Object>empty();
            
            model.addAttribute("articles", articles);
            model.addAttribute("currentCategory", boardCategory);
            model.addAttribute("selectedCoinSymbol", coinSymbol);
            model.addAttribute("boardCategories", BoardCategory.values());
            
            return "crypto/board"; // 카테고리별 게시판 페이지
            
        } catch (IllegalArgumentException e) {
            return "redirect:/"; // 잘못된 카테고리면 메인으로
        }
    }

    /**
     * 코인별 게시글 페이지
     */
    @GetMapping("/coins/{coinSymbol}")
    public String coinBoard(
            @PathVariable String coinSymbol,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        
        // 특정 코인 관련 모든 게시글 - 임시로 빈 페이지
        var articles = org.springframework.data.domain.Page.<Object>empty();
        
        // 해당 코인의 실시간 시세 정보
        marketDataService.getAllExchangePrices(coinSymbol)
                .doOnNext(prices -> model.addAttribute("coinPrices", prices))
                .subscribe();
        
        model.addAttribute("articles", articles);
        model.addAttribute("coinSymbol", coinSymbol.toUpperCase());
        model.addAttribute("boardCategories", BoardCategory.values());
        
        return "crypto/coin-board"; // 코인별 게시판 페이지
    }

    /**
     * 실시간 대시보드
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // 주요 코인들의 실시간 시세
        List<String> majorCoins = List.of("BTC", "ETH", "XRP", "ADA", "SOL");
        
        model.addAttribute("majorCoins", majorCoins);
        model.addAttribute("boardCategories", BoardCategory.values());
        
        return "crypto/dashboard"; // 실시간 대시보드 페이지
    }
}
