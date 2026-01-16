# 🥗 MealFit

MealFit는 AI를 활용한 **개인 맞춤형 식단 추천 서비스**입니다.  
클린 아키텍처와 이벤트 기반 아키텍처를 적용해 확장성과 유지 보수성을 고려한 구조로 설계되었습니다.

> 🍽️ "식생활, 이제 기술로 바꿉니다."

---

## 🔗 배포 주소

- Render 배포 버전: [https://mealfit.onrender.com/login](https://mealfit.onrender.com/login)
- AWS 무중단 롤링 배포 전략 예정

---

## 🧩 프로젝트 개요

| 항목        | 설명                                         |
|-------------|----------------------------------------------|
| 설계 방식   | DDD 도메인 중심 설계 + 클린 아키텍처         |
| 아키텍처    | Kafka 기반 이벤트 흐름 중심 구조              |
| 인증 방식   | JWT 기반 인증 흐름                            |
| 비동기 처리 | Kafka Consumer + Topic 분산 처리              |
| AI 연동     | OpenAI API 기반 맞춤형 식단 추천 로직 구현    |

---

## ⚙️ 기술 스택

- Kotlin + Spring Boot 3.5
- PostgreSQL
- Redis
- Apache Kafka + Zookeeper
- Docker Compose
- Swagger (springdoc-openapi)
- OpenAI API (Chat Completions, gpt-4o-mini)
<img width="241" height="233" alt="image" src="https://github.com/user-attachments/assets/842b1cd5-2191-4e60-bfbf-967fc53c5e44" />
<img width="1484" height="329" alt="image" src="https://github.com/user-attachments/assets/4ffc445a-c708-416a-90e8-00dc5feb9e9e" />

---

## 📦 주요 기능

- ✅ 회원가입 / 로그인 (JWT 기반 인증)
- ✅ Redis를 활용한 인증번호 발송 / 검증
- ✅ Kafka 이벤트 발행 / 수신 (`user.signed-up` 토픽 등)
- ✅ Swagger UI를 통한 API 문서 제공
- ✅ OpenAI API 연동을 통한 개인 맞춤형 식단 추천
- ✅ 미니 쿠버네티스 실습 구축
- 🚧 추천 결과를 UI에 반영하는 기능 고도화 예정
---

## 🏗️ 아키텍처 구조

```plaintext
MealFit
├── presentation        # REST API Controller
├── application         # UseCase, Command, Response DTO 등
├── domain              # 핵심 도메인 모델
├── infrastructure      # Redis, DB, Kafka 등 외부 인터페이스 구현
└── config              # Spring Security 및 글로벌 설정
```

---

## 🚀 실행 방법
1. Kafka / Zookeeper / PostgreSQL 컨테이너 실행
```bash
docker-compose up -d
```
- Docker Compose를 통해 컨테이너가 자동 구성됩니다.
  
2. Spring Boot 앱 로컬 실행
```bash
./gradlew bootRun
```
- 실행 후 localhost:8081에서 서비스 접근 가능 Swagger UI: http://localhost:8081/swagger-ui.html
⚠️ OpenAI API Key는 환경 변수로 관리해야 합니다.
export OPENAI_API_KEY=sk-xxxxxx
---

## 🧪 테스트 전략
- ✅ 단위 테스트 구성 중
- ✅ 통합 테스트에 TestContainers 도입 예정
- ✅ 인증 흐름, Kafka 이벤트 흐름 로깅 기반 디버깅

---

## 🗺️ 향후 개선 및 계획
- AWS 기반 무중단 롤링 배포 전략 구축
- Redis 기반 Refresh 토큰 저장 및 인증 흐름 개선
- Kafka 메시지 흐름 모니터링 대시보드 연결
- AI 모델 연동을 통한 식단 추천 고도화 (영양사 역할 Prompt 개선)

