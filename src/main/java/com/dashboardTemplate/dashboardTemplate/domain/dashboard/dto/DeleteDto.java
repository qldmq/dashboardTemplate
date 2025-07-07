package com.dashboardTemplate.dashboardTemplate.domain.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteDto {

    @Schema(description = "dashboardId", example = "대시보드 아이디 | ex: ")
    private String dashboardId;
}
