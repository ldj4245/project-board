package com.genielee.projectboard.service;

import com.genielee.projectboard.domain.Article;
import com.genielee.projectboard.domain.ArticleBookmark;
import com.genielee.projectboard.domain.UserAccount;
import com.genielee.projectboard.repository.ArticleBookmarkRepository;
import com.genielee.projectboard.repository.ArticleRepository;
import com.genielee.projectboard.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleBookmarkService {

    private final ArticleBookmarkRepository articleBookmarkRepository;
    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;

    /**
     * 게시글 북마크를 토글합니다.
     * @return 북마크 후 상태 (true: 북마크, false: 북마크 취소)
     */
    public boolean toggleBookmark(Long articleId, String userId) {
        Article article = articleRepository.getReferenceById(articleId);
        UserAccount userAccount = userAccountRepository.getReferenceById(userId);
        
        if (articleBookmarkRepository.existsByArticleAndUserAccount(article, userAccount)) {
            // 이미 북마크한 경우 - 북마크 취소
            articleBookmarkRepository.deleteByArticleAndUserAccount(article, userAccount);
            log.debug("북마크 취소: 게시글 ID = {}, 사용자 ID = {}", articleId, userId);
            return false;
        } else {
            // 북마크 추가
            ArticleBookmark articleBookmark = ArticleBookmark.of(article, userAccount);
            articleBookmarkRepository.save(articleBookmark);
            log.debug("북마크 추가: 게시글 ID = {}, 사용자 ID = {}", articleId, userId);
            return true;
        }
    }

    @Transactional(readOnly = true)
    public boolean isBookmarkedByUser(Long articleId, String userId) {
        if (userId == null) return false;
        
        Article article = articleRepository.getReferenceById(articleId);
        UserAccount userAccount = userAccountRepository.getReferenceById(userId);
        return articleBookmarkRepository.existsByArticleAndUserAccount(article, userAccount);
    }

    @Transactional(readOnly = true)
    public Page<Article> getBookmarkedArticles(String userId, Pageable pageable) {
        UserAccount userAccount = userAccountRepository.getReferenceById(userId);
        Page<Article> articles = articleBookmarkRepository.findBookmarkedArticlesByUserAccount(userAccount, pageable);
        
        // Lazy loading 문제 해결을 위해 필요한 데이터들을 미리 로드
        articles.getContent().forEach(article -> {
            article.getHashtags().size(); // hashtags 초기화
            article.getUserAccount().getNickname(); // userAccount 초기화
        });
        
        return articles;
    }
} 