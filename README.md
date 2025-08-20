# UBLE BACK-END REPOSITORY

## 프로젝트 소개

<div align="center">
  🏆LG 유플러스 유레카 SW 아카데미 2기 융합프로젝트 우수상🏅
  
  <img src="https://github.com/user-attachments/assets/3c016595-0424-404d-bfdb-c8068c1e1e37" alt="탐험하는 귀여운 마스코트" width="600" />
</div><br>

**🌐 서비스 바로가기** : https://www.u-ble.com

**📊 관리자 페이지 바로가기** : https://admin.u-ble.com

**💡 UBLE 이란?** : https://www.u-ble.com/intro


<br>

- **프로젝트명** : UBLE
- **프로젝트 주제** : LG U+ 멤버십 제휴처 안내 지도 서비스
- **프로젝트 기간** : 2025.06.30 - 2025.08.08

<br><br>

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

## 📚 스터디

<ul>
  <li><a href="https://github.com/MapChillE/uble-be/wiki/%5BSTUDY%5D-%EC%9D%B4%EC%9A%A9-%EB%82%B4%EC%97%AD-%EC%B4%88%EA%B8%B0%ED%99%94%EB%A5%BC-%EC%9C%84%ED%95%9C-%EC%8A%A4%EC%BC%80%EC%A4%84%EB%9F%AC-%EC%A0%81%EC%9A%A9%EA%B8%B0"> 📆 이용 내역 초기화를 위한 스케줄러 적용기</a></li>
  <li><a href="http://github.com/MapChillE/uble-be/wiki/%5BSTUDY%5D-%EC%A0%9C%ED%9C%B4%EC%B2%98-%EC%B6%94%EC%B2%9C-%EC%95%8C%EA%B3%A0%EB%A6%AC%EC%A6%98-%EA%B0%9C%EB%B0%9C"> ⛓️ 제휴처 추천 알고리즘 개발 </a></li>
  <li><a href="https://github.com/MapChillE/uble-be/wiki/%5BSTUDY%5D-Redis-%EC%BA%90%EC%8B%B1%EC%9D%84-%ED%86%B5%ED%95%9C-%EC%B6%94%EC%B2%9C-%EA%B2%B0%EA%B3%BC-%EC%9D%91%EB%8B%B5-%EC%86%8D%EB%8F%84-%EA%B0%9C%EC%84%A0"> ⛓️ Redis 캐싱을 통한 추천 결과 응답 속도 개선 </a></li>
  <li><a href="https://github.com/MapChillE/uble-be/wiki/%5BSTUDY%5D-%EB%A7%A4%EC%9E%A5-%ED%83%90%EC%83%89-%EA%B8%B0%EB%8A%A5-%EC%84%B1%EB%8A%A5-%EA%B0%9C%EC%84%A0"> 🗺️ 매장 탐색 기능 성능 개선 </a></li>
  <li><a href="https://github.com/MapChillE/uble-be/wiki/%5BSTUDY%5D-Elasticsearch%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EA%B2%80%EC%83%89-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84"> 🔎 Elasticsearch를 활용한 검색 기능 구현 </a></li>
</ul>

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

