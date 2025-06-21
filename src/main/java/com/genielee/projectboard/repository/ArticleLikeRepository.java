package com.genielee.projectboard.repository;

import com.genielee.projectboard.domain.Article;
import com.genielee.projectboard.domain.ArticleLike;
import com.genielee.projectboard.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    
    Optional<ArticleLike> findByArticleAndUserAccount(Article article, UserAccount userAccount);
    
    boolean existsByArticleAndUserAccount(Article article, UserAccount userAccount);
    
    void deleteByArticleAndUserAccount(Article article, UserAccount userAccount);
    
    @Query("SELECT COUNT(al) FROM ArticleLike al WHERE al.article = :article")
    long countByArticle(@Param("article") Article article);
} 