package com.dashboardTemplate.dashboardTemplate.domain.dashboard.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class UpdateDashboardDto {

    private GroupDataDto groupData;
    private AggregatedDataDto aggregatedData;

    private String dashboardId;

    @Getter
    @Setter
    public static class GroupDataDto {
        private int groupId;
        private String databaseColumn;
        private String databaseColumnAlias;
        private String data;
        private String dashboardId;
    }

    @Getter
    @Setter
    public static class AggregatedDataDto {
        private int aggregatedId;
        private String aggregatedDatabaseColumn;
        private String dataType;
        private String databaseColumnAlias;
        private String dashboardCondition;
        private String conditionValue;
        private String statMethod;
        private String dashboardId;
    }
}
