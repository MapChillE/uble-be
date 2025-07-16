# UBLE BACK-END

## 🧑🏻‍💻 팀원 소개
<div align="center">
  
|                                         양여은                                          |                                         변하영                                          |                                                       한현우                                                       |
| :-------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------------------------------: |
|       <img width="160px" src="https://avatars.githubusercontent.com/Yyang-YE" />        |       <img width="160px" src="https://avatars.githubusercontent.com/hayong39" />        |                    <img width="160px" src="https://avatars.githubusercontent.com/Eric-HAN-01"/>                    |
|                        [@Yyang-YE](https://github.com/Yyang-YE)                         |                        [@hayong39](https://github.com/hayong39)                         |                                   [@Eric-HAN-01](https://github.com/Eric-HAN-01)                                   |
| 👑 BE 개발 팀장 <br>인프라 관리 <br> 검색 기능 <br> 즐겨찾기 기능 <br> 로깅 기능 | 인프라 관리 <br> 소셜 로그인 기능 <br> 제휴처 목록 조회 기능 <br> 제휴처 매장 추천 기능 <br> 로깅 기능 | 데이터 전처리 <br>통계 관리 기능 <br> 피드백 등록 및 조회 기능 <br> 나의 위치 관리 기능 <br> 지도 반경 필터링 기능 |

</div>

<br><br><br>

## 💡 주요 기능
### 1. 회원 기능
- 카카오 소셜 로그인 및 회원가입
- 회원 정보(멤버십 등급/바코드, 생년월일, 성별, 관심 카테고리) 수정
- 제휴처 즐겨찾기 등록/삭제/조회
-  카테고리 별 즐겨찾기, 이용 횟수 비율 조회
- 로그아웃 및 탈퇴 

### 2. 제휴처 조회 기능
- 제휴처 전체 조회 및 필터링
- 제휴처 상세 조회
- 제휴처 이름/카테고리 기반 검색 및 자동완성

### 3. 지도 기반 매장 조회 기능
- 현재 또는 지정 위치 기반 근처 제휴처 매장 조회
- 제휴처 매장 상세 조회
- 제휴처 이름, 매장 이름, 카테고리 기반 검색 및 자동완성
- 자주 가는 곳 등록/삭제

### 4. 로깅 기능
- 유저별 검색 기록, 제휴처 조회 기록, 매장 조회 기록을 로깅
- 오래된 로그들은 주기적으로 S3에 업로드하여 관리

### 5. 추천 기능
- 사용자 정보(생년월일, 성별, 관심 카테고리), 로그(클릭/검색), 위치 정보 활용하여 제휴처 매장 추천
- 추천 결과 캐싱

### 6. 관리자 기능
- 사용자 이용 내역 카테고리 내 제휴사 순위 조회
- 사용자 이용 내역 월단위 조회
- 즐겨찾기 카테고리 내 제휴사 순위 조회

### 7. 피드백 기능
- 서비스에 대한 피드백 등록/조회

<br><br><br>

## 📌 ERD
<img width="2331" height="1821" alt="Uble ERD" src="https://github.com/user-attachments/assets/187e2db7-844b-488f-b829-ede63f6c01e5" />

<br><br><br>

## 🛠️ 시스템 아키텍처
<img width="9497" height="6106" alt="Group 427320379" src="https://github.com/user-attachments/assets/ca1886db-c0ec-46b5-9e1d-4796caafb603" />

---

<br><br><br>


## ⚙️ 사용 기술
### BE
  |  |사용 기술 |역할|
  |:-----------|:-----------|:---------------|
  |<img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white"/>|SpringBoot <br> (ver. ) | Backend FrameWork |
  |<img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=flat&logo=springsecurity&logoColor=white"/><br><img src="https://img.shields.io/badge/OAuth2-000000?style=flat&logo=OAuth2&logoColor=white"/><br><img src="https://img.shields.io/badge/JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white"/>| Spring Security <br> OAuth2 <br> JWT | 인증/인가 시스템 구축<br>소셜 로그인<br>stateless 인증 구현 |
  |<img src="https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=flat&logo=spring-data-jpa&logoColor=white"/>| Spring Data JPA |  |
  |<img src="https://img.shields.io/badge/QueryDSL-52B0E7?style=flat&logo=querydsl&logoColor=white"/>| QueryDSL |  |
  |<img src="https://img.shields.io/badge/Spring_Batch-6DB33F?style=flat&logo=spring-batch&logoColor=white"/>| Spring Batch |  |
  |<img src="https://img.shields.io/badge/Springdoc_Swagger-6DB33F?style=flat&logo=Springdoc_Swagger&logoColor=white"/>| Springdoc Swagger | API 명세 문서 자동 생성 |
  |<img src="https://img.shields.io/badge/Mockito-6DB33F?style=flat&logo=mockito&logoColor=white"/><br><img src="https://img.shields.io/badge/JUnit5-25A162?style=flat&logo=junit5&logoColor=white"/>| Mockito <br> JUnit5 | 테스트 프레임워크 |
  |<img src="https://img.shields.io/badge/ElasticSearch-005571?style=flat&logo=elasticsearch&logoColor=white"/>| Elasticsearch |  |
  |<img src="https://img.shields.io/badge/Kibana-005571?style=flat&logo=kibana&logoColor=white"/>| Kibana |  |
  |<img src="https://img.shields.io/badge/fastapi-009688?style=flat&logo=FastAPI&logoColor=white"/>| FastAPI | 추천 시스템 서버 구축 |
<br>

### DataBase
  |  |사용 기술 | 역할 |
  |:-----------|:-----------|:---------------|
  | <img src="https://img.shields.io/badge/postgres-%23316192.svg?style=flat&logo=postgresql&logoColor=white"/>|PostgreSQL <br> (ver. ) | 유저, 제휴처 관련 정보 저장 |
  | <img src="https://img.shields.io/badge/pgvector-%23316192.svg?style=flat&logo=pgvector&logoColor=white"/> | PGVector | 추천 시스템의 임베딩 기반 유사도 계산 시, vector 정보 저장 |
  |<img src="https://img.shields.io/badge/postgis-%23316192.svg?style=flat&logo=postgis&logoColor=white"/>| PostGIS | 특정 거리 내 매장 정보 조회 시 사용 |
  | <img src="https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white"/> | Redis | 세션 캐싱 및 응답 속도 개선 |

<br>

### Infra
|  |사용 기술 | 역할 |
|:-----------|:-----------|:---------------|
| <img src="https://img.shields.io/badge/AWS EC2-E5770D?style=flat&logo=amazonaws&logoColor=white"/> |AWS EC2| 서비스 배포 서버 |
| <img src="https://img.shields.io/badge/AWS RDS-4967E9?style=flat&logo=amazonaws&logoColor=white"/> |AWS RDS| 관계형 DB 호스팅 |
| <img src="https://img.shields.io/badge/AWS Route 53-8C4FFF?style=flat&logo=amazonaws&logoColor=white"/><br><img src="https://img.shields.io/badge/AWS LoadBalancer-8C4FFF?style=flat&logo=amazonaws&logoColor=white"/><br><img src="https://img.shields.io/badge/AWS Certificate Manager-D7262E?style=flat&logo=amazonaws&logoColor=white"/>| AWS Route53 <br> AWS Loadbalancer<br> AWS Certificate Manager |  |
| <img src="https://img.shields.io/badge/AWS S3-E25444?style=flat&logo=amazonaws&logoColor=white"/> | AWS S3 |  |
| <img src="https://img.shields.io/badge/AWS CloudWatch-6F953F?style=flat&logo=amazonaws&logoColor=white"/><br><img src="https://img.shields.io/badge/AWS Lambda-F68536?style=flat&logo=amazonaws&logoColor=white"/>| AWS CloudWatch <br> AWS Lambda |  |
| <img src="https://img.shields.io/badge/Docker-0db7ed?style=flat&logo=docker&logoColor=white"/> |Docker| 컨테이너 환경 |
| <img src="https://img.shields.io/badge/GitHub Actions-2671E5?style=flat&logo=githubactions&logoColor=white"/> |GitHub Actions| CI/CD 자동화 |


<br><br><br>

## ⚖️ 컨벤션

### **[ PACKAGE STRUCTURE ]**

```
📁 domain
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
 ├─ 📁 feedback
 ├─ 📁 store
 └─ 📁 users

📁 entity
 ├─ 📁 document
 └─ 📁 enums

📁 global
 ├─ 📁 config
 ├─ 📁 exception
 ├─ 📁 schedule
 └─ 📁 security

```
<br><br>

### **[ GIT ]**

**기초 규칙**

- 모든 변경은 이슈 및 브랜치 생성 후 develop 브랜치으로 병합
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
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, 5000, "매장 정보를 찾을 수 없습니다."),
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

<br><br><br>

## [📚 Github Wiki]()

<ul>
<li><a href=""> 🛠️ 프로젝트 수행 현황</a></li>
<li><a href=""> 📑 Team Convention</a></li>
<li><a href=""> 🏗️ DB 설계 </a></li>
<li><a href=""> 💬 추천 시스템 개발</a></li>
</ul>

---
