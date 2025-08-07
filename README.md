# UBLE BACK-END REPOSITORY

## 프로젝트 소개

<div align="center">
  <img src="https://github.com/user-attachments/assets/3c016595-0424-404d-bfdb-c8068c1e1e37" alt="탐험하는 귀여운 마스코트" width="600" />
</div>

**🌐 서비스 바로가기** : https://www.u-ble.com

**💡 UBLE 이란?** : https://www.u-ble.com/intro


<br>

- **프로젝트명** : UBLE
- **프로젝트 주제** : LG U+ 멤버십 제휴처 안내 지도 서비스
- **프로젝트 기간** : 2025.06.30 - 2025.08.08


## 🧑🏻‍💻 팀원 소개
<div align="center">
  
|                                         👑 양여은                                          |                                         변하영                                          |                                                       한현우                                                       |
| :-------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------------------------------: |
|       <img width="160px" src="https://avatars.githubusercontent.com/Yyang-YE" />        |       <img width="160px" src="https://avatars.githubusercontent.com/hayong39" />        |                    <img width="160px" src="https://avatars.githubusercontent.com/Eric-HAN-01"/>                    |
|                        [@Yyang-YE](https://github.com/Yyang-YE)                         |                        [@hayong39](https://github.com/hayong39)                         |                                   [@Eric-HAN-01](https://github.com/Eric-HAN-01)                                   |
|  통계 관리 기능 <br>인프라 관리 <br> 검색 기능 <br> 즐겨찾기 기능 <br> 로깅 기능 | 인프라 관리 <br> 소셜 로그인 기능 <br> 제휴처 목록 조회 기능 <br> 제휴처 매장 추천 기능 <br> 로깅 기능 | 데이터 전처리  <br> 피드백 등록 및 조회 기능 <br> 나의 위치 관리 기능 <br> 지도 반경 필터링 기능 <br> 매장 마커 클러스터링 |

</div>

<br><br><br>

## 💡 주요 기능
### 1. 회원 기능
- 카카오 소셜 로그인 및 회원가입
- 로그아웃 및 탈퇴 
- 회원 정보 (멤버십 등급/바코드, 생년월일, 성별, 관심 카테고리) 수정
- 제휴처 즐겨찾기 등록/삭제/조회
- (통계) 가장 많이 사용한 카테고리 및 제휴처 브랜드
- (통계) 가장 많이 사용한 날짜, 요일, 시간
- (통계) 비슷한 유저 평균 대비 사용량
- (통계) 월별 사용량 내역

### 2. 제휴처 조회 기능
- 제휴처 전체 조회 및 필터링
- 제휴처 상세 조회
- 제휴처 이름/카테고리 기반 검색 및 자동완성

### 3. 지도 기반 매장 조회 기능
- 현재 또는 지정 위치 기반 근처 제휴처 매장 조회 (클러스터링 적용)
- 제휴처 매장 상세 조회 
- 제휴처 이름, 매장 이름, 카테고리 기반 검색 및 자동완성
- 자주 가는 곳 등록/삭제

### 4. 로깅 기능
- 유저별 검색 기록, 제휴처 조회 기록, 매장 조회 기록을 로깅
- 콘솔 로그 모니터링 및 에러 로그 감지 알림 시스템
- 오래된 로그를 정기적으로 S3에 업로드하여 관리

### 5. 추천 기능
- 사용자 정보(생년월일, 성별, 관심 카테고리), 로그(클릭/검색), 위치 정보 활용하여 제휴처 매장 추천
- 추천 결과 캐싱

### 6. 관리자 기능
- (통계) 제휴처/카테고리 클릭 순위
- (통계) 상위 10개 제휴처 대상 관심사 변화 추이
- (통계) 일별 인기 검색어 순위
- (통계) 결과 미포함 검색어 순위
- (통계) 서울 지역구 이용 순위
- (통계) 제휴처/카테고리 이용 순위

### 7. 피드백 기능
- 서비스에 대한 피드백 등록/조회

<br><br><br>

## 💾 DB
📸 ERD : https://www.erdcloud.com/d/C9vcCB4kCHXRWWczR
### 1. PostgreSQL
<img width="2018" height="1799" alt="Uble ERD (2)" src="https://github.com/user-attachments/assets/bbdd5306-1e79-46ac-9e53-1125e1546ba1" />

<br><br>

### 2. Elasticsearch
**1) 검색용 INDEX**

