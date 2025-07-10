package com.dashboardTemplate.dashboardTemplate.domain.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateRequestDto {

    @Schema(description = "dashboardName", example = "테스트 대시보드")
    private String dashboardName;

    @Schema(description = "tableName", example = "test_phone")
    private String tableName;

    @Schema(description = "dashboardDescription", example = "테스트 대시보드의 설명입니다.")
    private String dashboardDescription;
}
