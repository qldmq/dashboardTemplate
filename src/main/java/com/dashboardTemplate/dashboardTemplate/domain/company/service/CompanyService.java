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
    private final String databaseName = "DashboardTemplate";

    public List<String> getTableNamesByCompany(String company) {
        String sql = """
                SELECT table_name
                FROM information_schema.tables
                WHERE table_schema = ?
                  AND table_name LIKE CONCAT(?, '_%')
            """;

        List<String> result = jdbcTemplate.queryForList(sql, String.class, databaseName, company);

        return result;
    }
}
