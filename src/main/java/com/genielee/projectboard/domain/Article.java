package com.genielee.projectboard.domain;

import com.genielee.projectboard.domain.constant.BoardCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;


@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList="title"),
        @Index(columnList = "createdBy"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "category"),
        @Index(columnList = "coinSymbol"),
        @Index(columnList = "isPinned"),
        @Index(columnList = "isHot"),
        @Index(columnList = "viewCount"),
        @Index(columnList = "category, createdAt"),
        @Index(columnList = "coinSymbol, createdAt")
})
@Entity
public class Article extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JoinColumn(name="userId")
    @ManyToOne(optional = false)
    private UserAccount userAccount; // 유저 정보 (ID)

    @Setter @Column(nullable = false) private String title; //제목
    @Setter @Column(nullable = false, length = 10000) private String content; //내용

    @Setter @Getter(AccessLevel.NONE) @Column(columnDefinition = "BIGINT DEFAULT 0") private Long viewCount = 0L; // 조회수

    // 코인판 스타일 확장 필드들
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(length = 20, columnDefinition = "varchar(20) default 'GENERAL'")
    private BoardCategory category = BoardCategory.GENERAL; // 게시판 카테고리

    @Setter
    @Column(length = 20)
    private String coinSymbol; // 관련 코인 심볼 (BTC, ETH 등)

    @Setter
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isPinned = false; // 공지글 여부

    @Setter
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isHot = false; // 인기글 여부 (조회수/댓글 기준)

    @ToString.Exclude
    @JoinTable(
            name = "article_hashtag",
            joinColumns = @JoinColumn(name = "articleId"),
            inverseJoinColumns = @JoinColumn(name = "hashtagId")
    )
    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private Set<Hashtag> hashtags = new LinkedHashSet<>();



    @ToString.Exclude
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    protected Article() {}

    private Article(UserAccount userAccount,String title, String content) {
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
    }

    public static Article of(UserAccount userAccount,String title, String content) {
        return new Article(userAccount,title, content);
    }

    public void addHashtag(Hashtag hashtag){
        this.getHashtags().add(hashtag);
    }

    public void addHashtags(Collection<Hashtag> hashtags){
        this.getHashtags().addAll(hashtags);
    }

    public void clearHashtags(){
        this.getHashtags().clear();
    }

    public void incrementViewCount() {
        if (this.viewCount == null) {
            this.viewCount = 0L;
        }
        this.viewCount++;
    }
    
    public Long getViewCount() {
        return this.viewCount != null ? this.viewCount : 0L;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Article article)) return false;
        return Objects.equals(getId(), article.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
