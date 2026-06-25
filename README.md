# Space Backend

Spring Boot 3.3 기반 공간 대여 서비스 REST API 서버입니다.

## 기술 스택

- **Java 21** / **Spring Boot 3.3**
- Spring Data JPA + Flyway + PostgreSQL 15
- Spring Security + JJWT 0.12.x (Stateless JWT)
- 네이버/카카오 소셜 로그인 (수동 OAuth2 코드 교환)
- 네이버페이 / 카카오페이 결제
- Docker Compose 개발 환경

## 빠른 시작

```bash
# 1. 환경변수 설정
cp .env.example .env
# .env 파일에 필수 값 입력

# 2. DB + 앱 실행
docker-compose up --build

# 3. 로컈 실행 (DB만 Docker 사용)
docker-compose up postgres
./gradlew bootRun
```

## 패키지 구조

```
com.space.backend
├── domain          # Entity, Enum, Repository 인터페이스, DomainService
├── application     # UseCase Service (인터페이스 + 구현체)
├── infrastructure  # JPA, 외부 API 클라이언트, Security, 스케줄러, Config
└── presentation    # Controller, Request/Response DTO
```

## 주요 API

| 범주 | 경로 | 인증 |
|---|---|---|
| 인증 | `/auth/**` | 공개 |
| 공간 조회 | `GET /api/spaces/**`, `GET /api/categories` | 공개 |
| 예약 | `/api/bookings/**` | JWT |
| 결제 | `/api/payments/**` | JWT |
| 관리자 | `/api/admin/**` | JWT + ADMIN |

전체 API 명세는 `docs/space_backend_springboot_spec_v1.0.md` 에서 확인하세요.

## 테스트

```bash
./gradlew test
```

Testcontainers (PostgreSQL) 기반 통합 테스트를 실행하려면 Docker 데이 필요합니다.
