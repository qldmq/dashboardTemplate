package com.dashboardTemplate.dashboardTemplate.domain.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateDashboardDto {

    private List<GroupDataDto> groupData;
    private List<AggregatedDataDto> aggregatedData;

    @Schema(description = "dashboardId", example = "$2a$10$dkCd.6yQRGSdXuUbMgzDO.z1B3cAP/qI323xfkuqTYUUdN4kv4lt2")
    private String dashboardId;

    @Getter
    @Setter
    public static class GroupDataDto {

        @Schema(description = "groupId", example = "1")
        private int groupId;

        @Schema(description = "databaseColumn", example = "age")
        private String databaseColumn;

        @Schema(description = "databaseColumnAlias", example = "나이")
        private String databaseColumnAlias;

        @Schema(description = "data", example = "25")
        private String data;

        @Schema(description = "dashboardId", example = "$2a$10$dkCd.6yQRGSdXuUbMgzDO.z1B3cAP/qI323xfkuqTYUUdN4kv4lt2")
        private String dashboardId;
    }

    @Getter
    @Setter
    public static class AggregatedDataDto {

        @Schema(description = "aggregatedId", example = "1")
        private int aggregatedId;

        @Schema(description = "aggregatedDatabaseColumn", example = "age")
        private String aggregatedDatabaseColumn;

        @Schema(description = "databaseColumnAlias", example = "나이")
        private String databaseColumnAlias;

        @Schema(description = "dataType", example = "int")
        private String dataType;

        @Schema(description = "dashboardCondition", example = "같다")
        private String dashboardCondition;

        @Schema(description = "conditionValue", example = "25")
        private String conditionValue;

        @Schema(description = "statMethod", example = "개수")
        private String statMethod;

        @Schema(description = "dashboardId", example = "$2a$10$dkCd.6yQRGSdXuUbMgzDO.z1B3cAP/qI323xfkuqTYUUdN4kv4lt2")
        private String dashboardId;
    }
}
