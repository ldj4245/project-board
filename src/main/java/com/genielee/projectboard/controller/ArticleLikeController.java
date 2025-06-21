package com.genielee.projectboard.controller;

import com.genielee.projectboard.service.ArticleLikeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/articles")
@RestController
public class ArticleLikeController {

    private final ArticleLikeService articleLikeService;

    @PostMapping("/{articleId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long articleId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        boolean isLiked = articleLikeService.toggleLike(articleId, userDetails.getUsername());
        long likeCount = articleLikeService.getLikeCount(articleId);
        
        return ResponseEntity.ok(Map.of(
            "isLiked", isLiked,
            "likeCount", likeCount
        ));
    }

    @GetMapping("/{articleId}/like")
    public ResponseEntity<Map<String, Object>> getLikeStatus(
            @PathVariable Long articleId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String userId = userDetails != null ? userDetails.getUsername() : null;
        boolean isLiked = articleLikeService.isLikedByUser(articleId, userId);
        long likeCount = articleLikeService.getLikeCount(articleId);
        
        return ResponseEntity.ok(Map.of(
            "isLiked", isLiked,
            "likeCount", likeCount
        ));
    }
} 