<img width="2062" height="1027" alt="ERD_SEARCH 1" src="https://github.com/user-attachments/assets/9fbde427-7051-4e91-ba2c-d657994315e4" />

<br><br>

**2) 통계용 INDEX**
   
<img width="1519" height="1061" alt="ERD_STATISTICS 2" src="https://github.com/user-attachments/assets/f46bba87-5d07-4462-8757-4bf4afef48f7" />

<br><br><br>

## 🛠️ 시스템 아키텍처
<img width="9888" height="6041" alt="Group 427320387" src="https://github.com/user-attachments/assets/80a2e554-64f7-4b73-bb20-37dbc553990b" />


---

<br><br><br>


## ⚙️ 사용 기술
### BE
  |  | 사용 기술                                                          | 역할                                        |
  |:-----------|:---------------------------------------------------------------|:------------------------------------------|
  |<img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white"/>| SpringBoot (ver. 3.5.3)                                        | Backend FrameWork                         |
  |<img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=flat&logo=springsecurity&logoColor=white"/><br><img src="https://img.shields.io/badge/OAuth2-000000?style=flat&logo=OAuth2&logoColor=white"/><br><img src="https://img.shields.io/badge/JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white"/>| Spring Security (ver.6.5.1) <br> OAuth2 <br> JWT (ver. 0.12.6) | 인증/인가 시스템 구축<br>소셜 로그인<br>stateless 인증 구현 |
  | <img src="https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=flat&logo=spring&logoColor=white"/> | Spring Data JPA (ver. 3.5.1) | 데이터 접근 계층을 단순화 및 유지보수성 향상 |
  |<img src="https://img.shields.io/badge/QueryDSL-52B0E7?style=flat&logo=querydsl&logoColor=white"/>| QueryDSL (ver. 5.1.0)                                          | 타입 안정성을 보장하는 동적 쿼리 작성                  |
  |<img src="https://img.shields.io/badge/Spring_Batch-6DB33F?style=flat&logo=spring&logoColor=white"/>| Spring Batch (ver. 3.5.3)                                      | 대량 데이터의 정기적 처리 로직 구현                      |
  |<img src="https://img.shields.io/badge/Springdoc_Swagger-6DB33F?style=flat&logo=Springdoc_Swagger&logoColor=white"/>| Springdoc Swagger (ver. 2.2.25)                                | API 명세 문서 자동 생성                           |
  |<img src="https://img.shields.io/badge/Mockito-6DB33F?style=flat&logo=mockito&logoColor=white"/><br><img src="https://img.shields.io/badge/JUnit5-25A162?style=flat&logo=junit5&logoColor=white"/>| Mockito (ver. 5.2.0) <br> JUnit5 (ver. 5.12.2)                 | 테스트 프레임워크                                 |
  |<img src="https://img.shields.io/badge/fastapi-009688?style=flat&logo=FastAPI&logoColor=white"/>| FastAPI (ver. 0.116.1)                                         | 추천 시스템 서버 구축                              |
  
<br>

### DataBase
  |  | 사용 기술                   | 역할                                   |
  |:-----------|:------------------------|:-------------------------------------|
  | <img src="https://img.shields.io/badge/postgres-%23316192.svg?style=flat&logo=postgresql&logoColor=white"/>| PostgreSQL (ver. 15.13) | 전체 데이터 저장                            |
  | <img src="https://img.shields.io/badge/pgvector-%23316192.svg?style=flat&logo=pgvector&logoColor=white"/> | PGVector                | 추천 시스템의 임베딩 기반 유사도 계산 시 vector 정보 저장 |
  |<img src="https://img.shields.io/badge/postgis-%23316192.svg?style=flat&logo=postgis&logoColor=white"/>| PostGIS                 | 특정 거리 내 매장 정보 조회 시 사용                |
  | <img src="https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white"/> | Redis                   | 스케줄러 분산 락 적용 및 세션 캐싱을 통한 응답 속도 개선    |

