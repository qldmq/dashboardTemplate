package com.dashboardTemplate.dashboardTemplate.domain.dashboard;

import com.dashboardTemplate.dashboardTemplate.domain.JDBC.service.JDBCService;
import static org.assertj.core.api.Assertions.assertThat;

import com.dashboardTemplate.dashboardTemplate.domain.dashboard.controller.DashboardController;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class DashboardTest {

    private static final Logger log = LoggerFactory.getLogger(DashboardTest.class);
    @Autowired
    private JDBCService jdbcService;

    @Autowired
    private DashboardController dashboardController;

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
}
