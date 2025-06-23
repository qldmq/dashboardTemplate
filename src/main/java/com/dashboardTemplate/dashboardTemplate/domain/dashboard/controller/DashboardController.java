package com.dashboardTemplate.dashboardTemplate.domain.dashboard.controller;

import com.dashboardTemplate.dashboardTemplate.config.UserDetailsImpl;
import com.dashboardTemplate.dashboardTemplate.domain.dashboard.dto.CreateRequestDto;
import com.dashboardTemplate.dashboardTemplate.domain.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Operation(summary = "대시보드 생성", description = "입력받은 데이터로 대시보드 생성")
    @PostMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> createDashboard(@RequestBody CreateRequestDto request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("createDashboard api 진입");

        String dashboardName = request.getDashboardName();
        String databaseName = request.getDatabaseName();
        String dashboardDescription = request.getDashboardDescription();
        int companyNum = userDetails.getAuth().getCompanyNum();

        log.info("dashboardName: {}, databaseName: {}, dashboardDescription: {}", dashboardName, databaseName, dashboardDescription);

        return dashboardService.createDashboard(dashboardName, databaseName, dashboardDescription, companyNum);
    }
}
