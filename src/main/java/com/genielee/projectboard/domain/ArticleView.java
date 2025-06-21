package com.genielee.projectboard.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
@Table(
    indexes = {
        @Index(columnList = "articleId"),
        @Index(columnList = "viewerIp"),
        @Index(columnList = "userId"),
        @Index(columnList = "createdAt")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"articleId", "viewerIp", "userId"})
    }
)
@Entity
public class ArticleView extends AuditingFields {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JoinColumn(name = "articleId", nullable = false)
    @ManyToOne(optional = false)
    private Article article;

    @Setter
    @Column(length = 45) // IPv6 최대 길이
    private String viewerIp;

    @Setter
    @Column(length = 50)
    private String userId; // 로그인 사용자의 경우

    protected ArticleView() {}

    private ArticleView(Article article, String viewerIp, String userId) {
        this.article = article;
        this.viewerIp = viewerIp;
        this.userId = userId;
    }

    public static ArticleView of(Article article, String viewerIp, String userId) {
        return new ArticleView(article, viewerIp, userId);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ArticleView that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
} 