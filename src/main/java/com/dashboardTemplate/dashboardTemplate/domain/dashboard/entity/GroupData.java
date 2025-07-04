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
@Table(name = "group_data")
@NoArgsConstructor
@AllArgsConstructor
public class GroupData {

    @Id
    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "database_column")
    private String databaseColumn;

    @Column(name = "database_column_alias")
    private String databaseColumnAlias;

    @Column(name = "data")
    private String data;

    @Column(name = "dashboard_id")
    private String dashboardId;
}
