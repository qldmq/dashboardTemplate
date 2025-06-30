package com.dashboardTemplate.dashboardTemplate.domain.company.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final JdbcTemplate jdbcTemplate;

    public List<String> getTableNamesByCompany(String company) {

        String currentDatabaseName = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);

        log.info("현재 연결된 DB 이름: {}", currentDatabaseName);

        String sql = """
                SELECT table_name
                FROM information_schema.tables
                WHERE table_schema = ?
                  AND table_name LIKE CONCAT(?, '_%')
            """;

        List<String> result = jdbcTemplate.queryForList(sql, String.class, currentDatabaseName, company);

        return result;
    }
}
