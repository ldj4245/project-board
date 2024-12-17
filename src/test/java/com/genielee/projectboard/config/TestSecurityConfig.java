package com.genielee.projectboard.config;

import com.genielee.projectboard.domain.UserAccount;
import com.genielee.projectboard.dto.UserAccountDto;
import com.genielee.projectboard.repository.UserAccountRepository;
import com.genielee.projectboard.service.UserAccountService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Import(SecurityConfig.class)
public class TestSecurityConfig {

    @MockBean private UserAccountService userAccountService;


    @BeforeTestMethod
    public void securitySetUp(){
        given(userAccountService.searchUser(anyString())).willReturn(Optional.of(createUserAccountDto()));
        given(userAccountService.saveUser(anyString(),anyString(),anyString(),anyString(),anyString()))
                .willReturn(createUserAccountDto());
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "leeTest",
                "pw",
                "lee-test@email.com",
                "lee-test",
                "test memo"

        );
    }

}
