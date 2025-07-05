package com.dashboardTemplate.dashboardTemplate.domain.dashboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "group_data")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
