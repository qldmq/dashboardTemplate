package com.dashboardTemplate.dashboardTemplate.domain.auth.repository;

import com.dashboardTemplate.dashboardTemplate.domain.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Integer> {

    Optional<Auth> findByCompanyId(String companyId);

    boolean existsByCompanyId(String companyId);
    boolean existsByCompany(String company);
}
