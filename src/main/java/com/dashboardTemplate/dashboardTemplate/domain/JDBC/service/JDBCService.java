package com.dashboardTemplate.dashboardTemplate.domain.JDBC.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JDBCService {

    private final JdbcTemplate jdbcTemplate;

    // companyEng로 시작하는 테이블 조회
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

    // 테이블에 있는 컬럼명 조회
    public List<Map<String, String>> getColumnByTableName(String tableName) {

        String sql = """
            SELECT column_name, data_type
            FROM information_schema.columns
            WHERE table_name = ?
            ORDER BY ordinal_position
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, String> columnMap = new LinkedHashMap<>();
            String dbType = rs.getString("data_type");

            String javaType = mapToJavaType(dbType);

            columnMap.put("databaseColumn", rs.getString("column_name"));
            columnMap.put("dataType", javaType);
            return columnMap;
        }, tableName);
    }

    private String mapToJavaType(String dbType) {
        return switch (dbType.toLowerCase()) {
            case "varchar", "text", "char", "nvarchar", "longtext" -> "string";
            case "int", "integer", "smallint", "mediumint" -> "int";
            case "bigint" -> "long";
            case "decimal", "numeric", "float", "double" -> "double";
            case "bit", "boolean" -> "boolean";
            case "date", "datetime", "timestamp" -> "datetime";
            case "blob" -> "byte[]";
            default -> "object";
        };
    }
}
