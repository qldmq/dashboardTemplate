package com.dashboardTemplate.dashboardTemplate.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReissueTokenRequestDto {

    @Schema(description = "refreshToken", example = "eyJhbGciOiJHAm5O7c..")
    private String refreshToken;
}