<br>

### Logging
|                                                                                                              | 사용 기술         | 역할                                                  |
  |:-------------------------------------------------------------------------------------------------------------|:--------------|:----------------------------------------------------|
| <img src="https://img.shields.io/badge/Filebeat-3ebeb0?style=flat&logo=filebeat&logoColor=white"/>           | Filebeat      | 컨테이너 내 JSON 로그 파일을 실시간으로 수집하여 Logstash로 전송          |
| <img src="https://img.shields.io/badge/Logstash-f3bd19?style=flat&logo=logstash&logoColor=white"/>           | Logstash      | Filebeat로부터 전달된 로그를 파싱, 변환, 필터링하여 Elasticsearch에 적재 |
| <img src="https://img.shields.io/badge/Elasticsearch-07a5de?style=flat&logo=elasticsearch&logoColor=white"/> | Elasticsearch | 검색 엔진 / 인덱스 기반으로 로그를 저장, 빠른 검색 지원                   |
| <img src="https://img.shields.io/badge/Kibana-ec407a?style=flat&logo=kibana&logoColor=white"/>               | Kibana        | 검색 결과 시각화 및 모니터링 지원 / 로그 시각화를 통해 용이한 로그 분석 가능       |


<br>

### Infra
|  |사용 기술 | 역할                |
|:-----------|:-----------|:------------------|
| <img src="https://img.shields.io/badge/AWS EC2-E5770D?style=flat&logo=amazonaws&logoColor=white"/> |AWS EC2| 서비스 배포 서버         |
| <img src="https://img.shields.io/badge/AWS RDS-4967E9?style=flat&logo=amazonaws&logoColor=white"/> |AWS RDS| 관계형 DB 호스팅        |
| <img src="https://img.shields.io/badge/AWS Route 53-8C4FFF?style=flat&logo=amazonaws&logoColor=white"/><br><img src="https://img.shields.io/badge/AWS LoadBalancer-8C4FFF?style=flat&logo=amazonaws&logoColor=white"/><br><img src="https://img.shields.io/badge/AWS Certificate Manager-D7262E?style=flat&logo=amazonaws&logoColor=white"/>| AWS Route53 <br> AWS Loadbalancer<br> AWS Certificate Manager | 도메인 및 HTTPS 적용    |
| <img src="https://img.shields.io/badge/AWS S3-E25444?style=flat&logo=amazonaws&logoColor=white"/> | AWS S3 | 제휴처 로고 및 로깅 내역 저장 |
| <img src="https://img.shields.io/badge/AWS CloudWatch-6F953F?style=flat&logo=amazonaws&logoColor=white"/><br><img src="https://img.shields.io/badge/AWS Lambda-F68536?style=flat&logo=amazonaws&logoColor=white"/>| AWS CloudWatch <br> AWS Lambda | 서버 모니터링 시스템 구축    |
| <img src="https://img.shields.io/badge/Docker-0db7ed?style=flat&logo=docker&logoColor=white"/> |Docker (ver. 28.0.1)| 개발 및 배포 환경의 컨테이너 기반 구성           |
| <img src="https://img.shields.io/badge/GitHub Actions-2671E5?style=flat&logo=githubactions&logoColor=white"/> |GitHub Actions| CI/CD 자동화         |

<br><br><br>

