package com.genielee.projectboard.service;

import com.genielee.projectboard.domain.Hashtag;
import com.genielee.projectboard.repository.HashtagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@DisplayName("비즈니스 로직 - 해시태그")
@ExtendWith(MockitoExtension.class)
class HashtagServiceTest {

    @InjectMocks private HashtagService sut;

    @Mock private HashtagRepository hashtagRepository;


    @DisplayName("본문을 파싱하면, 해시태그이름들을 중복 없이 반환한다.")
    @MethodSource
    @ParameterizedTest(name = "[{index}] \"{0}\" => {1}" )
    void givenContent_whenParsing_thenReturnsUniqueHashtagNames(String input, Set<String> expected){
        //given

        //when
        Set<String> actual = sut.parseHashtagNames(input);

        //then
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
        then(hashtagRepository).shouldHaveNoInteractions();

    }

    static Stream<Arguments> givenContent_whenParsing_thenReturnsUniqueHashtagNames() {
        return Stream.of(
                arguments(null, Set.of()),
                arguments("", Set.of()),
                arguments("   ", Set.of()),
                arguments("#", Set.of()),
                arguments("  #", Set.of()),
                arguments("#   ", Set.of()),
                arguments("apple", Set.of()),
                arguments("apple#", Set.of()),
                arguments("ap#ple", Set.of("ple")),
                arguments("#apple", Set.of("apple")),
                arguments("#apple_banana", Set.of("apple_banana")),
                arguments("#apple-banana", Set.of("apple")),
                arguments("#_apple_banana", Set.of("_apple_banana")),
                arguments("#-apple-banana", Set.of()),
                arguments("#_apple_banana__", Set.of("_apple_banana__")),
                arguments("#apple#banana", Set.of("apple", "banana")),
                arguments("#apple #banana", Set.of("apple", "banana")),
                arguments("#apple  #banana", Set.of("apple", "banana")),
                arguments("#apple   #banana", Set.of("apple", "banana")),
                arguments("#apple     #banana", Set.of("apple", "banana")),
                arguments("  #apple     #banana ", Set.of("apple", "banana")),
                arguments("   #apple     #banana   ", Set.of("apple", "banana")),
                arguments("#apple#banana#carrot", Set.of("apple", "banana", "carrot")),
                arguments("#apple #banana#carrot", Set.of("apple", "banana", "carrot")),
                arguments("#apple#banana #carrot", Set.of("apple", "banana", "carrot")),
                arguments("#apple,#banana,#carrot", Set.of("apple", "banana", "carrot")),
                arguments("#apple.#banana;#carrot", Set.of("apple", "banana", "carrot")),
                arguments("#apple|#banana:#carrot", Set.of("apple", "banana", "carrot")),
                arguments("#apple #banana  #carrot", Set.of("apple", "banana", "carrot")),
                arguments("   #apple,? #banana  ...  #carrot ", Set.of("apple", "banana", "carrot")),
                arguments("#apple#apple#banana#carrot", Set.of("apple", "banana", "carrot")),
                arguments("#apple#apple#apple#banana#carrot", Set.of("apple", "banana", "carrot")),
                arguments("#apple#banana#apple#carrot#apple", Set.of("apple", "banana", "carrot")),
                arguments("#apple#banana 아주 긴 글~~~~~~~~~~~~~~~~~~~~~", Set.of("apple", "banana")),
                arguments("아주 긴 글~~~~~~~~~~~~~~~~~~~~~#apple#banana", Set.of("apple", "banana")),
                arguments("아주 긴 글~~~~~~#apple#banana~~~~~~~~~~~~~~~", Set.of("apple", "banana")),
                arguments("아주 긴 글~~~~~~#apple~~~~~~~#banana~~~~~~~~", Set.of("apple", "banana"))
        );
    }

    @DisplayName("해시태그 이름을 입력하면, 저장된 해시태그 중 이름에 매칭하는 것들을 중복 없이 반환한다.")
    @Test
    void givenHashtagNames_whenFindingHashtags_thenReturnsHashtagSet(){
        //given
        Set<String> hashtagNames = Set.of("java","spring","boots");
        given(hashtagRepository.findByHashtagNameIn(hashtagNames)).willReturn(List.of(
                Hashtag.of("java"),
                Hashtag.of("spring")
        ));
        //when
        Set<Hashtag> hashtags = sut.findHashtagsByNames(hashtagNames);
        //then
        assertThat(hashtags).hasSize(2);
        then(hashtagRepository).should().findByHashtagNameIn(hashtagNames);

    }



}