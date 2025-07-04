package com.dashboardTemplate.dashboardTemplate.domain.auth.controller;

import com.dashboardTemplate.dashboardTemplate.config.UserDetailsImpl;
import com.dashboardTemplate.dashboardTemplate.domain.auth.dto.LoginRequest;
import com.dashboardTemplate.dashboardTemplate.domain.auth.dto.SignupRequest;
import com.dashboardTemplate.dashboardTemplate.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    // AccessToken 재발급
    @Operation(summary = "AccessToken 재발급", description = "refreshToken을 통해 accessToken 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<Map<String, Object>> reissue(@RequestBody Map<String, String> request) {
        log.info("reissue api 진입");

        String refreshToken = request.get("refreshToken");
        return authService.reissue(refreshToken);
    }

    // 회원가입
    @Operation(summary = "회원가입", description = "companyId를 받아 회원가입 처리")
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody SignupRequest request) {
        log.info("signup api 진입");

        return authService.signup(request.getCompanyId(), request.getCompany(), request.getCompanyEng());
    }
    
    // 로그인
    @Operation(summary = "로그인", description = "companyId를 입력해서 처리")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        log.info("login api 진입");

        return authService.login(request.getCompanyId());
    }
    
    // 로그아웃
    @Operation(summary = "로그아웃", description = "Redis에서 RefreshToken을 삭제하고 accessToken은 blackList에 등록해서 즉시 로그아웃 처리")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader("Authorization") String accessToken, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("logout api 진입");

        String companyId = userDetails.getAuth().getCompanyId();

        return authService.logout(accessToken, companyId);
    }
}
