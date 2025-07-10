package com.dashboardTemplate.dashboardTemplate.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequestDto {

    @Schema(description = "companyId", example = "test_id1")
    private String companyId;

    @Schema(description = "company", example = "테스트기업1")
    private String company;

    @Schema(description = "companyEng", example = "testCompany1")
    private String companyEng;
}
