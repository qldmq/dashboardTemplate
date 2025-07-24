package com.dashboardTemplate.dashboardTemplate.domain.dashboard.repository;

import com.dashboardTemplate.dashboardTemplate.domain.dashboard.entity.GroupData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupDataRepository extends JpaRepository<GroupData, Integer> {

    List<GroupData> findByDashboardId(String dashboardId);

    void deleteByDashboardId(String dashboardId);

    @Query("SELECT g FROM GroupData g WHERE g.dashboardId = :dashboardId AND g.databaseColumn = :databaseColumn")
    List<GroupData> findByDashboardIdAndDatabaseColumn(@Param("dashboardId") String dashboardId, @Param("databaseColumn") String databaseColumn);

}
