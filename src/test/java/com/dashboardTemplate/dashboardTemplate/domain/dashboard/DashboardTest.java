package com.dashboardTemplate.dashboardTemplate.domain.dashboard;

import com.dashboardTemplate.dashboardTemplate.domain.JDBC.service.JDBCService;
import static org.assertj.core.api.Assertions.assertThat;

import com.dashboardTemplate.dashboardTemplate.domain.dashboard.controller.DashboardController;
import com.dashboardTemplate.dashboardTemplate.domain.dashboard.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class DashboardTest {

    private static final Logger log = LoggerFactory.getLogger(DashboardTest.class);
    @Autowired
    private JDBCService jdbcService;

    @Autowired
    private DashboardController dashboardController;

    @Autowired
    private DashboardService dashboardService;

    // 테이블명에 들어있는 컬럼명 조회 테스트
    @Test
    void getColumnNamesByTableName() {

        String tableName = "samsung_pad";

        List<Map<String, String>> column = jdbcService.getColumnByTableName(tableName);

        assertThat(column).isNotEmpty();

        log.info("조회된 컬럼: {}", column);
    }

    // 컬럼에 들어있는 데이터 조회 테스트
    @Test
    void getGroupDataByColumnName() {

        String tableName = "test_phone";
        String columnName = "age";

        List<Object> data = jdbcService.getGroupDataByColumn(columnName, tableName);

        log.info("조회된 컬럼: {}", data);
    }

    // 필터링된 그룹데이터 조회
    @Test
    void getFilterGroupData() {

        String tableName=  "test_phone";
        String dashboardColumn = "version";
        String data = "iphone 1";
        LocalDateTime startDate = LocalDateTime.parse("2025-07-15T11:43");
        LocalDateTime endDate = LocalDateTime.parse("2025-11-24T18:00");

        int cnt = jdbcService.countGroupData(tableName, dashboardColumn, data, startDate, endDate);

        log.info("조회된 groupData 개수: {}", cnt);
    }

    @Test
    void getFilterData() {

        String dashboardId = "$2a$10$lRKsrtRYhZ7SJhd8XklJ1e51EFBWfBMOfPj1OodP/WUzRHsX22U76";
        String selectGroupData = "version";
        String selectAggregatedData = "가격 평균";
        LocalDateTime startDate = LocalDateTime.parse("2025-07-15T11:43");
        LocalDateTime endDate = LocalDateTime.parse("2025-11-24T18:00");

        ResponseEntity<Map<String, Object>> response = dashboardService.filterData(dashboardId, selectGroupData, selectAggregatedData, startDate, endDate);

        System.out.println("HTTP Status: " + response.getStatusCode());

        Map<String, Object> body = response.getBody();

        if (body != null && body.containsKey("message")) {
            List<Integer> counts = (List<Integer>) body.get("message");
            System.out.println("그룹별 카운트 결과: " + counts);
        } else {
            System.out.println("응답 바디에 message가 없습니다.");
        }
    }

    @Test
    void getFilterAggregatedData() {
        String statMethod = "평균";
        String dashboardCondition = "크다";
        String tableName = "test_phone";
        String columnName = "price";
        String data = "4000";
        String groupColumn = "";
        String groupValue = "";
        LocalDateTime startDate = LocalDateTime.parse("2025-07-15T11:43");
        LocalDateTime endDate = LocalDateTime.parse("2025-11-24T18:00");

        Object result = jdbcService.filterAggregatedData(statMethod, dashboardCondition, tableName, columnName, data, groupColumn, groupValue, startDate, endDate);

        int count = ((Number) result).intValue();
        log.info("테스트 결과: {}", count);
    }
}
