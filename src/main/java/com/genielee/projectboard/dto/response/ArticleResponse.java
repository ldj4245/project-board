package com.genielee.projectboard.dto.response;

import com.genielee.projectboard.dto.ArticleDto;
import com.genielee.projectboard.dto.HashtagDto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleResponse(
        Long id,
        String title,
        String content,
        Set<String> hashtags,
        LocalDateTime createdAt,
        String email,
        String nickname,
        Long viewCount) {

    public static ArticleResponse of(Long id, String title, String content, Set<String> hashtag, LocalDateTime createdAt, String email, String nickname, Long viewCount) {
        return new ArticleResponse(id, title, content, hashtag, createdAt, email, nickname, viewCount);
    }

    public static ArticleResponse from(ArticleDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtagDtos().stream()
                        .map(HashtagDto::hashtagName)
                        .collect(Collectors.toUnmodifiableSet()),
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname,
                dto.viewCount()
        );
    }


}
