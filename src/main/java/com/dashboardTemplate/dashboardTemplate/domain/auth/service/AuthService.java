package com.dashboardTemplate.dashboardTemplate.domain.auth.service;

import com.dashboardTemplate.dashboardTemplate.config.JwtTokenProvider;
import com.dashboardTemplate.dashboardTemplate.domain.auth.entity.Auth;
import com.dashboardTemplate.dashboardTemplate.domain.auth.repository.AuthRepository;
import com.dashboardTemplate.dashboardTemplate.domain.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final CompanyService companyService;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입
    public ResponseEntity<Map<String, Object>> signup(String companyId, String company) {

        Map<String, Object> responseMap = new HashMap<>();

        try {
            if (authRepository.existsByCompanyId(companyId)) {
                responseMap.put("message", "이미 존재하는 아이디입니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
            }

            if (authRepository.existsByCompany(company)) {
                responseMap.put("message", "이미 존재하는 회사입니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
            }

            Auth auth = new Auth();
            auth.setCompanyId(companyId);
            auth.setCompany(company);
            authRepository.save(auth);

            responseMap.put("message", "회원가입이 완료되었습니다.");
            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        } catch (Exception e) {
            responseMap.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }

    // 로그인
    public ResponseEntity<Map<String, Object>> login(String companyId) {

        Map<String, Object> responseMap = new HashMap<>();
        Optional<Auth> companyOptional = authRepository.findByCompanyId(companyId);

        try {
            if (companyOptional.isEmpty()) {
                responseMap.put("message", "사용자를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
            }

            String company = companyOptional.get().getCompany();

            List<String> tableNamesList = companyService.getTableNamesByCompany(company);
            String accessToken = jwtTokenProvider.createAccessToken(companyId);
            String refreshToken = jwtTokenProvider.createRefreshToken(companyId);

            responseMap.put("company", company);
            responseMap.put("accessToken", accessToken);
            responseMap.put("refreshToken", refreshToken);
            responseMap.put("tableNamesList", tableNamesList);
            responseMap.put("message", "로그인 성공");

            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        } catch (Exception e) {
            responseMap.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }
}
