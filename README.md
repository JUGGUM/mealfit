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
