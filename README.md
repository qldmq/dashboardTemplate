# 🧩 **B2B 통계 대시보드 생성기**

**기업 통계 분석을 위한 Spring Boot 백엔드 템플릿**  
JWT 인증, Redis 토큰 관리, Swagger 문서화, MySQL 연동 등을 포함한 템플릿입니다.

---

## 👥 **프로젝트 정보**

- **프로젝트명**: B2B 통계 대시보드 생성기
- **설명**:  
  - 기업별 로그인 및 프로젝트 테이블 선택  
  - 사용자 정의 그룹 항목 및 집계 항목 커스터마이징  
  - 조건 기반 통계 쿼리 자동 생성  
  - JSON 데이터 출력 → ApexCharts 등 시각화 연동  
- **진행 기간**: 2025.06 ~ 진행 중  
- **개발 인원**: 2명
- **URL**: https://rag-dashboard-console.vercel.app/login
- **Swagger 주소**: https://dashboardtemplate.duckdns.org/swagger-ui/index.html
- **Repository**
  - **BackEnd:** https://github.com/qldmq/dashboardTemplate
  - **FrontEnd:** https://github.com/ParkYongHo1/Rag-Dashboard-Console

### 🔧 **팀원**

|     | 이름               | 역할           | GitHub                                      |
|-----|--------------------|----------------|---------------------------------------------|
|<img src="https://github.com/qldmq.png" width="80"/>| **김서현** | 백엔드 개발     | [서현 GitHub](https://github.com/qldmq)     |
|<img src="https://github.com/ParkYongHo1.png" width="80"/> | **박용호** | 프론트엔드 개발 | [용호 GitHub](https://github.com/ParkYongHo1) |

---

## ✨ **백엔드 주요 기능**

- ✅ **JWT 기반 인증 및 인가**
- ✅ **Access / Refresh Token 분리 관리**
- ✅ **Redis**를 활용한 **Refresh Token 저장**
- ✅ **Swagger API 문서 자동화**
- ✅ **로그인/회원가입** 기능 구현
- ✅ **MySQL 기반** 데이터 저장
- ✅ **Nginx + Let's Encryp**를 통한 **HTTPS** 적용
- ✅ **Jenkins**를 이용한 **CI/CD 파이프라인** 구축 (**Docker** 환경에서 실행)

---

## 🛠️ **사용 기술 스택**

| 구분         | 기술                             |
|--------------|----------------------------------|
| **Language** | Java 17                         |
| **Framework**| Spring Boot 3.5.0              |
| **Build Tool**| Gradle                         |
| **Database** | MySQL                          |
| **ORM**      | Spring Data JPA                |
| **Auth**     | Spring Security, JWT, Redis    |
| **Docs**     | Swagger                       |
| **CI/CD**    | Jenkins                       |
| **Infra**    | AWS EC2, RDS                  |

---

## 🎯 **기대 효과** 
- ✅ 실무 기반 데이터 분석 UX 시뮬레이션
- ✅ SQL + JS 데이터 흐름 완전 이해
- ✅ 시각화 및 조건 설계 역량 증명

---

 **ERD**
![image](https://github.com/user-attachments/assets/207d2907-13ae-4c38-b652-c3a0715f89ae)

