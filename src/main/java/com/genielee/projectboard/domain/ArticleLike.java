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
        @Index(columnList = "userId"),
        @Index(columnList = "createdAt")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"articleId", "userId"})
    }
)
@Entity
public class ArticleLike extends AuditingFields {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JoinColumn(name = "articleId", nullable = false)
    @ManyToOne(optional = false)
    private Article article;

    @Setter
    @JoinColumn(name = "userId", nullable = false)
    @ManyToOne(optional = false)
    private UserAccount userAccount;

    protected ArticleLike() {}

    private ArticleLike(Article article, UserAccount userAccount) {
        this.article = article;
        this.userAccount = userAccount;
    }

    public static ArticleLike of(Article article, UserAccount userAccount) {
        return new ArticleLike(article, userAccount);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ArticleLike that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
} 