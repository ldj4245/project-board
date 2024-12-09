package com.genielee.projectboard.service;


import com.genielee.projectboard.domain.Hashtag;
import com.genielee.projectboard.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    public Set<String> parseHashtagNames(String content){
        if(content == null){
            return Set.of();
        }

        Pattern pattern = Pattern.compile("#[\\w가-힣]+");
        Matcher matcher = pattern.matcher(content.strip());
        Set<String> result = new HashSet<>();

        while(matcher.find()){
            result.add(matcher.group().replace("#",""));
        }



        return Set.copyOf(result); //불변으로 만들어주는 기능
    }

    public Set<Hashtag> findHashtagsByNames(Set<String> hashtagNames){
        return new HashSet<>(hashtagRepository.findByHashtagNameIn(hashtagNames));
        //new HashSet덕분에 return값이 List지만 set으로 반환할 수 있다.
    }


    //글이 삭제되면 해시태그 까지 삭제가 되면 안됨.
    //다른 글에서 등장할 수 있잖아
    //모든 게시글에서 지워졌을 때 비로소 해시태그가 빠져야됨
    public void deleteHashtagWithoutArticles(Long hashtagId){
        Hashtag hashtag = hashtagRepository.getReferenceById(hashtagId);
        //해시태그 조회시 (양방향) 비었다면
        //그제서야 삭제를 함.
        if(hashtag.getArticles().isEmpty()){
            hashtagRepository.delete(hashtag);
        }

    }
}
