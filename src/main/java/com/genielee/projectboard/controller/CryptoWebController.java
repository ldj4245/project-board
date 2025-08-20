package com.genielee.projectboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 암호화폐 관련 웹 페이지 컨트롤러
 */
@Controller
public class CryptoWebController {
    
    /**
     * 암호화폐 정보센터 페이지를 반환합니다
     */
    @GetMapping("/crypto-center")
    public String cryptoCenter() {
        return "crypto-center";
    }
}