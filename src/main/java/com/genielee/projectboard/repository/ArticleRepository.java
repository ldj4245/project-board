package com.genielee.projectboard.repository;

import com.genielee.projectboard.domain.Article;
import com.genielee.projectboard.domain.QArticle;
import com.genielee.projectboard.domain.constant.BoardCategory;
import com.genielee.projectboard.domain.projection.ArticleProjection;
import com.genielee.projectboard.repository.querydsl.ArticleRepositoryCustom;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(excerptProjection = ArticleProjection.class)
public interface ArticleRepository extends
        JpaRepository<Article, Long>,
        ArticleRepositoryCustom,
        QuerydslPredicateExecutor<Article>,
        QuerydslBinderCustomizer<QArticle>
{

    Page<Article> findByTitleContaining(String title, Pageable pageable);
    Page<Article> findByContentContaining(String content, Pageable pageable);
    Page<Article> findByUserAccount_UserIdContaining(String userId, Pageable pageable);
    Page<Article> findByUserAccount_NicknameContaining(String nickname, Pageable pageable);
    void deleteByIdAndUserAccount_UserId(Long articleId, String userid);

    // 코인판 스타일 확장 쿼리 메서드들
    Page<Article> findByCategoryOrderByCreatedAtDesc(BoardCategory category, Pageable pageable);
    Page<Article> findByCoinSymbolOrderByCreatedAtDesc(String coinSymbol, Pageable pageable);
    Page<Article> findByIsPinnedTrueOrderByCreatedAtDesc(Pageable pageable);
    Page<Article> findByIsHotTrueOrderByViewCountDesc(Pageable pageable);
    
    // 카테고리별 + 코인 필터링
    Page<Article> findByCategoryAndCoinSymbolOrderByCreatedAtDesc(BoardCategory category, String coinSymbol, Pageable pageable);
    
    // 인기글 조회 (조회수 + 댓글 수 기준)
    @Query("SELECT a FROM Article a LEFT JOIN a.articleComments ac WHERE a.category = :category GROUP BY a ORDER BY (a.viewCount + COUNT(ac)) DESC")
    Page<Article> findHotArticlesByCategory(@Param("category") BoardCategory category, Pageable pageable);
    
    // 특정 코인 관련 인기글
    @Query("SELECT a FROM Article a LEFT JOIN a.articleComments ac WHERE a.coinSymbol = :coinSymbol GROUP BY a ORDER BY (a.viewCount + COUNT(ac)) DESC")
    Page<Article> findHotArticlesByCoinSymbol(@Param("coinSymbol") String coinSymbol, Pageable pageable);

    @Override
    default void customize(QuerydslBindings bindings, QArticle root){
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.title,root.content,root.hashtags,root.createdAt,root.createdBy);
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.hashtags.any().hashtagName).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);

    }
}
