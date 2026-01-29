# 띵듀로이드 (Ddingduroid)

<p align="center">
  <img
    src="https://github.com/user-attachments/assets/518d5a61-4491-4319-9281-cc4ca42f7b4f"
    width="250"
    height="250"
    style="border-radius: 16px;"
  />
</p>

## 목차
- [프로젝트 소개](#프로젝트-소개)
- [개발 기간](#개발-기간)
- [팀 구성](#팀-구성)
- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [시스템 아키텍처](#시스템-아키텍처)
- [핵심 기술 구현](#핵심-기술-구현)
- [트러블슈팅](#트러블슈팅)
- [프로젝트 회고](#프로젝트-회고)

## 프로젝트 소개

띵듀로이드는 명지대학교 학생들이 강의 정보를 쉽고 빠르게 얻을 수 있도록 돕는 **RAG 기반 AI 챗봇 서비스**입니다.

기존 학사 정보 시스템(MSI)의 낙후된 UI/UX와 복잡한 정보 접근성 문제를 해결하기 위해, 자연어 질의응답을 통해 강의계획서의 세부 정보를 실시간으로 제공합니다.

### 프로젝트 배경

- **52.8%의 대학생**이 학사 관리 시스템에 불만족
- 53.3%의 높은 수치로 명지대 MSI가 시스템 UI/UX 낙후 및 비개인화 등 '개선이 시급한 사례'에 해당
- 강의계획서 PDF를 일일이 열람해야 하는 비효율성
- 수강신청 시 필요한 정보(평가 비율, 과제 여부 등)를 빠르게 비교하기 어려움

### 해결 방안

**"수십 개의 강의계획서 PDF를 일일이 열어보지 않고도, 단 몇 초 만에 원하는 정보를 검색한다"**

- 자연어 질의응답을 통한 직관적인 정보 검색
- RAG 기반 강의 제공으로 정확한 답변 생성
- 학생 맞춤형 배경 정보 활용 (전공, 수강 중인 강의 등)

## 개발 기간

**2025.11.20 ~ 2025.11.21** (AI·EduTech 해커톤 2일)

## 팀 구성

**총 6명**
- Backend: 3명
- Frontend: 1명
- Designer: 1명
- Planner: 1명

## 주요 기능

### 1. 사용자 인증 및 권한 관리

- JWT 기반 Access Token / Refresh Token 발급
- 명지대학교 이메일(@mju.ac.kr) 인증 시스템
- Access Token 블랙리스트 관리 (로그아웃 처리)
- 비밀번호 재설정 및 변경 기능

### 2. AI 기반 강의 정보 챗봇 🔥

#### RAG (Retrieval-Augmented Generation) 시스템
- **BM25 키워드 기반 검색** + **Vector 의미 기반 검색** 하이브리드 방식
- 강의계획서 PDF를 벡터 DB에 저장하여 정확한 정보 제공
- 사용자 맥락 정보 활용 (전공, 수강 중인 강의 등)

#### 자연어 질의응답
- "중간고사 없는 융소 전공 과목 알려줘"
- "데이터사이언스전공 교수님별 강의평 알려줘"
- "AI 관련 수업 전부 찾아줘"

### 3. 강의 검색 및 보관함

- 강의명, 교수명, 강좌번호로 강의 검색
- **Full-Text Search (MySQL)**: 한글 강의명에 대한 고속 검색
- 강의 보관함 기능으로 관심 강의 즐겨찾기
- 보관한 강의 기반 맞춤형 AI 추천

### 4. 사용자 정보 관리

- 학번, 이름, 이메일, 전공 정보 관리
- 비밀번호 변경 및 회원 탈퇴 기능
- 마이페이지를 통한 개인정보 조회

## 기술 스택

### Frontend
![Next.js](https://img.shields.io/badge/Next.js_14-000000?style=for-the-badge&logo=nextdotjs&logoColor=white)
![React](https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white)

### Backend
![Java](https://img.shields.io/badge/Java_17-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.3.1-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)

### Database
![MySQL](https://img.shields.io/badge/MySQL_8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

### AI & ML
![FastAPI](https://img.shields.io/badge/FastAPI-009688?style=for-the-badge&logo=fastapi&logoColor=white)
![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white)
![ChromaDB](https://img.shields.io/badge/ChromaDB-FF6B6B?style=for-the-badge)
![Gemini](https://img.shields.io/badge/Google_Gemini-4285F4?style=for-the-badge&logo=google&logoColor=white)

### Infrastructure
![AWS EC2](https://img.shields.io/badge/AWS_EC2-FF9900?style=for-the-badge&logo=amazon-aws&logoColor=white)
![AWS RDS](https://img.shields.io/badge/AWS_RDS-527FFF?style=for-the-badge&logo=amazon-rds&logoColor=white)
![Vercel](https://img.shields.io/badge/Vercel-000000?style=for-the-badge&logo=vercel&logoColor=white)
![AWS ECR](https://img.shields.io/badge/AWS_ECR-FF9900?style=for-the-badge&logo=amazon-aws&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)

### Tools
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=json-web-tokens&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![MapStruct](https://img.shields.io/badge/MapStruct-E34F26?style=for-the-badge)

## 시스템 아키텍처
<img width="1328" height="660" alt="image" src="https://github.com/user-attachments/assets/d037d4ec-4a19-4da0-a883-a6d6625623b3" />


## 핵심 기술 구현

### 1. RAG 기반 AI 챗봇 시스템 🔥

#### 하이브리드 검색 방식
```
1. 키워드 기반 검색 (BM25)
   → 강의명, 교수명 등 정확한 키워드 매칭
   
2. 의미 기반 검색 (Vector)
   → 문맥을 이해하여 유사한 의미의 강의 검색
   
3. 결과 통합
   → 두 방식의 결과를 결합하여 안정적인 검색 성능 확보
```

#### FastAPI 서버와의 통신
- **RestClient**를 활용한 동기 HTTP 통신
- **5분 Read Timeout** 설정으로 AI 응답 대기 시간 확보
- SimpleClientHttpRequestFactory로 타임아웃 세밀하게 제어
- Flux<String>으로 래핑하여 반환 (향후 스트리밍 확장 가능)

### 2. JWT 기반 인증 시스템

#### 토큰 구조
- **Access Token**: 1시간 유효, API 인증에 사용
- **Refresh Token**: 7일 유효, MySQL에 저장하여 관리

#### 보안 강화
- 로그아웃 시 Access Token을 블랙리스트에 등록 (DB 저장)
- Refresh Token은 DB에서 즉시 삭제
- JWT 필터에서 블랙리스트 검증
- 만료 시간 기반 자동 정리로 DB 용량 최적화

### 3. MySQL Full-Text Search를 활용한 강의 검색

#### 한글 검색 최적화
- **FULLTEXT INDEX** + **ngram 파서** 사용
- O(log N) 검색 성능으로 빠른 조회
- **BOOLEAN MODE**를 활용한 유연한 검색
- 강의명 검색 시 부분 일치 지원

#### 검색 방식
- 강의명: Full-Text Search (MATCH ... AGAINST)
- 교수명: LIKE 검색 (CONTAINING)
- 강좌번호: 정확한 일치 검색

### 4. 이메일 인증 시스템

#### 6자리 인증 코드 전송
- **JavaMailSender**를 활용한 Gmail SMTP 연동
- 5분 유효 시간 설정
- 1분 재전송 쿨다운 방지
- 인증 완료 후 DB에서 자동 삭제

#### 인증 프로세스
1. 사용자가 명지대 이메일(@mju.ac.kr) 입력
2. 6자리 랜덤 인증 코드 생성 및 전송
3. 사용자가 코드 입력 후 검증
4. 인증 완료 시 회원가입 진행 가능

### 5. CI/CD 파이프라인

#### GitHub Actions + Docker + AWS EC2
- main 브랜치 푸시 시 자동 배포 트리거
- Gradle 빌드 후 Docker 이미지 생성
- AWS ECR에 이미지 푸시
- EC2에서 Docker Compose로 컨테이너 재시작

#### 배포 프로세스
1. Java 17 환경에서 Gradle 빌드 (테스트 제외)
2. Docker 이미지 빌드 및 ECR 푸시
3. compose.yml을 EC2로 복사
4. EC2에서 최신 이미지 Pull 후 컨테이너 재시작
5. 사용하지 않는 이미지 자동 정리

## 트러블슈팅

### 1. CI/CD 파이프라인 구축 및 자동 배포 🔥

#### 문제 상황
- 수동 배포 시 반복적인 빌드/배포 작업으로 인한 시간 소요
- EC2 SSH 직접 접근으로 인한 보안 위험
- 환경 변수 관리의 어려움 (DB 연결 정보, JWT Secret 등)

#### 해결 과정

**1단계: GitHub Actions 기반 CI/CD 파이프라인 구축**
- main 브랜치 푸시 시 자동으로 빌드/배포 트리거
- Java 17 환경에서 Gradle 빌드 수행 (테스트 제외)
- AWS ECR에 Docker 이미지 자동 푸시

**2단계: Docker/Docker-Compose 기반 컨테이너화**
- Dockerfile로 애플리케이션 이미지 생성
- Docker Compose로 컨테이너 배포 및 환경 변수 주입
- 일관된 실행 환경 보장

**3단계: AWS 인프라 구성**
- **Amazon ECR**: Docker 이미지 저장소로 활용
- **Amazon EC2**: Ubuntu 24 기반 애플리케이션 서버
- **Amazon RDS(MySQL)**: 관리형 데이터베이스 서비스

#### 결과
- ✅ main 브랜치 푸시 시 자동 빌드/배포 (약 5분 소요)
- ✅ Docker 기반 컨테이너화로 환경 일관성 확보
- ✅ GitHub Secrets를 통한 안전한 환경 변수 관리
- ✅ 이전 이미지 태그로 즉시 롤백 가능

### 2. Docker Compose 환경 변수 주입 문제

#### 문제 상황
- GitHub Actions Secrets를 EC2의 Docker Compose에 전달 실패
- 환경 변수가 컨테이너 내부에서 `null`로 인식
- 배포 후 DB 연결 실패 및 애플리케이션 시작 불가

#### 해결 방법
- GitHub Actions의 `appleboy/ssh-action`을 통해 EC2에 SSH 접속
- `envs` 파라미터로 환경 변수 목록 명시
- EC2 내부에서 `export` 명령어로 환경 변수를 명시적으로 설정
- Docker Compose 실행 시 환경 변수가 컨테이너로 전달되도록 구성

#### 결과
- ✅ GitHub Secrets가 EC2로 안전하게 전달
- ✅ 환경 변수 기반 설정으로 보안 강화 (코드에 민감 정보 미포함)
- ✅ 배포 자동화 완성

### 3. Amazon RDS(MySQL) 연결 최적화

#### 문제 상황
- 초기 RDS 퍼블릭 액세스로 인한 보안 취약점
- 외부에서 DB에 직접 접근 가능한 위험성

#### 해결 방법
- **Security Group 설정**: RDS는 EC2 인스턴스에서만 3306 포트 접근 허용
- **VPC 내부 통신**: RDS와 EC2를 같은 VPC에 배치하여 프라이빗 통신
- **환경 변수 분리**: DB 연결 정보를 GitHub Secrets로 관리

#### 결과
- ✅ 안전한 DB 연결 (VPC 내부 통신)
- ✅ 외부에서 RDS 직접 접근 차단
- ✅ 적절한 타임아웃 설정으로 안정적인 연결 유지

### 4. SSL/TLS 적용 및 도메인 연결

#### 문제 상황
- HTTP 통신으로 인한 보안 취약점
- 프론트엔드(HTTPS)에서 백엔드(HTTP) 호출 시 Mixed Content 에러 발생
- IP 주소 기반 접근으로 인한 낮은 접근성

#### 해결 방법
- **도메인 연결**: `ddingdu.p-e.kr` 도메인 구매 및 EC2 연결
- **리버스 프록시**: Nginx로 80 → 8080 포트 포워딩
- **SSL 인증서**: Let's Encrypt를 통한 무료 SSL 인증서 발급 및 자동 갱신

#### 결과
- ✅ HTTPS 통신으로 보안 강화
- ✅ Mixed Content 에러 해결
- ✅ 도메인을 통한 접근성 향상
- ✅ 자동 SSL 인증서 갱신으로 유지보수 간소화

### 5. Gradle 빌드 최적화

#### 문제 상황
- CI/CD 파이프라인에서 빌드 시간 과다 소요 (약 3~4분)
- 불필요한 테스트 실행으로 인한 시간 낭비
- 빌드 결과물이 캐싱되지 않아 매번 전체 빌드 수행

#### 해결 방법
- Gradle 빌드 시 `-x test` 옵션으로 테스트 제외
- `.gitignore`에 불필요한 빌드 산출물 제외
- GitHub Actions 캐싱 전략 적용 (Gradle 의존성 캐싱)

#### 결과
- ✅ 빌드 시간 약 50% 단축 (3~4분 → 1~2분)
- ✅ CI/CD 파이프라인 전체 시간 단축
- ✅ 빠른 배포 사이클 확보

## 프로젝트 회고

### 배운 점
1. **RAG 시스템**: BM25 + Vector 하이브리드 검색을 통한 정확한 정보 검색
2. **RestClient 통신**: FastAPI 서버와의 효율적인 HTTP 통신 구현
3. **인증 시스템**: JWT 기반 인증 및 블랙리스트 관리
4. **Full-Text Search**: MySQL을 활용한 한글 검색 최적화
5. **CI/CD**: GitHub Actions + Docker + AWS를 통한 자동 배포

### 개선 사항
- **Redis 도입**: Access Token 블랙리스트를 Redis로 관리하여 성능 향상
- **스트리밍 응답**: Server-Sent Events(SSE)를 통한 실시간 AI 응답 스트리밍
- **캐싱**: 자주 검색되는 강의 정보 캐싱으로 응답 속도 개선
- **모니터링**: Prometheus + Grafana를 통한 실시간 모니터링
- **테스트 코드**: 단위 테스트 및 통합 테스트 작성

### 향후 계획
- 강의 평가 시스템 추가
- 시간표 자동 생성 기능
- 학생 간 강의 정보 공유 커뮤니티
- 교수님별 강의 스타일 분석 기능

## 링크

- **GitHub Repository**: [프로젝트 링크](https://github.com/dding-du/ddingdu-backend)
- **프로젝트 발표 자료**: [PDF 링크](https://drive.google.com/file/d/1u9m5rxzT1IBJK4EGJYRx2wK2E8EBC2xb/view?usp=drive_link)
- ~~**배포 URL**: [https://ddingdu.p-e.kr](https://ddingdu.p-e.kr)~~ (AWS 비용 문제로 운영 중단)
- ~~**Swagger API 문서**: [https://ddingdu.p-e.kr/swagger-ui/index.html](https://ddingdu.p-e.kr/swagger-ui/index.html)~~ (운영 중단)
