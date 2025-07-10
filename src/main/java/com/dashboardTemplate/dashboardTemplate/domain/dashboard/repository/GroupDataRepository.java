package com.dashboardTemplate.dashboardTemplate.domain.dashboard.repository;

import com.dashboardTemplate.dashboardTemplate.domain.dashboard.entity.GroupData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupDataRepository extends JpaRepository<GroupData, Integer> {

    List<GroupData> findByDashboardId(String dashboardId);

    void deleteByDashboardId(String dashboardId);
}
