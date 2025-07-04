package com.dashboardTemplate.dashboardTemplate.domain.dashboard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "aggregated_data")
@NoArgsConstructor
@AllArgsConstructor
public class AggregatedData {

    @Id
    @Column(name = "aggregated_id")
    private Integer aggregatedId;

    @Column(name = "aggregated_database_column")
    private String databaseColumn;

    @Column(name = "data_type")
    private String dataType;

    @Column(name = "database_column_alias")
    private String databaseColumnAlias;

    @Column(name = "condition")
    private String condition;

    @Column(name = "condition_value")
    private String conditionValue;

    @Column(name = "stat_method")
    private String statMethod;

    @Column(name = "dashboard_id")
    private String dashboardId;
}
