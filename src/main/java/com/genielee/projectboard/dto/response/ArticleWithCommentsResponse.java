package com.genielee.projectboard.dto.response;

import com.genielee.projectboard.dto.ArticleCommentDto;
import com.genielee.projectboard.dto.ArticleWithCommentsDto;
import com.genielee.projectboard.dto.HashtagDto;
import org.springframework.context.annotation.ScopeMetadata;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ArticleWithCommentsResponse(
        Long id,
        String title,
        String content,
        Set<String> hashtags,
        LocalDateTime createdAt,
        String email,
        String nickname,
        String userId,
        Set<ArticleCommentResponse> articleCommentsResponse
) {
    public static ArticleWithCommentsResponse of(Long id, String title, String content, Set<String> hashtag, LocalDateTime createdAt,
                                                 String email, String nickname, String userId, Set<ArticleCommentResponse> articleCommentResponse){
        return new ArticleWithCommentsResponse(id, title, content, hashtag, createdAt, email, nickname, userId, articleCommentResponse);
    }

    public static ArticleWithCommentsResponse from(ArticleWithCommentsDto dto){
        String nickname = dto.userAccountDto().nickname();

        if(nickname == null || nickname.isBlank()){
            nickname = dto.userAccountDto().userId();
        }


        return new ArticleWithCommentsResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtagDtos().stream().map(HashtagDto::hashtagName)
                        .collect(Collectors.toUnmodifiableSet())
                ,
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname,
                dto.userAccountDto().userId(),
                organizeChildComments(dto.articleCommentDtos())
        );
    }

    private static Set<ArticleCommentResponse> organizeChildComments(Set<ArticleCommentDto> dtos){
        //set은 데이터에 접근할 수 있는 방법을 제공하지 않는다 즉 순서를 보장하지 않는다는 뜻
        //데이터 접근이 용이하게 Map으로 변환한다.
        Map<Long,ArticleCommentResponse> map = dtos.stream()
                .map(ArticleCommentResponse::from)
                .collect(Collectors.toMap(ArticleCommentResponse::id, Function.identity()));
        //1. 데이터 구조 변경


        //articleCommentResponse를 순회하면서
        //parent에 child에 추가를 한다. (hasParentComment(부모를 갖고 있냐) <== child comment
        map.values().stream()
                .filter(ArticleCommentResponse::hasParentComment)
                .forEach(comment -> {
                    ArticleCommentResponse parentComment = map.get(comment.parentCommentId()); //자식에 있는 parentId를 통해 부모 꺼내옴
                    parentComment.childComments().add(comment);
                });


        return map.values().stream()
                .filter(comment -> !comment.hasParentComment())
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator
                                .comparing(ArticleCommentResponse::createdAt)
                                .reversed()
                                .thenComparing(ArticleCommentResponse::id)
                        )));


    }
}
