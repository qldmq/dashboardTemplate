package com.dashboardTemplate.dashboardTemplate.domain.auth.service;

import com.dashboardTemplate.dashboardTemplate.domain.auth.entity.Auth;
import com.dashboardTemplate.dashboardTemplate.domain.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;

    // 회원가입
    public ResponseEntity<Map<String, Object>> signup(String companyId) {

        Map<String, Object> responseMap = new HashMap<>();

        try {
            if (authRepository.existsByCompanyId(companyId)) {
                responseMap.put("message", "이미 존재하는 아이디입니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
            }

            Auth auth = new Auth();
            auth.setCompanyId(companyId);
            authRepository.save(auth);

            responseMap.put("message", "회원가입이 완료되었습니다.");
            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        } catch (Exception e) {
            responseMap.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }
}
