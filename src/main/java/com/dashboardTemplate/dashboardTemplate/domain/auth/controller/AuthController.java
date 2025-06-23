package com.dashboardTemplate.dashboardTemplate.domain.auth.controller;

import com.dashboardTemplate.dashboardTemplate.domain.auth.dto.LoginRequest;
import com.dashboardTemplate.dashboardTemplate.domain.auth.dto.SignupRequest;
import com.dashboardTemplate.dashboardTemplate.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        log.info("signup api 진입");

        return authService.signup(request.getCompanyId(), request.getCompany());
    }
    
    // 로그인
    @Operation(summary = "로그인", description = "companyId를 입력해서 처리")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        log.info("login api 진입");

        return authService.login(request.getCompanyId());
    }
}
