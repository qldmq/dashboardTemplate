package com.dashboardTemplate.dashboardTemplate.domain.dashboard.repository;

import com.dashboardTemplate.dashboardTemplate.domain.dashboard.entity.AggregatedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AggregatedDataRepository extends JpaRepository<AggregatedData, Integer> {

    List<AggregatedData> findByDashboardId(String dashboardId);

    void deleteByDashboardId(String dashboardId);
}
