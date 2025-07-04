package com.dashboardTemplate.dashboardTemplate.domain.dashboard.repository;

import com.dashboardTemplate.dashboardTemplate.domain.dashboard.entity.AggregatedData;
import com.dashboardTemplate.dashboardTemplate.domain.dashboard.entity.Dashboard;
import com.dashboardTemplate.dashboardTemplate.domain.dashboard.entity.GroupData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DashboardRepository extends JpaRepository<Dashboard, String> {

    Page<Dashboard> findByCompanyNum(int companyNum, Pageable pageable);

    Optional<Dashboard> findDashboardByDashboardId(String dashboardId);
}
