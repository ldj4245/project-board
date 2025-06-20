package com.genielee.projectboard.service;

import com.genielee.projectboard.domain.Article;
import com.genielee.projectboard.domain.ArticleComment;
import com.genielee.projectboard.domain.Hashtag;
import com.genielee.projectboard.domain.UserAccount;
import com.genielee.projectboard.dto.ArticleCommentDto;
import com.genielee.projectboard.dto.UserAccountDto;
import com.genielee.projectboard.repository.ArticleCommentRepository;
import com.genielee.projectboard.repository.ArticleRepository;
import com.genielee.projectboard.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스 로직 - 댓글")
@ExtendWith(MockitoExtension.class)
class ArticleCommentServiceTest {

    @InjectMocks private ArticleCommentService sut;

    @Mock private ArticleRepository articleRepository;
    @Mock private ArticleCommentRepository articleCommentRepository;
    @Mock private UserAccountRepository userAccountRepository;

    @DisplayName("게시글 ID로 조회하면, 해당하는 댓글 리스트를 반환한다.")
    @Test
    void givenArticleId_whenSearchingArticleComments_thenReturnsArticleComments() {
        // Given
        Long articleId = 1L;
        ArticleComment expectedParentComment = createArticleComment(1L, "parent content");
        ArticleComment expectedChildComment = createArticleComment(2L, "child content");
        expectedChildComment.setParentCommentId(expectedParentComment.getId());
        given(articleCommentRepository.findByArticle_Id(articleId)).willReturn(List.of(
                expectedParentComment,
                expectedChildComment
        ));

        // When
        List<ArticleCommentDto> actual = sut.searchArticleComments(articleId);

        // Then
        assertThat(actual).hasSize(2);
        assertThat(actual)
                .extracting("id", "articleId", "parentCommentId", "content")
                .containsExactlyInAnyOrder(
                        tuple(1L, 1L, null, "parent content"),
                        tuple(2L, 1L, 1L, "child content")
                );
        then(articleCommentRepository).should().findByArticle_Id(articleId);
    }


    @DisplayName("댓글 정보를 입력하면, 댓글을 저장한다.")
    @Test
    void givenArticleCommentInfo_whenSavingArticleComment_thenSavesArticleComment(){
        // Given
        ArticleCommentDto dto = createArticleCommentDto("댓글");
        given(articleRepository.getReferenceById(dto.articleId())).willReturn(createArticle());
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(createUserAccount());
        given(articleCommentRepository.save(any(ArticleComment.class))).willReturn(null);

        //when
        sut.saveArticleComment(dto);

        //Then
        then(articleRepository).should().getReferenceById(dto.articleId());
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(articleCommentRepository).should(never()).getReferenceById(anyLong());
        then(articleCommentRepository).should().save(any(ArticleComment.class));
    }

    @DisplayName("댓글 저장을 시도했는데 맞는 게시글이 없으면, 경고 로그를 찍고 아무것도 안 한다.")
    @Test
    void givenNonexistentArticle_whenSavingArticleComment_thenLogsSituationAndDoesNothing(){
        //Given
        ArticleCommentDto dto = createArticleCommentDto("댓글");
        given(articleRepository.getReferenceById(dto.articleId())).willThrow(EntityNotFoundException.class);

        //when
        sut.saveArticleComment(dto);

        //Then
        then(articleRepository).should().getReferenceById(dto.articleId());
        then(userAccountRepository).shouldHaveNoInteractions();
        then(articleCommentRepository).shouldHaveNoInteractions();
    }

