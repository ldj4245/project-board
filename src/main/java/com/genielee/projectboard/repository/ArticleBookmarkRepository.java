package com.genielee.projectboard.repository;

import com.genielee.projectboard.domain.Article;
import com.genielee.projectboard.domain.ArticleBookmark;
import com.genielee.projectboard.domain.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArticleBookmarkRepository extends JpaRepository<ArticleBookmark, Long> {
    
    Optional<ArticleBookmark> findByArticleAndUserAccount(Article article, UserAccount userAccount);
    
    boolean existsByArticleAndUserAccount(Article article, UserAccount userAccount);
    
    void deleteByArticleAndUserAccount(Article article, UserAccount userAccount);
    
    @Query("SELECT ab.article FROM ArticleBookmark ab WHERE ab.userAccount = :userAccount ORDER BY ab.createdAt DESC")
    Page<Article> findBookmarkedArticlesByUserAccount(@Param("userAccount") UserAccount userAccount, Pageable pageable);
} 