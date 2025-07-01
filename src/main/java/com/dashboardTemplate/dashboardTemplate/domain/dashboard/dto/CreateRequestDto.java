package com.dashboardTemplate.dashboardTemplate.domain.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateRequestDto {

    @Schema(description = "dashboardName", example = "대시보드1")
    private String dashboardName;

    @Schema(description = "tableName", example = "1번테이블")
    private String tableName;

    @Schema(description = "dashboardDescription", example = "대시보드 설명입니다.")
    private String dashboardDescription;
}
