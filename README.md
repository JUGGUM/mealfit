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
| 비동기 처리 | Kafka Consumer + Topic 분산 처리             |

---

## ⚙️ 기술 스택

- Kotlin + Spring Boot 3.5
- PostgreSQL
- Redis
- Apache Kafka + Zookeeper
- Docker Compose
- Swagger (springdoc-openapi)

---

## 📦 주요 기능

- ✅ 회원가입 / 로그인 (JWT 기반 인증)
- ✅ Redis를 활용한 인증번호 발송 / 검증
- ✅ Kafka 이벤트 발행 / 수신 (`user.signed-up` 토픽 등)
- ✅ Swagger UI를 통한 API 문서 제공
- 🚧 AI 모델과 연동한 식단 추천 로직 (구현 예정)

---

## 🏗️ 아키텍처 구조

```plaintext
MealFit
├── presentation        # REST API Controller
├── application         # UseCase, Command, Response DTO 등
├── domain              # 핵심 도메인 모델
├── infrastructure      # Redis, DB, Kafka 등 외부 인터페이스 구현
└── config              # Spring Security 및 글로벌 설정
