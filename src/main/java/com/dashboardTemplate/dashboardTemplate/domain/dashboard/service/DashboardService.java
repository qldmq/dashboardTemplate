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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
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

        try {
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

            log.info("저장이 완료되었습니다.");
            responseMap.put("message", "저장이 완료되었습니다.");
            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        } catch (Exception e) {
            log.error("대시보드 생성 중 예외 발생", e);
            responseMap.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
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

            log.info("대시보드 리스트 조회가 완료되었습니다.");
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            log.error("대시보드 리스트 조회 중 예외 발생", e);
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
                if (optionalInfo.isEmpty()) {
                    log.warn("해당 대시보드를 찾을 수 없습니다.");
                    responseMap.put("message", "해당 대시보드를 찾을 수 없습니다.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMap);
                }

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
                log.warn("올바른 상태를 입력해주세요.");
                responseMap.put("message", "올바른 상태를 입력해주세요");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
            }

            if ("COMPLETED".equalsIgnoreCase(status)) {
                List<GroupData> groupDataList = groupDataRepository.findByDashboardId(dashboardId);
                List<AggregatedData> aggregatedDataList = aggregatedDataRepository.findByDashboardId(dashboardId);

                List<Map<String, Object>> groupDataMaps = new ArrayList<>();
                for (GroupData groupData : groupDataList) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("groupId", groupData.getGroupId());
                    map.put("databaseColumn", groupData.getDatabaseColumn());
                    map.put("databaseColumnAlias", groupData.getDatabaseColumnAlias());
                    map.put("data", groupData.getData());
                    groupDataMaps.add(map);
                }

                List<Map<String, Object>> aggregatedDataMaps = new ArrayList<>();
                for (AggregatedData aggregatedData : aggregatedDataList) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("aggregatedId", aggregatedData.getAggregatedId());
                    map.put("aggregatedDatabaseColumn", aggregatedData.getAggregatedDatabaseColumn());
                    map.put("dataType", aggregatedData.getDataType());
                    map.put("databaseColumnAlias", aggregatedData.getDatabaseColumnAlias());
                    map.put("dashboardCondition", aggregatedData.getDashboardCondition());
                    map.put("conditionValue", aggregatedData.getConditionValue());
                    map.put("statMethod", aggregatedData.getStatMethod());
                    aggregatedDataMaps.add(map);
                }
                Map<String, Object> dashboardDetailInfo = new LinkedHashMap<>();
                dashboardDetailInfo.put("groupData", groupDataMaps);
                dashboardDetailInfo.put("aggregatedData", aggregatedDataMaps);

                responseMap.put("dashboardDetailInfo", dashboardDetailInfo);
            }

            log.info("상세 대시보드 조회가 완료되었습니다.");
            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        } catch (Exception e) {
            log.error("상세 대시보드 조회 중 예외 발생", e);
            responseMap.put("message", "서버오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }

    // 대시보드 수정
    @Transactional
    public ResponseEntity<Map<String, Object>> updateDashboard(String dashboardId,
                                                               List<UpdateDashboardDto.GroupDataDto> groupDataList,
                                                               List<UpdateDashboardDto.AggregatedDataDto> aggregatedDataList) {

        Map<String, Object> responseMap = new LinkedHashMap<>();

        try {

            Dashboard dashboard = dashboardRepository.findDashboardByDashboardId(dashboardId)
                    .orElseThrow(() -> new NoSuchElementException("해당 대시보드가 존재하지 않습니다."));
            dashboard.setUpdatedAt(LocalDateTime.now());

            groupDataRepository.deleteByDashboardId(dashboardId);
            aggregatedDataRepository.deleteByDashboardId(dashboardId);

            for (UpdateDashboardDto.GroupDataDto groupData : groupDataList) {
                GroupData gd = GroupData.builder()
                        .groupId(groupData.getGroupId())
                        .databaseColumn(groupData.getDatabaseColumn())
                        .databaseColumnAlias(groupData.getDatabaseColumnAlias())
                        .data(groupData.getData())
                        .dashboardId(dashboardId)
                        .build();

                groupDataRepository.save(gd);
            }

            for (UpdateDashboardDto.AggregatedDataDto aggregatedData : aggregatedDataList) {
                AggregatedData ar = AggregatedData.builder()
                        .aggregatedId(aggregatedData.getAggregatedId())
                        .aggregatedDatabaseColumn(aggregatedData.getAggregatedDatabaseColumn())
                        .dataType(aggregatedData.getDataType())
                        .databaseColumnAlias(aggregatedData.getDatabaseColumnAlias())
                        .dashboardCondition(aggregatedData.getDashboardCondition())
                        .conditionValue(aggregatedData.getConditionValue())
                        .statMethod(aggregatedData.getStatMethod())
                        .dashboardId(dashboardId)
                        .build();

                aggregatedDataRepository.save(ar);
            }

            dashboard.setDashboardStatus(DashboardStatus.COMPLETED);

            log.info("대시보드 수정이 완료되었습니다.");
            responseMap.put("message", "대시보드 수정이 완료되었습니다.");
            return ResponseEntity.status(HttpStatus.OK).body(responseMap);

        } catch (Exception e) {
            log.error("대시보드 수정 중 예외 발생", e);
            responseMap.put("message", "서버오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }

    // 대시보드 삭제
    @Transactional
    public ResponseEntity<Map<String, Object>> deleteDashboard(int companyNum, String dashboardId) {
        Map<String, Object> responseMap = new LinkedHashMap<>();

        Dashboard dashboard = dashboardRepository.findDashboardByDashboardId(dashboardId)
                .orElseThrow(() -> new NoSuchElementException("해당 대시보드가 존재하지 않습니다."));

        if (companyNum != dashboard.getCompanyNum()) {
            responseMap.put("message", "회원정보가 일치하지 않습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
        }

        List<GroupData> groupDataList = groupDataRepository.findByDashboardId(dashboardId);
        if (!groupDataList.isEmpty()) {
            groupDataRepository.deleteAll(groupDataList);
        }

        List<AggregatedData> aggregatedDataList = aggregatedDataRepository.findByDashboardId(dashboardId);
        if (!aggregatedDataList.isEmpty()) {
            aggregatedDataRepository.deleteAll(aggregatedDataList);
        }

        dashboardRepository.delete(dashboard);

        responseMap.put("message", "삭제가 완료되었습니다.");
        return ResponseEntity.ok(responseMap);
    }

    // 대시보드 그룹 항목의 데이터 조회
    public ResponseEntity<Map<String, Object>> dashboardGroupData(String columnName, String tableName) {

        Map<String, Object> responseMap = new LinkedHashMap<>();

        try {
            List<Object> groupData = jdbcService.getGroupDataByColumn(columnName, tableName);

            responseMap.put("groupData", groupData);
            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        } catch (Exception e) {
            log.error("그룹항목 데이터 조회 중 예외 발생", e);
            responseMap.put("message", "서버 에러: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }
}