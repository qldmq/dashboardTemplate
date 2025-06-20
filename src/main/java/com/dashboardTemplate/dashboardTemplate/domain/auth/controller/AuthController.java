package com.dashboardTemplate.dashboardTemplate.domain.auth.controller;

import com.dashboardTemplate.dashboardTemplate.domain.auth.dto.SignupRequest;
import com.dashboardTemplate.dashboardTemplate.domain.auth.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody SignupRequest request) {
        log.info("sighup api 진입");

        return authService.signup(request.getCompanyId());
    }
}
