package com.genielee.projectboard.repository;

import com.genielee.projectboard.domain.Article;
import com.genielee.projectboard.domain.ArticleView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ArticleViewRepository extends JpaRepository<ArticleView, Long> {
    
    @Query("SELECT av FROM ArticleView av WHERE av.article = :article AND av.viewerIp = :viewerIp AND (:userId IS NULL OR av.userId = :userId) AND av.createdAt > :since")
    Optional<ArticleView> findRecentView(@Param("article") Article article, 
                                        @Param("viewerIp") String viewerIp, 
                                        @Param("userId") String userId, 
                                        @Param("since") LocalDateTime since);
    
    boolean existsByArticleAndViewerIpAndUserIdAndCreatedAtAfter(Article article, 
                                                               String viewerIp, 
                                                               String userId, 
                                                               LocalDateTime since);
} 