# 🧩 **DashboardTemplate**

**기업 통계 분석을 위한 Spring Boot 백엔드 템플릿**  
JWT 인증, Redis 토큰 관리, Swagger 문서화, MySQL 연동 등을 포함한 템플릿입니다.

---

## 👥 **프로젝트 정보**

- **프로젝트명**: DashboardTemplate  
- **설명**:  
  기업의 주문, 매출 등의 데이터를 수집하고, 사용자가 원하는 조건(예: 날짜, 지역, 상품 등)으로 그룹핑하여 합계, 평균, 최댓값, 최솟값 등의 통계 정보를 산출해주는 백엔드 대시보드 템플릿입니다.  
  Spring Boot 기반 구조로 다양한 기업용 대시보드 시스템에 활용할 수 있습니다.
- **진행 기간**: 2025.06 ~ 진행 중  
- **개발 인원**: 2명

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
- ✅ **HTTPS (JKS/PKCS12)** 설정 지원

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

