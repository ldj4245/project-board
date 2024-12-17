package com.genielee.projectboard.service;


import com.genielee.projectboard.domain.UserAccount;
import com.genielee.projectboard.dto.UserAccountDto;
import com.genielee.projectboard.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;



    @Transactional(readOnly = true)
    public Optional<UserAccountDto> searchUser(String username){
        return userAccountRepository.findById(username)
                .map(UserAccountDto::from);
        //throw 안던지기로 왜냐 이 코드는 인증을 위한 사용자를 가져오는 코드이지만 여기서 처리하기엔 너무 애매하다.
        //입장에 따라 달라지기 때문에 ex)인증의 목적이나 실제 db에서의 목적이 다를 수도 있기 때문이다.

    }

    public UserAccountDto saveUser(String username, String password, String email, String nickname, String memo){
        return UserAccountDto.from(
                userAccountRepository.save(UserAccount.of(username, password, email, nickname, memo,username))
        );
    }
}
