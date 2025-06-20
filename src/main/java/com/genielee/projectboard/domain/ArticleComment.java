package com.genielee.projectboard.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

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


    @Setter
    @ManyToOne(optional = false)
    private Article article; //게시글 (ID)

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private UserAccount userAccount; //유저 정보 (ID)

    @Setter
    @Column(updatable = false)
    private Long parentCommentId;

    @OrderBy("createdAt ASC")
    @OneToMany(mappedBy = "parentCommentId",cascade = CascadeType.ALL)
    private Set<ArticleComment> childComments = new LinkedHashSet<>();


    @Setter @Column(nullable = false,length = 500) private String content; //본문


    protected ArticleComment() {}

    private ArticleComment(Article article,UserAccount userAccount, Long parentCommentId, String content) {
        this.article = article;
        this.userAccount = userAccount;
        this.parentCommentId = parentCommentId;
        this.content = content;
    }

    public static ArticleComment of(Article article,UserAccount userAccount, String content) {
        return new ArticleComment(article,userAccount,null, content);
    }

    public void setChildComments(ArticleComment child){
        child.setParentCommentId(this.getId());
        this.getChildComments().add(child);
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
