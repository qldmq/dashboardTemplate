package com.dashboardTemplate.dashboardTemplate.domain.dashboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "aggregated_data")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AggregatedData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aggregated_id")
    private Integer aggregatedId;

    @Column(name = "aggregated_database_column")
    private String aggregatedDatabaseColumn;

    @Column(name = "data_type")
    private String dataType;

    @Column(name = "database_column_alias")
    private String databaseColumnAlias;

    @Column(name = "dashboard_condition")
    private String dashboardCondition;

    @Column(name = "condition_value")
    private String conditionValue;

    @Column(name = "stat_method")
    private String statMethod;

    @Column(name = "dashboard_id")
    private String dashboardId;
}
