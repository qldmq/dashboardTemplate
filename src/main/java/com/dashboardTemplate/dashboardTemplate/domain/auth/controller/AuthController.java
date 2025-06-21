package com.dashboardTemplate.dashboardTemplate.domain.auth.controller;

import com.dashboardTemplate.dashboardTemplate.domain.auth.dto.SignupRequest;
import com.dashboardTemplate.dashboardTemplate.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 회원가입
    @Operation(summary = "회원가입", description = "companyId를 받아 회원가입 처리")
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody SignupRequest request) {
        log.info("sighup api 진입");

        return authService.signup(request.getCompanyId());
    }
    
    // 로그인
}