    @DisplayName("댓글 정보를 입력하면, 댓글을 수정한다.")
    @Test
    void givenArticleCommentInfo_whenUpdatingArticleComment_thenUpdatesArticleComment(){
        //Given
        String oldContent = "content";
        String updatedContent = "댓글";
        ArticleComment articleComment = createArticleComment(1L,oldContent);
        ArticleCommentDto dto = createArticleCommentDto(updatedContent);
        given(articleCommentRepository.getReferenceById(dto.id())).willReturn(articleComment);

        // When
        sut.updateArticleComment(dto);

        //Then
        assertThat(articleComment.getContent())
                .isNotEqualTo(oldContent)
                .isEqualTo(updatedContent);
        then(articleCommentRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("없는 댓글 정보를 수정하려고 하면, 경고 로그를 찍고 아무 것도 안 한다.")
    @Test
    void givenNonexistentArticleInfo_whenUpdatingArticleComment_thenLogsWarningAndDoesNothing(){
        //Given
        ArticleCommentDto dto = createArticleCommentDto("댓글");
        given(articleCommentRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        //when
        sut.updateArticleComment(dto);

        //Then
        then(articleCommentRepository).should().getReferenceById(dto.id());

    }

    @DisplayName("부모 댓글 ID와 댓글 정보를 입력하면, 대댓글을 저장한다.")
    @Test
    void givenParentCommentIdAndArticleCommentInfo_whenSaving_thenSavesChildComment(){
        //given
        Long parentCommentId = 1L;
        ArticleComment parent = createArticleComment(parentCommentId,"댓글");
        ArticleCommentDto child = createArticleCommentDto(parentCommentId,"대댓글");
        //child 에 있는 articleId를 이용해 조회
        given(articleRepository.getReferenceById(child.articleId())).willReturn(createArticle());
        given(userAccountRepository.getReferenceById(child.userAccountDto().userId())).willReturn(createUserAccount());
        //부모로 조회해서 값이 반환되므로 대댓글이 있다는 가정.
        given(articleCommentRepository.getReferenceById(child.parentCommentId())).willReturn(parent);

        //대댓글저장
        //when
        sut.saveArticleComment(child);


        //then
        assertThat(child.parentCommentId()).isNotNull();
        then(articleRepository).should().getReferenceById(child.articleId());
        then(userAccountRepository).should().getReferenceById(child.userAccountDto().userId());
        then(articleCommentRepository).should(never()).save(any(ArticleComment.class));
    }

    @DisplayName("댓글 ID를 입력하면, 댓글을 삭제한다.")
    @Test
    void givenArticleCommentId_whenDeletingArticleComment_thenDeletesArticleComment(){
        //Given
        Long articleCommentId = 1L;
        String userId = "lee";
        willDoNothing().given(articleCommentRepository).deleteByIdAndUserAccount_UserId(articleCommentId,userId);

        //when
        sut.deleteArticleComment(articleCommentId,userId);

        // Then
        then(articleCommentRepository).should().deleteByIdAndUserAccount_UserId(articleCommentId,userId);
    }

    private ArticleCommentDto createArticleCommentDto(String content){
        return createArticleCommentDto(null,content);
    }

    private ArticleCommentDto createArticleCommentDto(Long parentCommentId, String content){
        return createArticleCommentDto(1L,parentCommentId,content);
    }

    private ArticleCommentDto createArticleCommentDto(Long id, Long parentCommentId, String content){
        return ArticleCommentDto.of(
                id,
                1L,
                createUserAccountDto(),
                parentCommentId,
                content,
                LocalDateTime.now(),
                "lee",
                LocalDateTime.now(),
                "lee"

        );
    }
    private UserAccountDto createUserAccountDto(){
        return UserAccountDto.of(
                "lee",
                "password",
                "lee@gmail.com",
                "Lee",
                "THis is memo",
                LocalDateTime.now(),
                "lee",
                LocalDateTime.now(),
                "lee"
        );
    }

    private ArticleComment createArticleComment(Long id, String content){
        ArticleComment articleComment = ArticleComment.of(
                createArticle(),
                createUserAccount(),
                content
        );
        ReflectionTestUtils.setField(articleComment,"id",id);

        return articleComment;
    }

    private UserAccount createUserAccount(){
        return UserAccount.of(
                "lee",
                "password",
                "lee@gmail.com",
                "lee",
                null
        );
    }

    private Article createArticle(){
        Article article = Article.of(
                createUserAccount(),
                "title",
                "content"
        );
        ReflectionTestUtils.setField(article,"id",1L);
    article.addHashtags(Set.of(createHashtag(article)));

    return article;

    }

    private Hashtag createHashtag(Article article){
        return Hashtag.of("java");
    }





}