## ⚙️ 기술 선택 근거
| 선택 기술                                                                                                                                           | 대안                           | 비교 설명                                                                                                                                           |
|:------------------------------------------------------------------------------------------------------------------------------------------------|:-----------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------|
| PostgreSQL + PostGIS                                                                                                                            | MySQL                        | PostgreSQL은 PostGIS 확장을 통해 반경 내 매장 검색, 거리 계산 기능을 지원합니다. MySQL도 공간 쿼리를 제공하지만 정확도와 기능이 제한적이라 PostgreSQL을 선택하였습니다.                                 |
| pgvector                                                                                                                                        | Elasticsearch Vector / Faiss | pgvector는 벡터와 RDB 데이터를 하나의 DB에서 통합 관리할 수 있습니다. ElasticSearch는 텍스트 검색에는 강점이 있지만 벡터 정확도 측면에서 불리하며, Faiss는 별도의 인프라 관리가 필요하여 pgvector를 선택하였습니다. |
| ELK (Elasticsearch, Logstash, Kibana)                                                                                                           | AWS CloudWatch Agent             | ELK는 오픈소스 기반이고, Logstash을 활용한 커스텀 필드 정규화와 FastAPI와 Spring의 로그 통합이 가능합니다. CloudWatch Agent는 AWS 서비스에 종속적이며 커스텀 처리 유연성이 낮아 ELK를 선택하였습니다.                |

<br><br><br>

## 📚 스터디

<ul>
  <li><a href="https://github.com/MapChillE/uble-be/wiki/%5BSTUDY%5D-%EC%9D%B4%EC%9A%A9-%EB%82%B4%EC%97%AD-%EC%B4%88%EA%B8%B0%ED%99%94%EB%A5%BC-%EC%9C%84%ED%95%9C-%EC%8A%A4%EC%BC%80%EC%A4%84%EB%9F%AC-%EC%A0%81%EC%9A%A9%EA%B8%B0"> 📆 이용 내역 초기화를 위한 스케줄러 적용기</a></li>
  <li><a href="http://github.com/MapChillE/uble-be/wiki/%5BSTUDY%5D-%EC%A0%9C%ED%9C%B4%EC%B2%98-%EC%B6%94%EC%B2%9C-%EC%95%8C%EA%B3%A0%EB%A6%AC%EC%A6%98-%EA%B0%9C%EB%B0%9C"> ⛓️ 제휴처 추천 알고리즘 개발 </a></li>
  <li><a href="https://github.com/MapChillE/uble-be/wiki/%5BSTUDY%5D-Redis-%EC%BA%90%EC%8B%B1%EC%9D%84-%ED%86%B5%ED%95%9C-%EC%B6%94%EC%B2%9C-%EA%B2%B0%EA%B3%BC-%EC%9D%91%EB%8B%B5-%EC%86%8D%EB%8F%84-%EA%B0%9C%EC%84%A0"> ⛓️ Redis 캐싱을 통한 추천 결과 응답 속도 개선 </a></li>
  <li><a href="https://github.com/MapChillE/uble-be/wiki/%5BSTUDY%5D-%EB%A7%A4%EC%9E%A5-%ED%83%90%EC%83%89-%EA%B8%B0%EB%8A%A5-%EC%84%B1%EB%8A%A5-%EA%B0%9C%EC%84%A0"> 🗺️ 매장 탐색 기능 성능 개선 </a></li>
  <li><a href="https://github.com/MapChillE/uble-be/wiki/%5BSTUDY%5D-Elasticsearch%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EA%B2%80%EC%83%89-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84"> 🔎 Elasticsearch를 활용한 검색 기능 구현 </a></li>
</ul>

<br><br><br>

## ⚖️ 컨벤션

### **[ PACKAGE STRUCTURE ]**

```
📁 domain
 ├─ 📁 admin
 ├─ 📁 auth
 ├─ 📁 bookmark
 │   ├─ 📁 controller
 │   ├─ 📁 dto
 │   │   ├─ 📁 request
 │   │   └─ 📁 response
 │   ├─ 📁 exception
 │   ├─ 📁 repository
 │   └─ 📁 service
 ├─ 📁 brand
 ├─ 📁 category
 ├─ 📁 common
 ├─ 📁 feedback
 ├─ 📁 pin
 ├─ 📁 search
 ├─ 📁 store
 └─ 📁 users

📁 entity
 ├─ 📁 document
 └─ 📁 enums

📁 global
 ├─ 📁 config
 ├─ 📁 exception
 ├─ 📁 logging
 ├─ 📁 schedule
 └─ 📁 security

```
<br><br>

