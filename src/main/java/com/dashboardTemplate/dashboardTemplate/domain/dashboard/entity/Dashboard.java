package com.dashboardTemplate.dashboardTemplate.domain.dashboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "dashboard")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dashboard {

    @Id
    @Column(name = "dashboard_id")
    private String dashboardId;

    @Column(name = "company_num")
    private int companyNum;

    @Column(name = "dashboard_name")
    private String dashboardName;

    @Column(name = "database_name")
    private String databaseName;

    @Column(name = "dashboard_description")
    private String dashboardDescription;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "dashboard_status")
    private DashboardStatus dashboardStatus;
}
