package com.genielee.projectboard.controller;

import com.genielee.projectboard.domain.Article;
import com.genielee.projectboard.domain.constant.SearchType;
import com.genielee.projectboard.dto.ArticleDto;
import com.genielee.projectboard.dto.response.ArticleResponse;
import com.genielee.projectboard.service.ArticleBookmarkService;
import com.genielee.projectboard.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/articles")
@RestController
public class ArticleBookmarkController {

    private final ArticleBookmarkService articleBookmarkService;

    @PostMapping("/{articleId}/bookmark")
    public ResponseEntity<Map<String, Object>> toggleBookmark(
            @PathVariable Long articleId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        boolean isBookmarked = articleBookmarkService.toggleBookmark(articleId, userDetails.getUsername());
        
        return ResponseEntity.ok(Map.of(
            "isBookmarked", isBookmarked
        ));
    }

    @GetMapping("/{articleId}/bookmark")
    public ResponseEntity<Map<String, Object>> getBookmarkStatus(
            @PathVariable Long articleId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String userId = userDetails != null ? userDetails.getUsername() : null;
        boolean isBookmarked = articleBookmarkService.isBookmarkedByUser(articleId, userId);
        
        return ResponseEntity.ok(Map.of(
            "isBookmarked", isBookmarked
        ));
    }
}

@RequiredArgsConstructor
@RequestMapping("/bookmarks")
@Controller
class BookmarkPageController {

    private final ArticleBookmarkService articleBookmarkService;
    private final PaginationService paginationService;

    @GetMapping
    public String bookmarks(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails,
            ModelMap map) {
        
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        Page<Article> articles = articleBookmarkService.getBookmarkedArticles(userDetails.getUsername(), pageable);
        
        map.addAttribute("articles", articles.map(ArticleDto::from).map(ArticleResponse::from));
        map.addAttribute("paginationBarNumbers", paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages()));
        map.addAttribute("searchTypeHashtag", SearchType.HASHTAG);
        
        return "articles/bookmarks";
    }
} 