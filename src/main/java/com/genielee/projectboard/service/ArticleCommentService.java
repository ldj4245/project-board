package com.genielee.projectboard.service;


import com.genielee.projectboard.domain.Article;
import com.genielee.projectboard.domain.ArticleComment;
import com.genielee.projectboard.domain.UserAccount;
import com.genielee.projectboard.dto.ArticleCommentDto;
import com.genielee.projectboard.repository.ArticleCommentRepository;
import com.genielee.projectboard.repository.ArticleRepository;
import com.genielee.projectboard.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleCommentService {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserAccountRepository userAccountRepository;

    //해당 게시글에 있는 모든 댓글을 조회한다.
    @Transactional(readOnly = true)
    public List<ArticleCommentDto> searchArticleComments(Long articleId){
        return articleCommentRepository.findByArticle_Id(articleId)
                .stream()
                .map(ArticleCommentDto::from)
                .toList();

    }
    //댓글 저장시 게시물이 존재하는지 여부, 유저 정보 같이 저장. dto 에 들어있어야 함
    public void saveArticleComment(ArticleCommentDto dto){
        try{
            //dto로 articeId를 넘겨받아 article 조회
            Article article = articleRepository.getReferenceById(dto.articleId());
            //dto로 userAccountuserId를 넘겨받아 UserAccount조회
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
            //dto에 parentId까지 넘어왔다면 대댓글 아니면 그냥 댓글
            // dto.toEntity ==> 저장하기위해선 엔티티로 변환해야함.
            //처음엔 parentId가 null로 생성됨
            ArticleComment articleComment = dto.toEntity(article,userAccount);



           /*
            연관관계 메서드 참고
            public void setChildComments(ArticleComment child){
                child.setParentCommentId(this.getId());
                this.getChildComments().add(child);
            }
           */
            //대댓글이 존재하는 경우
            if(dto.parentCommentId() != null) {
                ArticleComment parentComment = articleCommentRepository.getReferenceById(dto.parentCommentId());
                //여기서 id 지정이 됨
                parentComment.setChildComments(articleComment);
                //child에 parentCommentId 지정 및  this.getChildComments().add(child)로 대댓글을 지정해줌.
            }else{
                //그게 아니라면 articleComment 저장
                //다시 한번 말하지만 articleComment는 parentId는 처음엔 null
                articleCommentRepository.save(articleComment);
            }


        }catch (EntityNotFoundException e){
            log.warn("댓글 저장 실패. 댓글 작성에 필요한 정보를 찾을 수 없습니다 - {}", e.getLocalizedMessage());
        }
    }

    public void updateArticleComment(ArticleCommentDto dto){
        try{
            ArticleComment articleComment = articleCommentRepository.getReferenceById(dto.id());
            if(dto.content() != null){ articleComment.setContent(dto.content());}
        }catch (EntityNotFoundException e){
            log.warn("댓글 업데이트 실패. 댓글을 찾을 수 없습니다 - dto: {}",dto);
        }

    }

    public void deleteArticleComment(Long articleCommentId, String userId){
        articleCommentRepository.deleteByIdAndUserAccount_UserId(articleCommentId,userId);
    }

}