## ⚖️ 컨벤션
- 🗂️ [패키지 구조 컨벤션](https://github.com/MapChillE/uble-be/wiki/CONVENTION#-package-structure-)
- ⚙️ [GIT 컨벤션](https://github.com/MapChillE/uble-be/wiki/CONVENTION#-git-)
- 💻 [CODE 컨벤션](https://github.com/MapChillE/uble-be/wiki/CONVENTION#-code-)

<br><br><br>

## 💻 Screens

| 화면 | 기능                                                                                                                     |
| ---- |------------------------------------------------------------------------------------------------------------------------|
| <img src="https://github.com/user-attachments/assets/76b19543-f01b-48cb-9c77-9a878d8a07e9" width="250"/> | ✨ **사용자 정보 및 관심사 기반 제휴처 추천** <br><br> 사용자의 나이, 성별, 관심 카테고리 등 프로필 정보를 분석하여 맞춤형 LGU+ 멤버십 제휴처를 추천합니다.                     |
| <img src="https://github.com/user-attachments/assets/b3974a9b-e678-4c2b-a7ba-8a0338bff2b8" width="250" /> | 🎁 **LGU+ 멤버십 제휴처 혜택 정보** <br><br> 각 제휴처의 상세한 혜택 정보를 조회하고, 할인 및 이벤트 등의 최신 정보를 제공합니다.                                   |
| <img width="250" alt="image" src="https://github.com/user-attachments/assets/c08a0d69-2f35-4467-b507-dddccaa658d1" /> | 🔥 **실시간 인기 멤버십 제휴처 정보** <br><br> 현재 시간 기준으로 사용자들이 가장 많이 방문한 인기 제휴처 정보를 실시간으로 제공합니다.                                   |
| <img width="250" src="https://github.com/user-attachments/assets/2ce870f6-40bf-45b5-9f1a-146292bebb33" /> | 🔍 **자동 완성 기능을 탑재한 검색 기능 (제휴처)** <br><br> 제휴처 및 키워드 검색 시, 사용자 입력에 따라 자동 완성 및 추천 검색어를 실시간으로 제공합니다.                      |
| <img src="https://github.com/user-attachments/assets/9abc8d76-dcf9-4542-aa60-8b03aed299b9" width="250" /> | 🗺 **지도 기반 제휴처 탐색** <br><br> 사용자의 현재 위치 주변에 존재하는 LGU+ 제휴처를 지도상에서 확인할 수 있으며, 매장 클릭 시 상세 정보가 제공됩니다.                      |
| <img src="https://github.com/user-attachments/assets/0e38e285-9195-4ac9-815f-aff245c5cc4b" width="250" /> | 📌 **사용자 지정 위치 저장** <br><br> 사용자가 자주 방문하거나 관심 있는 위치를 지정하여 빠르게 탐색할 수 있도록 저장할 수 있습니다.                                    |
| <img src="https://github.com/user-attachments/assets/52879155-6c4a-416c-8156-6a2dee63c1f1" width="250" /> | ⚡ **자동 완성 기능을 탑재한 제휴처 매장 검색 기능 (지도)** <br><br> 제휴처 이름이나 키워드를 입력할 때, 관련 매장을 자동 완성 형태로 실시간으로 제안하여 빠르게 탐색할 수 있습니다.        |
| <img src="https://github.com/user-attachments/assets/9d909a53-6c90-441e-8dc3-3321a3d143fe" width="250" /> | ⭐ **제휴처 즐겨찾기** <br><br> 관심 있는 제휴처를 즐겨찾기로 저장하여 언제든지 쉽게 다시 확인할 수 있도록 지원합니다.                                              |
| <img src="https://github.com/user-attachments/assets/6d8bd429-9338-4e60-afbf-dc447cba1a7a" width="250" /> | ⚙️ **사용자 정보 및 관심 카테고리 등 설정 지원(마이페이지)** <br><br> 사용자 프로필 정보, 관심 카테고리, 멤버십 등급, 바코드 번호 등 다양한 설정을 마이페이지에서 관리할 수 있도록 지원합니다. |
| <img src="https://github.com/user-attachments/assets/35685677-aa6f-4b41-9a97-42cfe27036aa" width="250" /> | 🎫 **바코드 기반 멤버십 혜택 사용** <br><br> 사용자가 등록한 바코드 번호를 기반으로 실제 사용할 수 있는 멤버십 바코드를 자동 생성하여 제휴처에서 혜택을 받을 수 있도록 지원합니다.          |
| <img src="https://github.com/user-attachments/assets/43d92131-a5c5-480c-b055-5e438d0016fd" width="250" /> | 📊 **유저 개인 및 전체 사용자 평균 기준 혜택 사용 통계 조회** <br><br> 개인의 혜택 사용량과 전체 사용자 평균 사용량을 비교하여 시각적으로 제공합니다.                          |
| <img src="https://github.com/user-attachments/assets/08eaa0d0-474e-4597-adb7-b42bf37376be" width="250" /> | 📝 **혜택 사용 내역 조회** <br><br> 사용자가 실제 사용한 제휴처 혜택의 사용 내역과 날짜, 사용처, 할인율 등의 상세 정보를 조회할 수 있도록 제공합니다.                         |
