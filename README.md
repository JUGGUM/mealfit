# 🥗 MealFit

**AI 기반 개인 맞춤형 식단 추천 서비스**

MealFit는 사용자의 건강 정보와 식습관을 분석하여 OpenAI GPT 모델을 활용한 맞춤형 식단을 제공하는 웹 서비스입니다.
**클린 아키텍처(Clean Architecture)**와 **이벤트 기반 아키텍처(Event-Driven Architecture)**를 적용하여 확장성, 유지보수성, 테스트 용이성을 극대화했습니다.

> 🍽️ "식생활, 이제 기술로 바꿉니다."

---

## 🔗 배포 주소

- **Render 배포 버전**: [https://mealfit.onrender.com/login](https://mealfit.onrender.com/login)
- **Swagger API 문서**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) (로컬)
- **AWS 무중단 롤링 배포 전략** (예정)

---

## 📋 목차

- [프로젝트 개요](#-프로젝트-개요)
- [기술적 하이라이트](#-기술적-하이라이트)
- [기술 스택](#️-기술-스택)
- [아키텍처](#️-아키텍처)
- [주요 기능](#-주요-기능)
- [프로젝트 구조](#-프로젝트-구조)
- [실행 방법](#-실행-방법)
- [모니터링](#-모니터링-prometheus--grafana)
- [k6 부하 테스트](#-k6-부하-테스트)
- [테스트](#-테스트)
- [설계 패턴](#-설계-패턴)
- [성능 최적화](#-성능-최적화)
- [향후 계획](#️-향후-계획)

---

## 🧩 프로젝트 개요

| 항목 | 설명 |
|------|------|
| **프로젝트명** | MealFit - AI 기반 식단 추천 서비스 |
| **개발 기간** | 2025.06 ~ 진행 중 |
| **개발 인원** | 1인 (개인 프로젝트) |
| **설계 방식** | DDD (Domain-Driven Design) + Clean Architecture |
| **아키텍처** | Event-Driven Architecture (Kafka 기반) |
| **인증 방식** | JWT (Access Token + Refresh Token) |
| **비동기 처리** | Apache Kafka + Spring Event + @Async |
| **동시성 제어** | Redisson 분산 락 |
| **AI 통합** | OpenAI GPT-4o-mini API |

---

## 💡 기술적 하이라이트

### 1. 🏗️ Clean Architecture 적용

계층별 명확한 역할 분리로 비즈니스 로직의 독립성을 확보했습니다.

```
Presentation Layer  → Controller (REST API)
Application Layer   → UseCase, Service, Event Handler
Domain Layer        → Entity, Value Object, Domain Event
Infrastructure Layer → Repository, External API Client, Messaging
```

- **의존성 역전 원칙(DIP)**: 도메인 계층이 인프라에 의존하지 않도록 포트-어댑터 패턴 적용
- **단일 책임 원칙(SRP)**: 각 계층이 명확한 책임을 가지고 변경 사유가 단일화

### 2. 🔄 Event-Driven Architecture

도메인 이벤트와 Kafka를 활용한 느슨한 결합 구조로 확장성을 확보했습니다.

**이벤트 흐름:**
```
회원가입 요청 → SignUpService (User 저장)
              ↓
       UserSignedUpEvent 발행
              ↓
@TransactionalEventListener (DB 커밋 후)
              ↓
    Kafka Topic 'user.signed-up' 발행
              ↓
     [비동기 처리: 알림 발송, 통계 수집 등]
```

**장점:**
- 서비스 간 결합도 감소
- 장애 전파 방지
- 비동기 처리로 응답 속도 향상

### 3. 🔐 분산 락을 통한 동시성 제어

Redisson을 활용한 분산 락으로 동시 가입 요청 시 이메일 중복 문제를 해결했습니다.

```kotlin
val lockKey = "lock:signup:${request.email}"
val lock = redissonClient.getLock(lockKey)

if (!lock.tryLock(2, 5, TimeUnit.SECONDS)) {
    throw IllegalStateException("다른 가입 요청이 처리 중입니다.")
}
```

**효과:** 분산 환경에서 안전한 중복 체크, Race Condition 방지

### 4. 🎯 전략 패턴을 활용한 멀티 로그인

다양한 로그인 방식을 전략 패턴으로 유연하게 확장할 수 있도록 설계했습니다.

```kotlin
interface LoginStrategy {
    fun login(request: LoginRequest): LoginResult
}

// 구현체
- LocalLoginStrategy    (일반 로그인)
- KakaoLoginStrategy    (카카오 로그인)
- NaverLoginStrategy    (네이버 로그인)
```

**확장성:** 새로운 OAuth 제공자 추가 시 기존 코드 수정 없이 전략 추가만으로 확장 가능

### 5. 🤖 AI 통합 (OpenAI GPT-4o-mini)

사용자의 건강 프로필 기반 맞춤형 식단을 AI로 추천합니다.

---

## ⚙️ 기술 스택

### Backend
- **Kotlin 1.9.25** + **Spring Boot 3.5.0** + **Java 21**
- **Spring Data JPA**, **Spring Security**, **Spring Kafka**, **Spring WebFlux**

### Database & Cache
- **PostgreSQL 15-alpine** (메인 DB)
- **Redis (Redisson 3.22.1)** (캐시, Refresh Token, 분산 락)

### Messaging & Infrastructure
- **Apache Kafka 7.5.0** + **Zookeeper**
- **Docker Compose**
- **Gradle (Kotlin DSL)**

### Authentication & Documentation
- **JWT (jjwt 0.11.5)**
- **Swagger (springdoc 2.0.2)**

### External API
- **OpenAI API** (GPT-4o-mini)

<img width="241" height="233" alt="기술 스택" src="https://github.com/user-attachments/assets/842b1cd5-2191-4e60-bfbf-967fc53c5e44" />

---

## 🏗️ 아키텍처

### 계층별 역할

| 계층 | 역할 | 예시 |
|------|------|------|
| **Presentation** | HTTP 요청/응답 처리 | ,  |
| **Application** | 비즈니스 유스케이스 구현 | ,  |
| **Domain** | 핵심 비즈니스 로직 | , ,  |
| **Infrastructure** | 외부 시스템 연동 | ,  |

---

## 📦 주요 기능

### ✅ 구현 완료

1. **회원 관리**
   - 회원가입 (이메일 중복 체크 + 분산 락)
   - 로그인 (Local/Kakao/Naver 멀티 전략)
   - JWT 토큰 발급 (Access + Refresh Token)

2. **인증/인가**
   - Spring Security 설정
   - JWT 기반 인증 필터
   - Role 기반 권한 관리

3. **식단 설문**
   - DietSurvey 작성 (나이, 성별, 신체정보, 건강상태, 식습관, 목표)

4. **이벤트 기반 아키텍처**
   - Kafka 이벤트 발행/구독 ( 토픽)
   - @TransactionalEventListener로 트랜잭션 커밋 후 이벤트 처리

5. **API 문서**
   - Swagger UI 통합

### 🚧 구현 예정
- AI 식단 추천 고도화
- 알림 시스템 (카카오톡, 이메일)
- 소셜 기능 (식단 공유, 커뮤니티)
- 결제 시스템

---

## 📁 프로젝트 구조

```
src/main/kotlin/dev/mealfit/mealfit/
├── user/               # 사용자 도메인
│   ├── domain/         # User, Role, UserSignedUpEvent
│   ├── application/    # SignUpService, LoginService, LoginStrategy
│   ├── infrastructure/ # UserRepository, KafkaProducer
│   └── presentation/   # UserController
│
├── diet/               # 식단 도메인
│   ├── domain/         # DietSurvey, DietRecommendation
│   ├── application/    # DietSurveyService
│   ├── infrastructure/ # DietSurveyRepository, Consumer
│   └── presentation/   # DietController
│
├── ai/                 # AI 통합
│   └── infrastructure/ # OpenAiClient
│
├── auth/               # 인증
│   └── infrastructure/ # RefreshTokenStore
│
└── common/             # 공통 설정
    ├── security/       # SecurityConfig, JwtTokenProvider
    ├── kafka/          # KafkaConfig
    ├── redis/          # RedisConfig
    ├── swagger/        # SwaggerConfig
    └── error/          # GlobalExceptionHandler
```

---

## 🚀 실행 방법

### 1. 환경 변수 설정

`application-local.yml`에서 다음을 설정하세요:

```yaml
openai:
  api:
    key: your-openai-api-key

jwt:
  secret: my-secret-key-at-least-32-bytes
  expiration: 86400000
```

### 2. 인프라 실행

```bash
# Docker Compose로 PostgreSQL, Redis, Kafka 실행
docker-compose up -d

# 컨테이너 상태 확인
docker-compose ps
```

### 3. 애플리케이션 실행

```bash
./gradlew bootRun
```

### 4. 접속

- **애플리케이션**: http://localhost:8081
- **Swagger UI**: http://localhost:8081/swagger-ui.html

### 5. 종료

```bash
docker-compose down
```

---

## 📊 모니터링 (Prometheus + Grafana)

### 서비스 주소

| 서비스 | 주소 | 계정 |
|--------|------|------|
| **Grafana** 대시보드 | http://localhost:3000 | admin / mealfit |
| **Prometheus** UI | http://localhost:9090 | - |
| **Actuator** 메트릭 | http://localhost:8081/actuator/prometheus | - |

### Docker Compose로 전체 스택 실행

```bash
# 전체 실행 (앱 + DB + Kafka + Prometheus + Grafana)
docker-compose up -d

# Grafana만 별도 확인
docker-compose logs -f grafana
```

컨테이너가 뜨면 **Grafana (http://localhost:3000)** 에서 바로 `MealFit 모니터링` 대시보드가 표시됩니다.

### 수집 메트릭

| 메트릭 | 설명 | PromQL 예시 |
|--------|------|------------|
| **RPS** | 초당 요청 수 | `sum(rate(http_server_requests_seconds_count{job="mealfit-app"}[1m]))` |
| **응답시간 P99** | 99번째 백분위 | `histogram_quantile(0.99, sum(rate(http_server_requests_seconds_bucket[5m])) by (le))` |
| **HTTP 상태별** | 2xx / 4xx / 5xx 비율 | `sum(rate(...{status=~"5.."}[1m]))` |
| **JVM Heap** | 힙 사용량 | `sum(jvm_memory_used_bytes{area="heap"})` |
| **GC 횟수** | GC 일시정지 비율 | `rate(jvm_gc_pause_seconds_count[1m])` |
| **HikariCP** | Active / Idle / Pending 커넥션 | `hikaricp_connections_active` |
| **Kafka 랙** | 컨슈머 랙 (spring-kafka 활성화 시) | `kafka_consumer_records_lag` |

### 로컬 개발 시 (Docker 없이)

애플리케이션만 로컬 실행 중이라면 Prometheus + Grafana를 별도로 실행할 수 있습니다:

```bash
# 모니터링 스택만 실행
docker-compose up -d prometheus grafana

# 단, prometheus.yml의 타겟이 host.docker.internal:8081을 가리켜야 합니다
# monitoring/prometheus/prometheus.yml 에서 targets 수정:
#   - host.docker.internal:8081
```

### 디렉토리 구조

```
monitoring/
├── prometheus/
│   └── prometheus.yml            # 스크레이프 설정 (10초 간격)
└── grafana/
    ├── provisioning/
    │   ├── datasources/
    │   │   └── prometheus.yml    # Prometheus 데이터소스 자동 등록
    │   └── dashboards/
    │       └── dashboard.yml     # 대시보드 파일 경로 설정
    └── dashboards/
        ├── mealfit.json          # MealFit 서버 모니터링 대시보드 (15개 패널)
        └── k6.json               # k6 부하 테스트 대시보드 (12개 패널)
```

---

## 🔥 k6 부하 테스트

### 스크립트 구조

```
loadtest/
├── scripts/
│   ├── main.js      # 메인 부하 테스트: 50 → 100 → 200 VU 단계별 증가
│   ├── smoke.js     # 스모크 테스트: 5 VU, 1분 (배포 후 빠른 검증)
│   └── stress.js    # 스트레스 테스트: 한계 부하 탐색 (5% 에러 시 자동 중단)
└── helpers/
    └── auth.js      # 로그인 / 회원가입 공통 헬퍼
```

### 성공 기준 (Thresholds)

| 지표 | 기준 |
|------|------|
| P95 응답시간 | **500ms 이하** |
| P99 응답시간 | 1000ms 이하 |
| 에러율 | **1% 이하** |

### 실행 방법

**① Docker Compose로 실행 (권장, Prometheus 연동 포함)**

```bash
# 전체 스택이 먼저 실행 중이어야 함
docker-compose up -d

# 메인 부하 테스트 (50→100→200 VU, 단계별 1분 유지)
docker-compose --profile loadtest run --rm k6 \
  run --out experimental-prometheus-rw /loadtest/scripts/main.js

# 스모크 테스트 (빠른 검증)
docker-compose --profile loadtest run --rm k6 \
  run --out experimental-prometheus-rw /loadtest/scripts/smoke.js

# 스트레스 테스트 (한계 탐색)
docker-compose --profile loadtest run --rm k6 \
  run --out experimental-prometheus-rw /loadtest/scripts/stress.js
```

**② k6 로컬 설치 후 실행**

```bash
# macOS
brew install k6

# Windows (Chocolatey)
choco install k6

# 로컬 앱 대상 메인 테스트
k6 run loadtest/scripts/main.js

# Prometheus 연동 포함 (로컬 앱 + Docker Prometheus)
K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
  k6 run --out experimental-prometheus-rw loadtest/scripts/main.js
```

**③ BASE_URL 환경 변수로 대상 서버 변경**

```bash
# 개발 서버 대상
BASE_URL=http://dev.mealfit.com k6 run loadtest/scripts/main.js

# 스테이징 서버 대상 (Docker)
docker-compose --profile loadtest run --rm \
  -e BASE_URL=http://staging.mealfit.com k6 \
  run --out experimental-prometheus-rw /loadtest/scripts/main.js
```

### Grafana k6 대시보드

테스트 실행 중 http://localhost:3000 에서 **"MealFit k6 부하 테스트"** 대시보드를 확인하세요.

| 패널 | 내용 |
|------|------|
| Active VUs / RPS / P95 / P99 / 에러율 | 실시간 요약 지표 |
| VU 수 / RPS 추이 | 시간별 부하 변화 |
| 응답시간 퍼센타일 (P50/P95/P99) | 지연 분포 |
| 엔드포인트별 P95 / RPS / 에러 | API 단위 성능 비교 |
| 데이터 송수신량 | 네트워크 처리량 |

### 테스트 계정 정리

```sql
-- 부하 테스트 후 DB 정리 (PostgreSQL)
DELETE FROM user_roles
 WHERE user_id IN (SELECT id FROM users WHERE email LIKE 'lt_%@mealfit.test');
DELETE FROM users WHERE email LIKE 'lt_%@mealfit.test';
```

---

## 🧪 테스트

### 테스트 구성 (총 67개)

| 분류 | 대상 | 테스트 수 |
|------|------|----------|
| **단위 테스트** | SignUpService, LoginService, LoginStrategyFactory, DietSurveyService, UserSignedUpEventHandler, JwtTokenProvider | 47개 |
| **통합 테스트** | AuthController, SignUp (H2 in-memory DB) | 13개 |
| **동시성 테스트** | 회원가입 100스레드 중복 방지, 식단 설문 동시 생성 | 6개 |
| **컨텍스트 로드** | MealFitApplicationTests | 1개 |

### 테스트 실행

```bash
# 전체 테스트
./gradlew test

# 단위 테스트만
./gradlew test --tests "dev.mealfit.mealfit.unit.*"

# 통합 테스트만
./gradlew test --tests "dev.mealfit.mealfit.integration.*"

# 동시성 테스트만
./gradlew test --tests "dev.mealfit.mealfit.concurrency.*"
```

### 테스트 파일 구조

```
src/test/kotlin/dev/mealfit/mealfit/
├── unit/
│   ├── service/
│   │   ├── SignUpServiceTest.kt          # 회원가입 로직, 이벤트 발행, BCrypt
│   │   ├── LoginServiceTest.kt           # 전략 패턴 위임, 미지원 타입 예외
│   │   └── DietSurveyServiceTest.kt      # 초기 설문 생성, 기본값 검증
│   ├── login/
│   │   └── LoginStrategyFactoryTest.kt   # local/kakao/naver 전략 반환
│   ├── event/
│   │   └── UserSignedUpEventHandlerTest.kt  # 이벤트 핸들러 호출 검증
│   └── security/
│       └── JwtTokenProviderTest.kt       # 토큰 생성/검증/만료/변조
├── integration/
│   ├── AuthControllerIntegrationTest.kt  # 로그인/회원가입 전체 흐름
│   └── SignUpIntegrationTest.kt          # 회원가입 API, DB 저장 확인
├── concurrency/
│   ├── SignUpConcurrencyTest.kt          # 동일 이메일 100스레드 → 1건만 저장
│   └── DietSurveyConcurrencyTest.kt      # 동시 설문 생성 중복 방지
└── MealFitApplicationTests.kt            # 컨텍스트 로드
```

### 주요 기술 선택

- **H2 in-memory DB** (MySQL 호환 모드): 통합·동시성 테스트용, 테스트마다 독립 DB URL 사용
- **mockito-kotlin 5.2.1**: Kotlin non-null 타입과의 호환성 (`doReturn().whenever()` 패턴)
- **ApplicationEventPublisher 람다**: `@FunctionalInterface` 특성 활용, 이벤트 직접 캡처
- **CountDownLatch**: 100개 스레드 동시 출발 보장

---

## 🎨 설계 패턴

- **Clean Architecture**: 계층 분리로 도메인 독립성 확보
- **DDD**: Aggregate, Domain Event로 도메인 중심 설계
- **Strategy Pattern**: 멀티 로그인 전략
- **Factory Pattern**: LoginStrategyFactory
- **Event-Driven Pattern**: 도메인 이벤트 + Kafka
- **Repository Pattern**: 영속성 계층 추상화
- **Ports & Adapters**: Application과 Infrastructure 분리

---

## ⚡ 성능 최적화

1. **동시성 제어**: Redisson 분산 락
2. **비동기 처리**: @Async + Kafka
3. **캐싱**: Redis (Refresh Token, 인증번호)
4. **연결 풀**: HikariCP, Kafka 파티셔닝
5. **Query 최적화**: LAZY Loading, Fetch Join

---

## 🗺️ 향후 계획

### Phase 1: 기능 완성
- AI 식단 추천 고도화
- 알림 시스템 (카카오톡, 이메일)
- 사용자 대시보드 (식단 캘린더, 영양 통계)

### Phase 2: 품질 향상
- 테스트 커버리지 80% 이상
- Query 최적화, Redis 캐싱 확대

### Phase 3: 운영 고도화
- AWS 배포 (ECS, RDS, ElastiCache, MSK)
- 무중단 배포 (Blue-Green, Rolling Update)
- 모니터링 (Prometheus, Grafana)

### Phase 4: 확장
- 모바일 앱 (Flutter)
- 소셜 기능 (식단 공유)
- 프리미엄 기능 (결제)

---

## 📝 라이센스

개인 포트폴리오 목적으로 제작되었습니다.

---

## 🙏 참고 자료

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Domain-Driven Design](https://www.domainlanguage.com/ddd/)
- [Apache Kafka](https://kafka.apache.org/documentation/)
- [OpenAI API](https://platform.openai.com/docs/api-reference)

---

<div align="center">

**MealFit** - AI로 바꾸는 식생활 🥗

Made with ❤️ by Kotlin & Spring Boot

</div>
