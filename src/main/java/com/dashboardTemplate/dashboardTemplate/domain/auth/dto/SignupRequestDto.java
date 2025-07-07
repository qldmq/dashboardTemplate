package com.dashboardTemplate.dashboardTemplate.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequestDto {

    @Schema(description = "companyId", example = "기업 아이디 | ex: test_id")
    private String companyId;

    @Schema(description = "company", example = "기업명 | ex: 테스트기업")
    private String company;

    @Schema(description = "companyEng", example = "기업명(영어로) | ex: testCompany")
    private String companyEng;
}
