package com.dashboardTemplate.dashboardTemplate.domain.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupDataRequestDto {

    @Schema(description = "columnName", example = "age")
    private String columnName;

    @Schema(description = "tableName", example = "test_phone")
    private String tableName;
}
