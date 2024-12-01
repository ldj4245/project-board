package com.genielee.projectboard.config;

import com.genielee.projectboard.domain.UserAccount;
import com.genielee.projectboard.repository.UserAccountRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Import(SecurityConfig.class)
public class TestSecurityConfig {

    @MockBean private UserAccountRepository userAccountRepository;


    @BeforeTestMethod
    public void securitySetUp(){
        given(userAccountRepository.findById(anyString())).willReturn(Optional.of(UserAccount.of(
                "leeTest",
                "pw",
                "lee-test@email.com",
                "lee-test",
                "test memo"
        )));




    }

}
