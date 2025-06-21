package com.genielee.projectboard.service;

import com.genielee.projectboard.domain.Article;
import com.genielee.projectboard.domain.ArticleLike;
import com.genielee.projectboard.domain.UserAccount;
import com.genielee.projectboard.repository.ArticleLikeRepository;
import com.genielee.projectboard.repository.ArticleRepository;
import com.genielee.projectboard.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleLikeService {

    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;

    /**
     * 게시글 좋아요를 토글합니다.
     * @return 좋아요 후 상태 (true: 좋아요, false: 좋아요 취소)
     */
    public boolean toggleLike(Long articleId, String userId) {
        Article article = articleRepository.getReferenceById(articleId);
        UserAccount userAccount = userAccountRepository.getReferenceById(userId);
        
        if (articleLikeRepository.existsByArticleAndUserAccount(article, userAccount)) {
            // 이미 좋아요한 경우 - 좋아요 취소
            articleLikeRepository.deleteByArticleAndUserAccount(article, userAccount);
            log.debug("좋아요 취소: 게시글 ID = {}, 사용자 ID = {}", articleId, userId);
            return false;
        } else {
            // 좋아요 추가
            ArticleLike articleLike = ArticleLike.of(article, userAccount);
            articleLikeRepository.save(articleLike);
            log.debug("좋아요 추가: 게시글 ID = {}, 사용자 ID = {}", articleId, userId);
            return true;
        }
    }

    @Transactional(readOnly = true)
    public boolean isLikedByUser(Long articleId, String userId) {
        if (userId == null) return false;
        
        Article article = articleRepository.getReferenceById(articleId);
        UserAccount userAccount = userAccountRepository.getReferenceById(userId);
        return articleLikeRepository.existsByArticleAndUserAccount(article, userAccount);
    }

    @Transactional(readOnly = true)
    public long getLikeCount(Long articleId) {
        Article article = articleRepository.getReferenceById(articleId);
        return articleLikeRepository.countByArticle(article);
    }
} 