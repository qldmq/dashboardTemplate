package com.dashboardTemplate.dashboardTemplate.domain.dashboard.service;

import com.dashboardTemplate.dashboardTemplate.domain.JDBC.service.JDBCService;
import com.dashboardTemplate.dashboardTemplate.domain.dashboard.dto.UpdateDashboardDto;
import com.dashboardTemplate.dashboardTemplate.domain.dashboard.entity.AggregatedData;
import com.dashboardTemplate.dashboardTemplate.domain.dashboard.entity.Dashboard;
import com.dashboardTemplate.dashboardTemplate.domain.dashboard.entity.DashboardStatus;
import com.dashboardTemplate.dashboardTemplate.domain.dashboard.entity.GroupData;
import com.dashboardTemplate.dashboardTemplate.domain.dashboard.repository.AggregatedDataRepository;
import com.dashboardTemplate.dashboardTemplate.domain.dashboard.repository.DashboardRepository;
import com.dashboardTemplate.dashboardTemplate.domain.dashboard.repository.GroupDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final PasswordEncoder passwordEncoder;
    private final JDBCService jdbcService;
    private final GroupDataRepository groupDataRepository;
    private final AggregatedDataRepository aggregatedDataRepository;

    // 대시보드 생성
    public ResponseEntity<Map<String, Object>> createDashboard(String dashboardName, String tableName, String dashboardDescription, int companyNum) {

        Map<String, Object> responseMap = new HashMap<>();

        int randNum = secureRandom.nextInt(900000) + 100000;
        String id = passwordEncoder.encode(String.valueOf(randNum));
        String text = (dashboardDescription.isEmpty() || dashboardDescription == null) ? "-" : dashboardDescription;

        Dashboard dashboard = Dashboard.builder()
                .dashboardId(id)
                .companyNum(companyNum)
                .dashboardName(dashboardName)
                .tableName(tableName)
                .dashboardDescription(text)
                .dashboardStatus(DashboardStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build();

        dashboardRepository.save(dashboard);

        responseMap.put("message", "저장이 완료되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(responseMap);
    }

    // 대시보드 조회
    public ResponseEntity<Map<String, Object>> checkDashboardList(int companyNum, Pageable pageable) {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            Page<Dashboard> dashboardPage = dashboardRepository.findByCompanyNum(companyNum, pageable);

            responseMap.put("totalPages", dashboardPage.getTotalPages()); // 전체 페이지 수
            responseMap.put("totalCount", dashboardPage.getTotalElements()); // 전체 데이터 수
            responseMap.put("currentPage", dashboardPage.getNumber() + 1); // 현재 페이지 번호
            responseMap.put("dashboardList", dashboardPage.getContent());

            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            responseMap.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }

    // 상세 대시보드 조회
    public ResponseEntity<Map<String, Object>> checkDashboardDetails(String dashboardId, String status) {
        Map<String, Object> responseMap = new LinkedHashMap<>();

        try {
            if ("COMPLETED".equalsIgnoreCase(status) || "CREATED".equalsIgnoreCase(status)){
                Optional<Dashboard> optionalInfo = (dashboardRepository.findDashboardByDashboardId(dashboardId));
                Dashboard dashboardInfo = optionalInfo.get();

                List<Map<String, String>> databaseColumnList = jdbcService.getColumnByTableName(dashboardInfo.getTableName());
                Map<String, String> dashboardDefaultInfo = new LinkedHashMap<>();

                dashboardDefaultInfo.put("dashboardName", dashboardInfo.getDashboardName());
                dashboardDefaultInfo.put("tableName", dashboardInfo.getTableName());
                dashboardDefaultInfo.put("dashboardDescription", dashboardInfo.getDashboardDescription());

                responseMap.put("createdAt", optionalInfo.get().getCreatedAt());
                responseMap.put("updatedAt", optionalInfo.get().getUpdatedAt());
                responseMap.put("databaseColumnList", databaseColumnList);
                responseMap.put("dashboardDefaultInfo", dashboardDefaultInfo);
            } else {
                responseMap.put("message", "올바른 상태를 입력해주세요");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
            }

            if ("COMPLETED".equalsIgnoreCase(status)) {
                Optional<GroupData> groupDataOpt = groupDataRepository.findByDashboardId(dashboardId);
                Optional<AggregatedData> aggregatedDataOpt = aggregatedDataRepository.findByDashboardId(dashboardId);

                if (groupDataOpt.isPresent() && aggregatedDataOpt.isPresent()) {
                    GroupData groupData = groupDataOpt.get();
                    AggregatedData aggregatedData = aggregatedDataOpt.get();

                    Map<String, Object> groupDataMap = new LinkedHashMap<>();
                    groupDataMap.put("groupId", groupData.getGroupId());
                    groupDataMap.put("databaseColumn", groupData.getDatabaseColumn());
                    groupDataMap.put("databaseColumnAlias", groupData.getDatabaseColumnAlias());
                    groupDataMap.put("data", groupData.getData());

                    Map<String, Object> aggregatedDataMap = new LinkedHashMap<>();
                    aggregatedDataMap.put("aggregatedId", aggregatedData.getAggregatedId());
                    aggregatedDataMap.put("aggregatedDatabaseColumn", aggregatedData.getAggregatedDatabaseColumn());
                    aggregatedDataMap.put("dataType", aggregatedData.getDataType());
                    aggregatedDataMap.put("databaseColumnAlias", aggregatedData.getDatabaseColumnAlias());
                    aggregatedDataMap.put("dashboardCondition", aggregatedData.getDashboardCondition());
                    aggregatedDataMap.put("conditionValue", aggregatedData.getConditionValue());
                    aggregatedDataMap.put("statMethod", aggregatedData.getStatMethod());

                    Map<String, Object> dashboardDetailInfo = new LinkedHashMap<>();
                    dashboardDetailInfo.put("groupData", groupDataMap);
                    dashboardDetailInfo.put("aggregatedData", aggregatedDataMap);

                    responseMap.put("dashboardDetailInfo", dashboardDetailInfo);
                } else {
                    responseMap.put("dashboardDetailInfo", null);
                    responseMap.put("message", "GroupData 또는 AggregatedData를 찾을 수 없습니다.");
                }
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        } catch (Exception e) {
            responseMap.put("message", "서버오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }

    // 대시보드 수정
    public ResponseEntity<Map<String, Object>> updateDashboard(String dashboardId,
                                                               UpdateDashboardDto.GroupDataDto groupData,
                                                               UpdateDashboardDto.AggregatedDataDto aggregatedData) {

        Map<String, Object> responseMap = new LinkedHashMap<>();

        try {
            GroupData gdb = GroupData.builder()
                    .databaseColumn(groupData.getDatabaseColumn())
                    .databaseColumnAlias(groupData.getDatabaseColumnAlias())
                    .data(groupData.getData())
                    .dashboardId(dashboardId)
                    .build();

            AggregatedData arb = AggregatedData.builder()
                    .aggregatedDatabaseColumn(aggregatedData.getAggregatedDatabaseColumn())
                    .dataType(aggregatedData.getDataType())
                    .databaseColumnAlias(aggregatedData.getDatabaseColumnAlias())
                    .dashboardCondition(aggregatedData.getDashboardCondition())
                    .conditionValue(aggregatedData.getConditionValue())
                    .statMethod(aggregatedData.getStatMethod())
                    .dashboardId(dashboardId)
                    .build();

            groupDataRepository.save(gdb);
            aggregatedDataRepository.save(arb);

            responseMap.put("message", "성공");
            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        } catch (Exception e) {
            responseMap.put("message", "서버오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }
}
