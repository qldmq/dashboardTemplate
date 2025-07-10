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

    @Schema(description = "dashboardId", example = "대시보드 아이디 | ex: $2a$10$dkCd.6yQRGSdXuUbMgzDO.z1B3cAP/qI323xfkuqTYUUdN4kv4lt2")
    private String dashboardId;

    @Getter
    @Setter
    public static class GroupDataDto {

        @Schema(description = "groupId", example = "그룹 아이디 | ex: 1")
        private int groupId;

        @Schema(description = "databaseColumn", example = "그룹 컬럼명 | ex: age")
        private String databaseColumn;

        @Schema(description = "databaseColumnAlias", example = "그룹 컬럼 별칭 | ex: 나이")
        private String databaseColumnAlias;

        @Schema(description = "data", example = "데이터 | ex: 25")
        private String data;

        @Schema(description = "dashboardId", example = "대시보드 아이디 | ex: $2a$10$dkCd.6yQRGSdXuUbMgzDO.z1B3cAP/qI323xfkuqTYUUdN4kv4lt2")
        private String dashboardId;
    }

    @Getter
    @Setter
    public static class AggregatedDataDto {

        @Schema(description = "aggregatedId", example = "조건 아이디 | ex: 1")
        private int aggregatedId;

        @Schema(description = "aggregatedDatabaseColumn", example = "조건컬럼명 | ex: age")
        private String aggregatedDatabaseColumn;

        @Schema(description = "databaseColumnAlias", example = "조건 컬럼 별칭 | ex: 나이")
        private String databaseColumnAlias;

        @Schema(description = "dataType", example = "데이터 타입 | ex: int")
        private String dataType;

        @Schema(description = "dashboardCondition", example = "조건 | ex: 같다")
        private String dashboardCondition;

        @Schema(description = "conditionValue", example = "조건값 | ex: 25")
        private String conditionValue;

        @Schema(description = "statMethod", example = "통계 방법 | ex: 개수")
        private String statMethod;

        @Schema(description = "dashboardId", example = "대시보드 아이디 | ex: $2a$10$dkCd.6yQRGSdXuUbMgzDO.z1B3cAP/qI323xfkuqTYUUdN4kv4lt2")
        private String dashboardId;
    }
}
