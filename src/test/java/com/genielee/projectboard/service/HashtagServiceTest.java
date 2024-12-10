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

    @InjectMocks
    private HashtagService sut;

    @Mock
    private HashtagRepository hashtagRepository;


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
        Set<String> hashtagNames = Set.of("java", "spring", "boots");
        given(hashtagRepository.findByHashtagNameIn(hashtagNames)).willReturn(List.of(
                Hashtag.of("java"),
                Hashtag.of("spring!!")
        ));
        //when
        Set<Hashtag> hashtags = sut.findHashtagsByNames(hashtagNames);

        hashtags.forEach(it -> {

            System.out.println(it.getHashtagName());
                }
        );


        //then
        assertThat(hashtags).hasSize(2);
        then(hashtagRepository).should().findByHashtagNameIn(hashtagNames);
/**
 *
 * @Override
 * public int hashCode() {
 *     return Objects.hash(this.getId());
 * }
 *id가 null이면 Objects.hash(null)은 0을 반환합니다. 따라서 id가 null인 모든 Hashtag 인스턴스는 hashCode가 0입니다.
 * HashSet에 요소를 추가할 때는 hashCode()와 equals()를 모두 사용합니다:
 *
 * 해시 코드 검사: hashCode()를 이용해 객체가 저장될 버킷을 찾습니다.
 * 동일성 검사: 해당 버킷 내에서 equals()를 이용해 동일한 객체가 이미 있는지 확인합니다.
 * 현재 상황에서는:
 *
 * 두 Hashtag 인스턴스의 hashCode가 모두 0이므로 같은 버킷에 저장됩니다.
 * 그러나 equals() 결과가 false이므로 다른 객체로 인식되어 HashSet에 모두 추가됩니다.
 */
    }



}