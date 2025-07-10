package com.dashboardTemplate.dashboardTemplate.domain.dashboard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
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

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "dashboard_description")
    private String dashboardDescription;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "dashboard_status")
    private DashboardStatus dashboardStatus;
}
