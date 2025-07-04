package com.dashboardTemplate.dashboardTemplate.domain.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "company")
@NoArgsConstructor
@AllArgsConstructor
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_num")
    private Integer companyNum;

    @Column(name = "company_id")
    private String companyId;

    @Column(name = "company")
    private String company;

    @Column(name = "company_eng")
    private String companyEng;

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setCompanyEng(String companyEng) {
        this.companyEng = companyEng;
    }
}