### **[ GIT ]**

**기초 규칙**

- 모든 변경은 이슈 및 브랜치 생성 후 develop 브랜치으로 병합
- main 브랜치로의 PR은 develop 브랜치에서만 생성 가능
- 생성된 PR은 최소 1명의 리뷰어의 확인 이후 병합 가능

<br>

**PR 제목 컨벤션**

| **컨벤션**      | **예시 및 설명**              |
| --------------- | ----------------------------- |
| [태그명] 제목   | [Feat] 즐겨찾기 추가 API 구현 |
| develop -> main | main 브랜치로의 PR            |

<br>

**브랜치 네이밍 컨벤션**

| **브랜치명**          | **설명**              | **예시**             |
| --------------------- | --------------------- | -------------------- |
| `main`                | 배포용 브랜치         | main                 |
| `develop`             | 개발용 브랜치         | develop              |
| `태그/UBLE-xx-도메인` | 이슈 기반 기능 브랜치 | fix/UBLE-12-bookmark |
| `태그/도메인`         | 일반 작업용 브랜치    | fix/bookmark         |

<br>

**커밋 컨벤션**

[UBLE-티켓넘버] 태그: 내용

Ex. [UBLE-12] fix: 즐겨찾기 DTO 수정

| 태그       | 설명                                             |
| ---------- | ------------------------------------------------ |
| `feat`     | 새로운 기능 추가                                 |
| `fix`      | 버그 수정                                        |
| `docs`     | 문서 변경                                        |
| `style`    | 코드 스타일 변경 (포매팅 수정, 세미콜론 추가 등) |
| `refactor` | 코드 리팩토링                                    |
| `test`     | 테스트 코드 추가 및 수정                         |
| `chore`    | 빌드 프로세스, 도구 설정 변경 등 기타 작업       |
| `comment`  | 필요한 주석 추가 및 변경                         |
| `rename`   | 파일 또는 폴더 명을 수정하거나 옮기는 작업       |
| `remove`   | 파일을 삭제하는 작업                             |

<br><br>

### **[ CODE ]**

**기초 규칙**

- Entity 및 DTO를 생성하는 경우, Builder 패턴을 사용한다.

<br>

**네이밍 컨벤션**

| 항목                   | 규칙                   | 예시                                  |
| ---------------------- | ---------------------- | ------------------------------------- |
| **클래스, 인터페이스** | PascalCase             | UserController, AuthService           |
| **컨트롤러 메소드**    | 메소드+내용            | (리스트) getUsers(), (단일) getUser() |
| **서비스 메소드**      | 컨트롤러 메소드와 동일 | (리스트) getUsers(), (단일) getUser() |
| **변수**               | camelCase              | userList, savedUser, isActive         |
| **상수**               | SNAKE_CASE             | MAX_RETRY_COUNT                       |
| **DTO**                | 파일명 + Res/Req       | GetStoreRes                           |

<br>

**예외처리 컨벤션**

- 커스텀 예외는 도메인 별로 관리되며, 아래와 같은 구조를 가진다.

```java
    // 필드
    private final HttpStatus status;
    private final int code;
    private final String message;

    // 예시
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, 5000, "매장 정보를 찾을 수 없습니다.")
```

- 각 도메인에 대해 시작 번호를 부여하여 고유한 에러 코드를 가질 수 있도록 한다.

| 도메인 | 에러 코드 |
| ------ | --------- |
| 인증   | `1000`    |
| 사용자 | `2000`    |
| 어드민 | `3000`    |
| 브랜드 | `4000`    |
| 스토어 | `5000`    |
| 북마크 | `6000`    |
| 피드백 | `7000`    |
| 핀 | `8000`    |
| 공통 | `9000`    |

---
