package com.genielee.projectboard.service;

import com.genielee.projectboard.domain.Article;
import com.genielee.projectboard.domain.ArticleView;
import com.genielee.projectboard.repository.ArticleRepository;
import com.genielee.projectboard.repository.ArticleViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleViewService {

    private final ArticleViewRepository articleViewRepository;
    private final ArticleRepository articleRepository;

    /**
     * 게시글 조회수를 증가시킵니다. (24시간 내 중복 조회 방지)
     */
    public void incrementViewCount(Long articleId, String viewerIp, String userId) {
        Article article = articleRepository.getReferenceById(articleId);
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        
        // 24시간 내에 이미 조회한 기록이 있는지 확인
        boolean hasRecentView = articleViewRepository.existsByArticleAndViewerIpAndUserIdAndCreatedAtAfter(
            article, viewerIp, userId, oneDayAgo
        );
        
        if (!hasRecentView) {
            // 새로운 조회 기록 생성
            ArticleView articleView = ArticleView.of(article, viewerIp, userId);
            articleViewRepository.save(articleView);
            
            // 게시글 조회수 증가
            article.incrementViewCount();
            articleRepository.save(article);
            
            log.debug("조회수 증가: 게시글 ID = {}, IP = {}, 사용자 ID = {}", articleId, viewerIp, userId);
        }
    }

    @Transactional(readOnly = true)
    public long getViewCount(Long articleId) {
        return articleRepository.getReferenceById(articleId).getViewCount();
    }
} 