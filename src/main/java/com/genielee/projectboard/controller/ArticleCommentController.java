package com.genielee.projectboard.controller;

import com.genielee.projectboard.dto.request.ArticleCommentRequest;
import com.genielee.projectboard.dto.security.BoardPrincipal;
import com.genielee.projectboard.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/comments")
@Controller
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;


    //th:name = content
    //articleCommentRequest 내부 string content와 바인딩
    @PostMapping("/new")
    public String postNewArticleComment(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            ArticleCommentRequest articleCommentRequest){
        articleCommentService.saveArticleComment(articleCommentRequest.toDto(boardPrincipal.toDto()
        ));


       return "redirect:/articles/" + articleCommentRequest.articleId();
    }

    @PostMapping("/{commentId}/delete")
    public String deleteArticleComment(@PathVariable Long commentId,
                                       @AuthenticationPrincipal BoardPrincipal boardPrincipal,
                                       Long articleId){
        articleCommentService.deleteArticleComment(commentId, boardPrincipal.getUsername());

        return "redirect:/articles/" + articleId;
    }



}
