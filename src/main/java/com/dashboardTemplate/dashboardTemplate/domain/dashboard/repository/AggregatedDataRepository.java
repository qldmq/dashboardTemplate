package com.dashboardTemplate.dashboardTemplate.domain.dashboard.repository;

import com.dashboardTemplate.dashboardTemplate.domain.dashboard.entity.AggregatedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AggregatedDataRepository extends JpaRepository<AggregatedData, Integer> {

    List<AggregatedData> findByDashboardId(String dashboardId);

    AggregatedData findByDatabaseColumnAlias(String alias);

    void deleteByDashboardId(String dashboardId);
}
