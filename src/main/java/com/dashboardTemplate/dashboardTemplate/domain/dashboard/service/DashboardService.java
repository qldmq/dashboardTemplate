package com.dashboardTemplate.dashboardTemplate.domain.dashboard.service;

import com.dashboardTemplate.dashboardTemplate.domain.dashboard.entity.Dashboard;
import com.dashboardTemplate.dashboardTemplate.domain.dashboard.entity.DashboardStatus;
import com.dashboardTemplate.dashboardTemplate.domain.dashboard.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<Map<String, Object>> createDashboard(String dashboardName, String databaseName, String dashboardDescription, int companyNum) {

        Map<String, Object> responseMap = new HashMap<>();

        int randNum = secureRandom.nextInt(900000) + 100000;
        String id = passwordEncoder.encode(String.valueOf(randNum));

        Dashboard dashboard = Dashboard.builder()
                .dashboardId(id)
                .companyNum(companyNum)
                .dashboardName(dashboardName)
                .databaseName(databaseName)
                .dashboardDescription(dashboardDescription)
                .dashboardStatus(DashboardStatus.CREATED)
                .createdAt(LocalDate.now())
                .updatedAt(null)
                .build();

        dashboardRepository.save(dashboard);

        responseMap.put("message", "저장이 완료되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(responseMap);
    }
}
