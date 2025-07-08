package com.dashboardTemplate.dashboardTemplate.domain.dashboard.controller;

import com.dashboardTemplate.dashboardTemplate.config.UserDetailsImpl;
import com.dashboardTemplate.dashboardTemplate.domain.dashboard.dto.CreateRequestDto;
import com.dashboardTemplate.dashboardTemplate.domain.dashboard.dto.DeleteDto;
import com.dashboardTemplate.dashboardTemplate.domain.dashboard.dto.UpdateDashboardDto;
import com.dashboardTemplate.dashboardTemplate.domain.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        String tableName = request.getTableName();
        String dashboardDescription = request.getDashboardDescription();
        int companyNum = userDetails.getAuth().getCompanyNum();

        log.info("dashboardName: {}, tableName: {}, dashboardDescription: {}", dashboardName, tableName, dashboardDescription);

        return dashboardService.createDashboard(dashboardName, tableName, dashboardDescription, companyNum);
    }

    @Operation(summary = "대시보드 조회 (페이지네이션)", description = "companyId로 본인이 만든 대시보드를 페이지네이션을 통해 리스트로 조회")
    @GetMapping("/dashboards")
    public ResponseEntity<Map<String, Object>> checkDashboardList (@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                   @RequestParam(defaultValue = "1") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        log.info("checkDashboard api 진입");

        int companyNum = userDetails.getAuth().getCompanyNum();
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return dashboardService.checkDashboardList(companyNum, pageable);
    }

    @Operation(summary = "상세 대시보드 조회", description = "dashboardId와 status를 입력받아 해당하는 대시보드를 조회")
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> checkDashboardDetails(@RequestParam String dashboardId, @RequestParam String status) {

        log.info("checkDashboardDetails api 진입");

        return dashboardService.checkDashboardDetails(dashboardId, status);
    }

    @Operation(summary = "대시보드 수정", description = "대시보드 내용 수정")
    @PatchMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> updateDashboard(@RequestBody UpdateDashboardDto updateDashboardDto) {
        
        log.info("updateDashboard api 진입");

        String dashboardId = updateDashboardDto.getDashboardId();
        UpdateDashboardDto.GroupDataDto groupDataDto = updateDashboardDto.getGroupData();
        UpdateDashboardDto.AggregatedDataDto aggregatedDataDto = updateDashboardDto.getAggregatedData();

        return dashboardService.updateDashboard(dashboardId, groupDataDto, aggregatedDataDto);
    }

    @Operation(summary = "대시보드 삭제", description = "대시보드 삭제")
    @DeleteMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> deleteMapping(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody DeleteDto deleteDto) {

        log.info("deleteDashboard api 진입");

        int companyNum = userDetails.getAuth().getCompanyNum();
        String dashboardId = deleteDto.getDashboardId();

        return dashboardService.deleteDashboard(companyNum, dashboardId );
    }
    
    @Operation(summary = "그룹데이터 조회", description = "그룹데이터 조회")
    @GetMapping("/groupData")
    public ResponseEntity<Map<String, Object>> getDashboardGroupData(@Parameter(name = "columnName", description = "조회할 컬럼명", example = "age") @RequestParam String columnName,
                                                                     @Parameter(name = "tableName", description = "조회할 테이블명", example = "test_phone") @RequestParam String tableName) {

        log.info("getDashboardGroupData api 진입");

        return dashboardService.dashboardGroupData(columnName, tableName);
    }
}
