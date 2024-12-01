package com.genielee.projectboard.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdBy"),
        @Index(columnList = "createdAt")
})
@Entity
public class ArticleComment extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Setter @ManyToOne(optional = false) private Article article; //게시글 (ID)
    @Setter @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private UserAccount userAccount; //유저 정보 (ID)
    @Setter @Column(nullable = false,length = 500) private String content; //본문


    protected ArticleComment() {}

    private ArticleComment(Article article,UserAccount userAccount, String content) {
        this.article = article;
        this.userAccount = userAccount;
        this.content = content;
    }

    public static ArticleComment of(Article article,UserAccount userAccount, String content) {
        return new ArticleComment(article,userAccount, content);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ArticleComment that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
