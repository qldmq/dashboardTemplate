package com.dashboardTemplate.dashboardTemplate.domain.auth.service;

import com.dashboardTemplate.dashboardTemplate.config.JwtTokenProvider;
import com.dashboardTemplate.dashboardTemplate.domain.auth.entity.Auth;
import com.dashboardTemplate.dashboardTemplate.domain.auth.repository.AuthRepository;
import com.dashboardTemplate.dashboardTemplate.domain.JDBC.service.JDBCService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final JDBCService JDBCService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    // 토큰 재발급
    public ResponseEntity<Map<String, Object>> reissue(String refreshToken) {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                responseMap.put("message", "RefreshToken이 유효하지 않습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
            }

            String companyId = jwtTokenProvider.getCompanyIdFromToken(refreshToken);
            String redisRefreshToken = redisTemplate.opsForValue().get("RT:" + companyId);

            if (redisRefreshToken == null) {
                responseMap.put("message", "로그인 정보가 없습니다. 다시 로그인해주세요.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
            }

            if (!redisRefreshToken.equals(refreshToken)) {
                responseMap.put("message", "RefreshToken이 일치하지 않습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
            }

            String newAccessToken = jwtTokenProvider.createAccessToken(companyId);
            Date expiration = jwtTokenProvider.getExpirationDateFromToken(newAccessToken);
            long expirationTime = expiration.getTime();

            responseMap.put("accessToken", newAccessToken);
            responseMap.put("accessTokenExpiresAt", expirationTime);
            responseMap.put("message", "토큰이 재발급되었습니다.");

            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        } catch (Exception e) {
            responseMap.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }

    // 회원가입
    public ResponseEntity<Map<String, Object>> signup(String companyId, String company, String companyEng) {

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
            auth.setCompanyEng(companyEng);
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

            String companyEng = companyOptional.get().getCompanyEng();
            String company = companyOptional.get().getCompany();

            List<String> tableNamesList = JDBCService.getTableNamesByCompany(companyEng);
            String accessToken = jwtTokenProvider.createAccessToken(companyId);
            String refreshToken = jwtTokenProvider.createRefreshToken(companyId);

            redisTemplate.opsForValue().set(
                    "RT:" + companyId,
                    refreshToken,
                    refreshTokenValidity,
                    TimeUnit.MILLISECONDS
            );

            Date expiration = jwtTokenProvider.getExpirationDateFromToken(accessToken);
            long expirationTime = expiration.getTime();

            responseMap.put("company", company);
            responseMap.put("accessToken", accessToken);
            responseMap.put("refreshToken", refreshToken);
            responseMap.put("tableNamesList", tableNamesList);
            responseMap.put("accessTokenExpiresAt", expirationTime);
            responseMap.put("message", "로그인 성공");

            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        } catch (Exception e) {
            responseMap.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }
    
    // 로그아웃
    public ResponseEntity<Map<String, Object>> logout(String accessToken, String companyId) {

        Map<String, Object> responseMap = new HashMap<>();

        try {
            String redisKey = "RT:" + companyId;
            if (redisTemplate.hasKey(redisKey)) {
                redisTemplate.delete(redisKey);
            }

            if (accessToken != null && accessToken.startsWith("Bearer ")) {
                accessToken = accessToken.substring(7);
            }

            if (jwtTokenProvider.validateToken(accessToken)) {
                Date expiration = jwtTokenProvider.getExpirationDateFromToken(accessToken);
                long now = System.currentTimeMillis();
                long remainingTime = expiration.getTime() - now;

                if (remainingTime > 0) {
                    redisTemplate.opsForValue().set(
                            "BL:" + accessToken,
                            "logout",
                            remainingTime,
                            TimeUnit.MILLISECONDS
                    );
                }
            }

            responseMap.put("message", "로그아웃되었습니다.");
            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        } catch (Exception e) {
            responseMap.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }
}
