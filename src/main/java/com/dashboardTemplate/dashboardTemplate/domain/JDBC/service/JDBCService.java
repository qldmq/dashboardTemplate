package com.dashboardTemplate.dashboardTemplate.domain.JDBC.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JDBCService {

    private final JdbcTemplate jdbcTemplate;

    // 1. [수정] MySQL의 'SELECT DATABASE()' -> PostgreSQL의 'current_database()'로 변경
    // 2. [수정] PostgreSQL은 table_schema가 DB이름이 아니라 보통 'public'입니다.
    public List<String> getTableNamesByCompany(String company) {

        String currentDatabaseName = jdbcTemplate.queryForObject("SELECT current_database()", String.class);

        log.info("현재 연결된 DB 이름: {}", currentDatabaseName);

        // [수정 포인트 1] CONCAT(?, '_%')을 지우고 그냥 LIKE ? 로 변경
        String sql = """
            SELECT table_name
            FROM information_schema.tables
            WHERE table_catalog = ?
                AND table_schema = 'public'
                AND table_name LIKE ?
        """;

        // [수정 포인트 2] Java에서 미리 검색어 패턴을 완성함
        // 예: 회사명이 "apple"이면 -> "apple_%" 라는 문자열을 만듦
        String searchPattern = company + "_%";

        // 완성된 패턴(searchPattern)을 SQL에 전달
        List<String> result = jdbcTemplate.queryForList(sql, String.class, currentDatabaseName, searchPattern);

        return result;
    }

    // 테이블에 있는 컬럼명 조회
    public List<Map<String, String>> getColumnByTableName(String tableName) {

        // 3. [수정] information_schema.columns 조회 시 스키마 조건 추가 (안전장치)
        String sql = """
            SELECT column_name, data_type
            FROM information_schema.columns
            WHERE table_name = ?
            AND table_schema = 'public'
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

    // 4. [수정] PostgreSQL 데이터 타입 매핑 추가
    private String mapToJavaType(String dbType) {
        return switch (dbType.toLowerCase()) {
            case "varchar", "text", "char", "character varying", "character" -> "string";
            case "int", "integer", "smallint", "int2", "int4" -> "int";
            case "bigint", "int8" -> "long";
            case "decimal", "numeric", "float", "double", "real", "double precision" -> "double";
            case "bit", "boolean", "bool" -> "boolean";
            case "date", "datetime", "timestamp", "timestamp without time zone", "timestamptz" -> "datetime";
            case "blob", "bytea" -> "byte[]";
            default -> "object";
        };
    }

    // 5. [수정] 백틱(`) 제거하고 쌍따옴표(") 사용
    // PostgreSQL은 식별자(테이블명, 컬럼명)를 감쌀 때 백틱이 아닌 쌍따옴표를 씁니다.
    public List<Object> getGroupDataByColumn(String columnName, String tableName) {

        String sql = String.format("SELECT DISTINCT %s FROM %s", columnName, tableName);

        return jdbcTemplate.queryForList(sql, Object.class);
    }

    public Integer countGroupData(String tableName, String databaseColumn, String data, LocalDateTime startDate, LocalDateTime endDate) {

        String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = ? AND created_at BETWEEN ? AND ?", tableName, databaseColumn);

        return jdbcTemplate.queryForObject(sql, Integer.class, new Object[]{data, startDate, endDate});
    }

    public Number filterAggregatedData(String statMethod,
                                       String dashboardCondition,
                                       String tableName,
                                       String aggregatedDatabaseColumn,
                                       String conditionValue,
                                       String selectGroupData,
                                       String groupValue,
                                       LocalDateTime startDate,
                                       LocalDateTime endDate) {

        String stat = switch (statMethod) {
            case "개수" -> "COUNT";
            case "합계" -> "SUM";
            case "평균" -> "AVG";
            default -> throw new IllegalArgumentException("지원하지 않는 통계 메서드입니다.");
        };

        String sql;
        Object[] params;

        // 테이블명과 컬럼명에 쌍따옴표("")를 붙여서 PostgreSQL 대소문자 이슈 방지
        String t = "\"" + tableName + "\"";
        String col = "\"" + aggregatedDatabaseColumn + "\"";
        String groupCol = "\"" + selectGroupData + "\"";

        switch (dashboardCondition) {
            case "크다" -> {
                sql = String.format("SELECT %s(%s) FROM %s WHERE %s > ? AND %s = ? AND created_at BETWEEN ? AND ?",
                        stat, col, t, col, groupCol);
                params = new Object[]{conditionValue, groupValue, startDate, endDate};
            }
            case "작다" -> {
                sql = String.format("SELECT %s(%s) FROM %s WHERE %s < ? AND %s = ? AND created_at BETWEEN ? AND ?",
                        stat, col, t, col, groupCol);
                params = new Object[]{conditionValue, groupValue, startDate, endDate};
            }
            case "크거나 같다" -> {
                sql = String.format("SELECT %s(%s) FROM %s WHERE %s >= ? AND %s = ? AND created_at BETWEEN ? AND ?",
                        stat, col, t, col, groupCol);
                params = new Object[]{conditionValue, groupValue, startDate, endDate};
            }
            case "작거나 같다" -> {
                sql = String.format("SELECT %s(%s) FROM %s WHERE %s <= ? AND %s = ? AND created_at BETWEEN ? AND ?",
                        stat, col, t, col, groupCol);
                params = new Object[]{conditionValue, groupValue, startDate, endDate};
            }
            case "같다" -> {
                sql = String.format("SELECT %s(%s) FROM %s WHERE %s = ? AND %s = ? AND created_at BETWEEN ? AND ?",
                        stat, col, t, col, groupCol);
                params = new Object[]{conditionValue, groupValue, startDate, endDate};
            }
            case "다르다" -> {
                sql = String.format("SELECT %s(%s) FROM %s WHERE %s != ? AND %s = ? AND created_at BETWEEN ? AND ?",
                        stat, col, t, col, groupCol);
                params = new Object[]{conditionValue, groupValue, startDate, endDate};
            }
            case "포함된다" -> {
                sql = String.format("SELECT %s(%s) FROM %s WHERE %s LIKE ? AND %s = ? AND created_at BETWEEN ? AND ?",
                        stat, col, t, col, groupCol);
                params = new Object[]{"%" + conditionValue + "%", groupValue, startDate, endDate};
            }
            case "포함되지 않는다" -> {
                sql = String.format("SELECT %s(%s) FROM %s WHERE %s NOT LIKE ? AND %s = ? AND created_at BETWEEN ? AND ?",
                        stat, col, t, col, groupCol);
                params = new Object[]{"%" + conditionValue + "%", groupValue, startDate, endDate};
            }
            case "범위 지정" -> {
                String[] values = conditionValue.split(",");
                if (values.length != 2) throw new IllegalArgumentException("범위 지정은 'a,b' 형태여야 합니다.");
                sql = String.format("SELECT %s(%s) FROM %s WHERE %s BETWEEN ? AND ? AND %s = ? AND created_at BETWEEN ? AND ?",
                        stat, col, t, col, groupCol);
                params = new Object[]{values[0].trim(), values[1].trim(), groupValue, startDate, endDate};
            }
            case "없음" -> {
                sql = String.format("SELECT %s(%s) FROM %s WHERE %s = ? AND created_at BETWEEN ? AND ?",
                        stat, col, t, groupCol);
                params = new Object[]{groupValue, startDate, endDate};
            }
            default -> throw new IllegalArgumentException("지원하지 않는 조건입니다.");
        }

        log.info("SQL문: {}, params: {}", sql, Arrays.toString(params));
        // 통계 쿼리 결과가 null일 경우 0으로 처리하는 것이 안전할 수 있습니다.
        // 여기서는 그대로 반환합니다.
        return jdbcTemplate.queryForObject(sql, Number.class, params);
    }

    public List<String> getDistinctGroupValues(String tableName, String selectGroupData, LocalDateTime startDate, LocalDateTime endDate) {
        // 여기도 컬럼, 테이블명에 쌍따옴표 추가
        String sql = String.format("SELECT DISTINCT \"%s\" FROM \"%s\" WHERE created_at BETWEEN ? AND ?", selectGroupData, tableName);
        return jdbcTemplate.queryForList(sql, String.class, startDate, endDate);
    }
    
    
    /* mysql용
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

    public List<Object> getGroupDataByColumn(String columnName, String tableName) {

        String sql = String.format("SELECT DISTINCT `%s` FROM `%s`", columnName, tableName);

        return jdbcTemplate.queryForList(sql, Object.class);
    }

    public Integer countGroupData(String tableName, String databaseColumn, String data, LocalDateTime startDate, LocalDateTime endDate) {

        String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = ? AND created_at BETWEEN ? AND ?", tableName, databaseColumn);

        return jdbcTemplate.queryForObject(sql, Integer.class, new Object[]{data, startDate, endDate});
    }

    public Number filterAggregatedData(String statMethod,
                                       String dashboardCondition,
                                       String tableName,
                                       String aggregatedDatabaseColumn,
                                       String conditionValue,
                                       String selectGroupData,
                                       String groupValue,
                                       LocalDateTime startDate,
                                       LocalDateTime endDate) {

        String stat = switch (statMethod) {
            case "개수" -> "COUNT";
            case "합계" -> "SUM";
            case "평균" -> "AVG";
            default -> throw new IllegalArgumentException("지원하지 않는 통계 메서드입니다.");
        };

        String sql;
        Object[] params;

        switch (dashboardCondition) {
            case "크다" -> {
                sql = String.format("SELECT %s(%s) FROM %s WHERE %s > ? AND %s = ? AND created_at BETWEEN ? AND ?",
                        stat, aggregatedDatabaseColumn, tableName, aggregatedDatabaseColumn, selectGroupData);
                params = new Object[]{conditionValue, groupValue, startDate, endDate};
            }
            case "작다" -> {
                sql = String.format("SELECT %s(%s) FROM %s WHERE %s < ? AND %s = ? AND created_at BETWEEN ? AND ?",
                        stat, aggregatedDatabaseColumn, tableName, aggregatedDatabaseColumn, selectGroupData);
                params = new Object[]{conditionValue, groupValue, startDate, endDate};
            }
            case "크거나 같다" -> {
                sql = String.format("SELECT %s(%s) FROM %s WHERE %s >= ? AND %s = ? AND created_at BETWEEN ? AND ?",
                        stat, aggregatedDatabaseColumn, tableName, aggregatedDatabaseColumn, selectGroupData);
                params = new Object[]{conditionValue, groupValue, startDate, endDate};
            }
            case "작거나 같다" -> {
                sql = String.format("SELECT %s(%s) FROM %s WHERE %s <= ? AND %s = ? AND created_at BETWEEN ? AND ?",
                        stat, aggregatedDatabaseColumn, tableName, aggregatedDatabaseColumn, selectGroupData);
                params = new Object[]{conditionValue, groupValue, startDate, endDate};
            }
            case "같다" -> {
                sql = String.format("SELECT %s(%s) FROM %s WHERE %s = ? AND %s = ? AND created_at BETWEEN ? AND ?",
                        stat, aggregatedDatabaseColumn, tableName, aggregatedDatabaseColumn, selectGroupData);
                params = new Object[]{conditionValue, groupValue, startDate, endDate};
            }
            case "다르다" -> {
                sql = String.format("SELECT %s(%s) FROM %s WHERE %s != ? AND %s = ? AND created_at BETWEEN ? AND ?",
                        stat, aggregatedDatabaseColumn, tableName, aggregatedDatabaseColumn, selectGroupData);
                params = new Object[]{conditionValue, groupValue, startDate, endDate};
            }
            case "포함된다" -> {
                sql = String.format("SELECT %s(%s) FROM %s WHERE %s LIKE ? AND %s = ? AND created_at BETWEEN ? AND ?",
                        stat, aggregatedDatabaseColumn, tableName, aggregatedDatabaseColumn, selectGroupData);
                params = new Object[]{"%" + conditionValue + "%", groupValue, startDate, endDate};
            }
            case "포함되지 않는다" -> {
                sql = String.format("SELECT %s(%s) FROM %s WHERE %s NOT LIKE ? AND %s = ? AND created_at BETWEEN ? AND ?",
                        stat, aggregatedDatabaseColumn, tableName, aggregatedDatabaseColumn, selectGroupData);
                params = new Object[]{"%" + conditionValue + "%", groupValue, startDate, endDate};
            }
            case "범위 지정" -> {
                String[] values = conditionValue.split(",");
                if (values.length != 2) throw new IllegalArgumentException("범위 지정은 'a,b' 형태여야 합니다.");
                sql = String.format("SELECT %s(%s) FROM %s WHERE %s BETWEEN ? AND ? AND %s = ? AND created_at BETWEEN ? AND ?",
                        stat, aggregatedDatabaseColumn, tableName, aggregatedDatabaseColumn, selectGroupData);
                params = new Object[]{values[0].trim(), values[1].trim(), groupValue, startDate, endDate};
            }
            case "없음" -> {
                sql = String.format("SELECT %s(%s) FROM %s WHERE %s = ? AND created_at BETWEEN ? AND ?",
                        stat, aggregatedDatabaseColumn, tableName, selectGroupData);
                params = new Object[]{groupValue, startDate, endDate};
            }
            default -> throw new IllegalArgumentException("지원하지 않는 조건입니다.");
        }

        log.info("SQL문: {}, params: {}", sql, Arrays.toString(params));
        return jdbcTemplate.queryForObject(sql, Number.class, params);
    }
    public List<String> getDistinctGroupValues(String tableName, String selectGroupData, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = String.format("SELECT DISTINCT %s FROM %s WHERE created_at BETWEEN ? AND ?", selectGroupData, tableName);
        return jdbcTemplate.queryForList(sql, String.class, startDate, endDate);
    }
    
     */
}
