package com.dashboardTemplate.dashboardTemplate.domain.dashboard.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateRequestDto {

    private String dashboardName;
    private String databaseName;
    private String dashboardDescription;
}
