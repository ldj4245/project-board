package com.genielee.projectboard.service;


import com.genielee.projectboard.domain.Article;
import com.genielee.projectboard.domain.Hashtag;
import com.genielee.projectboard.domain.UserAccount;
import com.genielee.projectboard.domain.constant.SearchType;
import com.genielee.projectboard.dto.ArticleDto;
import com.genielee.projectboard.dto.ArticleWithCommentsDto;
import com.genielee.projectboard.dto.HashtagDto;
import com.genielee.projectboard.repository.ArticleRepository;
import com.genielee.projectboard.repository.HashtagRepository;
import com.genielee.projectboard.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {

    private final HashtagService hashtagService;
    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;
    private final HashtagRepository hashtagRepository;

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable){
        if(searchKeyword == null || searchKeyword.isBlank()){
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }
        return switch (searchType){
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword,pageable).map(ArticleDto::from);
            case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword,pageable).map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from);
            case HASHTAG -> articleRepository.findByHashtagNames(Arrays.stream(searchKeyword.split(" ")).toList(),pageable).map(ArticleDto::from);
        };
    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticleWithComments(Long articleId) {
        return articleRepository.findById(articleId).map(ArticleWithCommentsDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));

    }

    @Transactional(readOnly = true)
    public ArticleDto getArticle(Long articleId){
        return articleRepository.findById(articleId)
                .map(ArticleDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId : " + articleId));
    }


    //저장할때 userAccount 정보를 같이 저장해야해서 dto에서 Entity로 변환로직이 있음
    public void saveArticle(ArticleDto dto){

        UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
        Set<Hashtag> hashtags = renewHashtagsFromContent(dto.content());
        Article article = articleRepository.save(dto.toEntity(userAccount));
        article.addHashtags(hashtags);

    }



    public void updateArticle(Long articleId, ArticleDto dto){
        try{
            Article article = articleRepository.getReferenceById(articleId);
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());

            if(article.getUserAccount().equals(userAccount)){
                if(dto.title() != null){article.setTitle(dto.title());}
                if(dto.content() != null){article.setContent(dto.content());}

                Set<Long>hashtagIds = article.getHashtags().stream()
                        .map(Hashtag::getId)
                        .collect(Collectors.toUnmodifiableSet());

                article.clearHashtags();
                articleRepository.flush(); //delete 쿼리 발생시킴

                hashtagIds.forEach(hashtagService::deleteHashtagWithoutArticles); //forEach로 가져온 id를 이용해 전부 지워주고

                Set<Hashtag> hashtags = renewHashtagsFromContent(dto.content());
                article.addHashtags(hashtags);
            }

        }catch (EntityNotFoundException e){
            log.warn("게시글 업데이트 실패. 게시글을 수정하는데 필요한 정보를 찾을 수 없습니다. - :{}",e.getLocalizedMessage());
        }
    }

    public void deleteArticle(long articleId, String userId){
        Article article = articleRepository.getReferenceById(articleId);
        Set<Long> hashtagIds = article.getHashtags().stream()
                        .map(Hashtag::getId)
                                .collect(Collectors.toUnmodifiableSet());



        articleRepository.deleteByIdAndUserAccount_UserId(articleId,userId);
        articleRepository.flush();


        hashtagIds.forEach(hashtagService::deleteHashtagWithoutArticles);



    }

    public long getArticleCount(){
        return articleRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticlesViaHashtag(String hashtagName, Pageable pageable){
        if(hashtagName == null || hashtagName.isBlank()){
            return Page.empty(pageable);
        }

        return articleRepository.findByHashtagNames(List.of(hashtagName), pageable)
                .map(ArticleDto::from);
    }

    public List<String> getHashtags(){
        return hashtagRepository.findAllHashtagNames(); //TODO HashtagService 로 리팩토링 하기
    }

    private Set<Hashtag> renewHashtagsFromContent(String content) {
        //1. 본문에서 해시태그 뽑아오기
        Set<String> hashtagNamesInContent = hashtagService.parseHashtagNames(content);
        //2. 파싱된 해시태그를 이용해서 db에 검색 (실제로 존재하는 해시태그)
        Set<Hashtag> hashtags = hashtagService.findHashtagsByNames(hashtagNamesInContent);
        //3. 해시태그를 Set<String>으로 뽑아오기 .map(Hashtag::getHashtagName)을 이용해서
        Set<String> existingHashtagNames = hashtags.stream()
                .map(Hashtag::getHashtagName)
                .collect(Collectors.toUnmodifiableSet());

        hashtagNamesInContent.forEach(newHashtagName -> {
            if(!existingHashtagNames.contains(newHashtagName)){
                hashtags.add(Hashtag.of(newHashtagName));
            }
        });

        return hashtags;

    }

}


