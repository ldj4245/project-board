package com.genielee.projectboard.service.crypto;

import com.genielee.projectboard.domain.crypto.Cryptocurrency;
import com.genielee.projectboard.repository.crypto.CryptocurrencyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 암호화폐 리포지토리 테스트
 */
@ActiveProfiles("test")
@DisplayName("암호화폐 리포지토리 테스트")
@DataJpaTest
class CryptoPriceServiceTest {

    @Autowired
    private CryptocurrencyRepository cryptocurrencyRepository;

    @DisplayName("암호화폐 리포지토리가 정상적으로 주입되는가")
    @Test
    void givenNothing_whenCheckingRepository_thenRepositoryIsInjected() {
        // Given & When & Then
        assertThat(cryptocurrencyRepository).isNotNull();
    }

    @DisplayName("초기 상태에서 활성 암호화폐 개수는 0개")
    @Test
    void givenNothing_whenCountingActiveCryptocurrencies_thenReturnsZero() {
        // Given & When
        long count = cryptocurrencyRepository.countByIsActiveTrue();

        // Then
        assertThat(count).isEqualTo(0L);
    }
    
    @DisplayName("암호화폐를 저장하고 조회할 수 있다")
    @Test
    void givenCryptocurrency_whenSaveAndFind_thenReturnsCorrectData() {
        // Given
        Cryptocurrency bitcoin = Cryptocurrency.of("bitcoin", "BTC", "Bitcoin");
        bitcoin.changeActiveStatus(true); // 활성 상태 명시적 설정
        
        // When
        Cryptocurrency savedCrypto = cryptocurrencyRepository.saveAndFlush(bitcoin);
        long activeCount = cryptocurrencyRepository.countByIsActiveTrue();
        
        // Then
        assertThat(savedCrypto).isNotNull();
        assertThat(savedCrypto.getId()).isEqualTo("bitcoin");
        assertThat(savedCrypto.getSymbol()).isEqualTo("BTC");
        assertThat(savedCrypto.getName()).isEqualTo("Bitcoin");
        assertThat(savedCrypto.getIsActive()).isTrue();
        assertThat(activeCount).isEqualTo(1L);
    }
}