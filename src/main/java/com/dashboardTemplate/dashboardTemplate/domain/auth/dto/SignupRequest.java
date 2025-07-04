package com.dashboardTemplate.dashboardTemplate.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {

    @Schema(description = "companyId", example = "samsung_id")
    private String companyId;

    @Schema(description = "company", example = "삼성")
    private String company;

    @Schema(description = "companyEng", example = "samsung")
    private String companyEng;
}
