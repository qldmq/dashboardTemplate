package com.dashboardTemplate.dashboardTemplate.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReissueTokenRequestDto {

    @Schema(description = "refreshToken", example = "리프레시 토큰 | ex: eyJhbGciOiJHAm5O7c..")
    private String refreshToken;
}
