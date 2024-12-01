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
            Article article = articleRepository.getReferenceById(dto.articleId());
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
            articleCommentRepository.save(dto.toEntity(article,userAccount));
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