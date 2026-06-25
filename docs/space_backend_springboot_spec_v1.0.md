# Space Backend — Spring Boot 백엔드 개발 명세서 v1.0

> 공간 대여 서비스를 위한 Spring Boot 3.x 기반 REST API 서버 개발 명세서입니다.
> Flutter 클라이언트와 연동하며 네이버·카카오 소셜 로그인, 네이버페이·카카오페이 결제를 핵심 기능으로 제공합니다.

---

## 1. 프로젝트 개요

### 1.1 시스템 소개

공간 대여 사업자를 위한 백엔드 REST API 서버입니다. 사용자는 소셜 로그인 후 월 단위 달력으로 예약 가능 일자를 확인하고, 시간 단위로 공간을 예약하며 네이버페이 또는 카카오페이로 결제합니다. 관리자는 공간·카테고리 CRUD, 예약 상태 관리, 환불 승인을 처리합니다.

### 1.2 기술 스택

| 항목 | 스택 |
|------|------|
| 언어 | Java 21 (LTS) |
| 프레임워크 | Spring Boot 3.3 |
| ORM / DB 접근 | Spring Data JPA + QueryDSL 5.x |
| DB 마이그레이션 | Flyway |
| 인증 | Spring Security + JJWT 0.12.x |
| HTTP 클라이언트 | RestClient (Spring 6.1 표준) |
| 빌드 | Gradle (Kotlin DSL) |
| 컨테이너 | Docker multi-stage (Jib 플러그인) |

### 1.3 의존성 목록 (build.gradle.kts)

```
Spring Boot                 3.3.x
Java                        21
Spring Web (MVC)
Spring Security
Spring Data JPA
PostgreSQL Driver
Flyway Core + PostgreSQL
JJWT (io.jsonwebtoken)      0.12.x
Spring Boot Validation
Lombok
MapStruct                   1.5.x
QueryDSL JPA                5.x
Spring Boot Actuator
Spring Boot Test
Mockito
Testcontainers (PostgreSQL)
WireMock
```

### 1.4 인프라 구성

- **DB**: PostgreSQL 15 (`uuid-ossp`, `btree_gist` 익스텐션)
- **캐시**: Redis (Refresh Token 저장소 — 선택적 도입)
- **배포**: Docker Compose (개발), 단일 JAR 컨테이너 (운영)
- **환경설정**: `application.yml` + `.env`

---

## 2. 클린 아키텍처 레이어 설계

`domain / application / infrastructure / presentation` 4-레이어 구조를 적용합니다.

```
com.space.backend
├── domain
│   ├── user
│   ├── space
│   ├── booking
│   └── payment
│
├── application
│   ├── auth
│   ├── user
│   ├── space
│   ├── booking
│   ├── payment
│   └── admin
│
├── infrastructure
│   ├── persistence
│   │   ├── user/
│   │   ├── space/
│   │   ├── booking/
│   │   └── payment/
│   ├── security
│   ├── external
│   │   ├── naver/
│   │   └── kakao/
│   ├── scheduler
│   └── config
│
└── presentation
    ├── auth
    ├── user
    ├── space
    ├── booking
    ├── payment
    └── admin
        ├── category
        ├── space
        └── booking
```

### 2.1 의존성 방향 규칙

```
presentation → application → domain
infrastructure → domain
presentation  ↛ infrastructure
```

모든 Repository 인터페이스는 `domain` 레이어에 정의하고, 구현체는 `infrastructure/persistence`에 위치합니다.

---

## 3. 도메인 모델 설계

### 3.1 User 도메인

**테이블**: `users`, `user_social_accounts`, `refresh_tokens`

```java
@Entity @Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = UUID)
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String profileImageUrl;
    @Enumerated(STRING) private UserRole role;
    private boolean isActive;
}

@Entity @Table(name = "user_social_accounts")
public class UserSocialAccount {
    @Id @GeneratedValue(strategy = UUID)
    private UUID id;
    @ManyToOne(fetch = LAZY) private User user;
    @Enumerated(STRING) private OAuthProvider provider;
    private String socialId;
}

@Entity @Table(name = "refresh_tokens")
public class RefreshToken {
    @Id @GeneratedValue(strategy = UUID)
    private UUID id;
    @ManyToOne(fetch = LAZY) private User user;
    @Column(unique = true) private String token;
    private Instant expiresAt;
}
```

```java
public enum UserRole { USER, ADMIN }
public enum OAuthProvider { NAVER, KAKAO }
```

```java
public interface UserRepository {
    Optional<User> findById(UUID id);
    Optional<User> findBySocialAccount(OAuthProvider provider, String encryptedSocialId);
    User save(User user);
}

public interface RefreshTokenRepository {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(UUID userId);
    RefreshToken save(RefreshToken token);
}
```

### 3.2 Space 도메인

**테이블**: `space_categories`, `spaces`, `space_images`, `space_operating_hours`, `space_closed_days`

```java
@Entity @Table(name = "spaces")
public class Space {
    @Id @GeneratedValue(strategy = UUID) private UUID id;
    @ManyToOne(fetch = LAZY) private SpaceCategory category;
    private String name;
    private String description;
    private String address;
    private int capacity;
    private int minHours;
    private int maxHours;
    private int pricePerHour;
    private String thumbnailUrl;
    private int displayOrder;
    private boolean isActive;
    @OneToMany(mappedBy = "space", cascade = ALL, orphanRemoval = true)
    private List<SpaceImage> images;
    @OneToMany(mappedBy = "space", cascade = ALL, orphanRemoval = true)
    private List<SpaceOperatingHours> operatingHours;
    @OneToMany(mappedBy = "space", cascade = ALL, orphanRemoval = true)
    private List<SpaceClosedDay> closedDays;
}

@Entity @Table(name = "space_operating_hours")
public class SpaceOperatingHours {
    @Id @GeneratedValue(strategy = UUID) private UUID id;
    @ManyToOne(fetch = LAZY) private Space space;
    private int dayOfWeek;
    private LocalTime openTime;
    private LocalTime closeTime;
    private boolean isClosed;
}

@Entity @Table(name = "space_closed_days")
public class SpaceClosedDay {
    @Id @GeneratedValue(strategy = UUID) private UUID id;
    @ManyToOne(fetch = LAZY) private Space space;
    private LocalDate closedDate;
    private String reason;
}
```

### 3.3 Booking 도메인

**테이블**: `bookings`

```java
@Entity @Table(name = "bookings")
public class Booking {
    @Id @GeneratedValue(strategy = UUID) private UUID id;
    @ManyToOne(fetch = LAZY) private User user;
    @ManyToOne(fetch = LAZY) private Space space;
    private Instant startAt;
    private Instant endAt;
    private int hours;
    private int headcount;
    private int totalPrice;
    @Enumerated(STRING) private PaymentType paymentType;
    @Enumerated(STRING) private BookingStatus status;
    private Instant pendingExpiresAt;
    private String memo;
    private String adminMemo;
    private Instant createdAt;
    private Instant updatedAt;
}

public enum BookingStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    CANCEL_REQUESTED,
    CANCELLED_BY_USER,
    CANCELLED_BY_ADMIN
}

public enum PaymentType { NAVER_PAY, KAKAO_PAY }
```

`CANCEL_REQUESTED` 상태는 사용자가 취소를 신청한 뒤 관리자 승인 대기 중인 상태를 명시적으로 표현합니다. `pendingExpiresAt`은 결제 미완료 예약의 자동 만료 시각을 저장합니다.

```java
public class BookingDomainService {
    public List<TimeSlot> calculateAvailableSlots(
        Space space, LocalDate date, List<Booking> existingBookings
    ) { ... }

    public Map<LocalDate, Boolean> calculateAvailableDates(
        Space space, int year, int month, List<Booking> existingBookings
    ) { ... }

    public void validateBookingRequest(
        Space space, Instant startAt, int hours, int headcount
    ) { ... }

    public CancelPreview calculateCancelPreview(Booking booking, Instant now) { ... }

    public int calculateTotalPrice(int pricePerHour, int hours) {
        return pricePerHour * hours;
    }
}

public record CancelPreview(
    int originalAmount,
    int refundAmount,
    int penaltyAmount,
    String penaltyReason,
    boolean isCancellable
) {}
```

```java
public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(UUID id);
    List<Booking> findByUserId(UUID userId);
    List<Booking> findBySpaceAndDateRange(UUID spaceId, Instant from, Instant to);
    List<Booking> findBySpaceAndMonth(UUID spaceId, int year, int month);
    List<Booking> findExpiredPending(Instant now);
    List<Booking> findAll(BookingSearchCondition condition);
}
```

### 3.4 Payment 도메인

**테이블**: `payments`

```java
@Entity @Table(name = "payments")
public class Payment {
    @Id @GeneratedValue(strategy = UUID) private UUID id;
    @OneToOne(fetch = LAZY) private Booking booking;
    @ManyToOne(fetch = LAZY) private User user;
    @Enumerated(STRING) private PaymentProvider provider;
    private int amountKrw;
    @Enumerated(STRING) private PaymentStatus status;
    @Column(unique = true) private String pgOrderId;
    private String pgTransactionId;
    private Integer refundAmountKrw;
    private String refundReason;
    private Instant paidAt;
    private Instant refundedAt;
}

public enum PaymentStatus { READY, APPROVED, REFUND_REQUESTED, REFUNDED, FAILED }
public enum PaymentProvider { NAVER_PAY, KAKAO_PAY }
```

```java
public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findByPgOrderId(String pgOrderId);
    Optional<Payment> findByBookingId(UUID bookingId);
    List<Payment> findRefundRequested();
}
```

---

## 4. Application 레이어 설계

### 4.1 AuthService

```java
public interface AuthService {
    TokenResponse processOAuthCallback(OAuthProvider provider, String code);
    TokenResponse refreshAccessToken(String refreshToken);
    void logout(UUID userId);
    void linkSocialAccount(UUID userId, OAuthProvider provider, String code);
}
```

### 4.2 SpaceService

```java
public interface SpaceService {
    List<CategoryDto> getCategories();
    List<SpaceSummaryDto> getSpaces(UUID categoryId);
    SpaceDetailDto getSpaceById(UUID id);
    AvailableDatesDto getAvailableDates(UUID spaceId, int year, int month);
    AvailableSlotsDto getAvailableSlots(UUID spaceId, LocalDate date);
}
```

### 4.3 BookingService

```java
public interface BookingService {
    BookingDto createBooking(UUID userId, CreateBookingCommand command);
    List<BookingDto> getMyBookings(UUID userId);
    BookingDto getBookingById(UUID userId, UUID bookingId);
    CancelPreview getCancelPreview(UUID userId, UUID bookingId);
    void requestCancel(UUID userId, UUID bookingId);
}
```

### 4.4 PaymentService

```java
public interface PaymentService {
    PaymentReadyResponse readyNaverPay(UUID userId, UUID bookingId);
    void approveNaverPay(String pgOrderId, String paymentId);
    PaymentReadyResponse readyKakaoPay(UUID userId, KakaoPayReadyCommand command);
    void approveKakaoPay(String pgOrderId, String pgToken);
    void handlePaymentFailure(String pgOrderId);
}
```

### 4.5 AdminBookingService

```java
public interface AdminBookingService {
    PageDto<AdminBookingDto> getBookings(BookingSearchCondition condition, Pageable pageable);
    AdminBookingDto getBookingById(UUID bookingId);
    void updateBookingStatus(UUID bookingId, UpdateBookingStatusCommand command);
    void approveRefund(UUID bookingId, ApproveRefundCommand command);
}
```

### 4.6 AdminSpaceService

```java
public interface AdminSpaceService {
    SpaceDetailDto createSpace(CreateSpaceCommand command);
    SpaceDetailDto updateSpace(UUID spaceId, UpdateSpaceCommand command);
    void deleteSpace(UUID spaceId);
    CategoryDto createCategory(CreateCategoryCommand command);
    CategoryDto updateCategory(UUID categoryId, UpdateCategoryCommand command);
    void deleteCategory(UUID categoryId);
    void updateOperatingHours(UUID spaceId, List<OperatingHoursCommand> commands);
    void addClosedDay(UUID spaceId, AddClosedDayCommand command);
    void removeClosedDay(UUID spaceId, LocalDate date);
}
```

### 4.7 BookingExpiryService (스케줄러)

```java
public interface BookingExpiryService {
    void expireStaleBookings();
}
```

PENDING 상태로 생성된 예약은 `pendingExpiresAt` 기준으로 결제가 완료되지 않으면 자동으로 `CANCELLED_BY_ADMIN` 상태로 전환됩니다. `@Scheduled(fixedDelay = 60000)`으로 매 1분 실행합니다.

---

## 5. Infrastructure 레이어 설계

### 5.1 보안

```
infrastructure/security/
├── JwtProvider.java
├── JwtAuthenticationFilter.java
├── SecurityConfig.java
└── CustomUserDetailsService.java
```

```java
http
  .csrf(AbstractHttpConfigurer::disable)
  .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
  .authorizeHttpRequests(auth -> auth
    .requestMatchers("/health", "/auth/**").permitAll()
    .requestMatchers(GET, "/api/categories/**", "/api/spaces/**").permitAll()
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    .anyRequest().authenticated()
  )
  .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
```

### 5.2 소셜 로그인 (수동 OAuth2 코드 교환)

Spring Security OAuth2 Client 대신 Flutter WebView 흐름과 호환되는 수동 방식을 유지합니다.

```
infrastructure/external/naver/
├── NaverOAuthClient.java
├── NaverUserInfoClient.java
└── NaverOAuthProperties.java

infrastructure/external/kakao/
├── KakaoOAuthClient.java
├── KakaoUserInfoClient.java
└── KakaoOAuthProperties.java
```

### 5.3 결제 연동

```
infrastructure/external/naver/
├── NaverPayClient.java
└── NaverPayProperties.java

infrastructure/external/kakao/
├── KakaoPayClient.java
└── KakaoPayProperties.java
```

### 5.4 스케줄러

```
infrastructure/scheduler/
└── BookingExpiryScheduler.java
```

### 5.5 Persistence

```
infrastructure/persistence/
├── user/
│   ├── UserJpaRepository.java
│   └── UserRepositoryImpl.java
├── space/
│   ├── SpaceJpaRepository.java
│   └── SpaceRepositoryImpl.java
├── booking/
│   ├── BookingJpaRepository.java
│   ├── BookingQueryRepository.java  (QueryDSL)
│   └── BookingRepositoryImpl.java
└── payment/
    ├── PaymentJpaRepository.java
    └── PaymentRepositoryImpl.java
```

---

## 6. Presentation 레이어 설계

### 6.1 Controller 목록

| Controller | Base Path | 인증 |
|---|---|---|
| `AuthController` | `/auth` | 공개 |
| `UserController` | `/api/users` | JWT |
| `SpaceController` | `/api/spaces`, `/api/categories` | 공개 |
| `BookingController` | `/api/bookings` | JWT |
| `PaymentController` | `/api/payments` | JWT |
| `AdminCategoryController` | `/api/admin/categories` | ADMIN |
| `AdminSpaceController` | `/api/admin/spaces` | ADMIN |
| `AdminBookingController` | `/api/admin/bookings` | ADMIN |
| `HealthController` | `/health` | 공개 |

### 6.2 전체 API 엔드포인트 명세

#### 인증 (공개)

```
GET  /auth/naver                        네이버 OAuth 인가 URL 리다이렉트
GET  /auth/naver/callback               네이버 OAuth 콜백 → JWT 발급
GET  /auth/kakao                        카카오 OAuth 인가 URL 리다이렉트
GET  /auth/kakao/callback               카카오 OAuth 콜백 → JWT 발급
POST /auth/refresh                      Access Token 재발급
POST /auth/logout                       로그아웃 (Refresh Token 삭제)
```

#### 사용자 (JWT 필요)

```
GET  /api/users/me                      내 프로필 조회
GET  /api/users/link/naver              네이버 계정 연동
GET  /api/users/link/kakao              카카오 계정 연동
```

#### 공간 / 카테고리 (공개)

```
GET  /api/categories                    카테고리 목록 조회
GET  /api/spaces?categoryId={id}        공간 목록 조회 (카테고리 필터)
GET  /api/spaces/{id}                   공간 상세 조회
GET  /api/spaces/{id}/available-dates   월 단위 예약 가능 일자 조회
     ?year={year}&month={month}         → 해당 월의 날짜별 가용 여부 배열 반환
GET  /api/spaces/{id}/available-slots   일 단위 예약 가능 시간 슬롯 조회
     ?date={YYYY-MM-DD}
```

#### 예약 (JWT 필요)

```
POST /api/bookings                      예약 생성 (status: PENDING)
GET  /api/bookings/my                   내 예약 목록 조회
GET  /api/bookings/{id}                 내 예약 상세 조회
GET  /api/bookings/{id}/cancel-preview  취소 시 환불 예상 금액 및 위약금 사전 확인
POST /api/bookings/{id}/cancel          취소 신청 (status: CANCEL_REQUESTED)
```

#### 결제 (JWT 필요)

```
POST /api/payments/naver/ready          네이버페이 결제 준비
GET  /api/payments/naver/approve        네이버페이 결제 승인 콜백 (PG사 리다이렉트)
GET  /api/payments/naver/fail           네이버페이 결제 실패 콜백
POST /api/payments/kakao/ready          카카오페이 결제 준비
GET  /api/payments/kakao/approve        카카오페이 결제 승인 콜백 (PG사 리다이렉트)
GET  /api/payments/kakao/cancel         카카오페이 결제 취소 콜백
GET  /api/payments/kakao/fail           카카오페이 결제 실패 콜백
```

#### 관리자 — 카테고리 (ADMIN)

```
GET    /api/admin/categories            카테고리 전체 목록
POST   /api/admin/categories            카테고리 등록
PUT    /api/admin/categories/{id}       카테고리 수정
DELETE /api/admin/categories/{id}       카테고리 삭제
```

#### 관리자 — 공간 (ADMIN)

```
GET    /api/admin/spaces                        공간 전체 목록 (비활성 포함)
POST   /api/admin/spaces                        공간 등록
PUT    /api/admin/spaces/{id}                   공간 수정
DELETE /api/admin/spaces/{id}                   공간 삭제
PUT    /api/admin/spaces/{id}/operating-hours   운영시간 일괄 수정
POST   /api/admin/spaces/{id}/closed-days       휴무일 등록
DELETE /api/admin/spaces/{id}/closed-days/{date} 휴무일 삭제
```

#### 관리자 — 예약 / 환불 (ADMIN)

```
GET    /api/admin/bookings              예약 목록 조회 (상태·날짜 필터, 페이지네이션)
       ?status={status}&date={YYYY-MM-DD}&page={n}&size={n}
GET    /api/admin/bookings/{id}         예약 상세 조회
PATCH  /api/admin/bookings/{id}/status  예약 상태 변경
       Body: { "status": "CONFIRMED" | "CANCELLED_BY_ADMIN", "adminMemo": "" }
POST   /api/admin/bookings/{id}/refund  환불 실행
       Body: { "refundAmountKrw": 50000, "reason": "" }
```

### 6.3 예약-결제 상태 흐름

```
[예약 생성] POST /api/bookings
    → status: PENDING, pendingExpiresAt = now + 15분

[결제 준비] POST /api/payments/{provider}/ready
    → Payment(status: READY) 생성, pgOrderId 반환

[결제 승인 콜백] GET /api/payments/{provider}/approve
    → Payment(status: APPROVED), Booking(status: CONFIRMED)
    → @Transactional 내 원자적 처리

[결제 실패/취소 콜백] GET /api/payments/{provider}/fail|cancel
    → Payment(status: FAILED), Booking(status: CANCELLED_BY_ADMIN)

[PENDING 자동 만료] @Scheduled(fixedDelay=60000)
    → pendingExpiresAt 초과 PENDING 예약 → CANCELLED_BY_ADMIN
```

### 6.4 취소-환불 상태 흐름

```
[취소 사전 확인] GET /api/bookings/{id}/cancel-preview
    → CancelPreview(환불금액, 위약금, 취소가능여부) 반환

[취소 신청] POST /api/bookings/{id}/cancel
    → Booking(status: CANCEL_REQUESTED)

[관리자 환불 승인] POST /api/admin/bookings/{id}/refund
    → Payment(status: REFUNDED), Booking(status: CANCELLED_BY_USER)
    → PG API 실제 환불 호출
```

### 6.5 예외 처리

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    // DomainException        → 400 Bad Request
    // UnauthorizedException  → 401 Unauthorized
    // ForbiddenException     → 403 Forbidden
    // NotFoundException      → 404 Not Found
    // ConflictException      → 409 Conflict  (중복 예약)
    // PaymentException       → 502 Bad Gateway
    // SchedulerException     → 내부 로깅만, 사용자 응답 없음
}
```

---

## 7. DB 마이그레이션 전략

PostgreSQL `btree_gist` 익스텐션과 `EXCLUDE USING gist` 기반 중복 예약 방지 제약을 DB 레벨에서 강제합니다.

```
src/main/resources/db/migration/
├── V1__create_users.sql
├── V2__create_spaces.sql
├── V3__create_bookings.sql           pending_expires_at 컬럼 포함
└── V4__create_payments.sql           refund_requested 상태 포함
```

`bookings` 테이블 핵심 제약:

```sql
ALTER TABLE bookings
  ADD COLUMN pending_expires_at TIMESTAMPTZ,
  ADD COLUMN status VARCHAR(30) NOT NULL DEFAULT 'PENDING';

ALTER TABLE bookings
  ADD CONSTRAINT bookings_no_overlap
  EXCLUDE USING gist (
    space_id WITH =,
    tstzrange(start_at, end_at) WITH &&
  )
  WHERE (status IN ('PENDING', 'CONFIRMED'));
```

---

## 8. 환경설정

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true

jwt:
  secret: ${JWT_SECRET}
  access-expiry-seconds: ${JWT_ACCESS_EXPIRY_SECONDS:900}
  refresh-expiry-days: ${JWT_REFRESH_EXPIRY_DAYS:30}

encryption:
  social-id-key: ${SOCIAL_ID_ENCRYPTION_KEY_BASE64}

oauth:
  naver:
    client-id: ${NAVER_CLIENT_ID}
    client-secret: ${NAVER_CLIENT_SECRET}
    redirect-uri: ${NAVER_REDIRECT_URI}
  kakao:
    client-id: ${KAKAO_CLIENT_ID}
    client-secret: ${KAKAO_CLIENT_SECRET}
    redirect-uri: ${KAKAO_REDIRECT_URI}

payment:
  naver:
    client-id: ${NAVER_PAY_CLIENT_ID}
    client-secret: ${NAVER_PAY_CLIENT_SECRET}
    chain-id: ${NAVER_PAY_CHAIN_ID}
    env: ${NAVER_PAY_ENV:sandbox}
  kakao:
    secret-key: ${KAKAO_PAY_SECRET_KEY}
    cid: ${KAKAO_PAY_CID:TC0ONETIME}
    approval-url: ${KAKAO_PAY_APPROVAL_URL}
    cancel-url: ${KAKAO_PAY_CANCEL_URL}
    fail-url: ${KAKAO_PAY_FAIL_URL}

booking:
  pending-expiry-minutes: ${BOOKING_PENDING_EXPIRY_MINUTES:15}
```

---

## 9. 구현 순서

### Sprint 1 — 기반 세팅
1. Gradle 및 Spring Boot 기본 프로젝트 생성
2. `application.yml` 및 `.env` 매핑
3. Flyway 마이그레이션 V1~V4 구성 (`pending_expires_at`, `CANCEL_REQUESTED` 상태 포함)
4. Entity / Enum 전체 작성
5. Docker Compose 세팅

### Sprint 2 — 인증
1. JWT 발급/검증 (`JwtProvider`)
2. `SecurityFilterChain` 구성
3. 네이버/카카오 OAuth 클라이언트 구현 (수동 코드 교환)
4. 콜백 기반 로그인 처리, 소셜 계정 연동
5. `refresh` / `logout` 구현

### Sprint 3 — 공간
1. 카테고리/공간 목록·상세 조회
2. `getAvailableDates` — 월 단위 가용 일자 계산 (`BookingDomainService`)
3. `getAvailableSlots` — 일 단위 가용 시간 슬롯 계산
4. 공간 API 통합 테스트

### Sprint 4 — 예약
1. 예약 생성 (`PENDING` + `pendingExpiresAt` 설정)
2. 내 예약 목록/상세 조회
3. `getCancelPreview` — 환불 예상 금액 계산
4. `requestCancel` — 취소 신청 (`CANCEL_REQUESTED` 전환)
5. `BookingExpiryScheduler` — PENDING 자동 만료

### Sprint 5 — 결제
1. 네이버페이 ready/approve/fail 구현
2. 카카오페이 ready/approve/cancel/fail 구현
3. 결제 승인 콜백: `Payment(APPROVED)` + `Booking(CONFIRMED)` 원자적 트랜잭션
4. 결제 실패 콜백: `Booking(CANCELLED_BY_ADMIN)` 롤백 처리

### Sprint 6 — 관리자 및 마감
1. 관리자 카테고리/공간 CRUD
2. 운영시간/휴무일 관리 API
3. 관리자 예약 목록/상태 변경 API
4. 관리자 환불 승인 API (PG API 실제 호출 포함)
5. 통합 테스트 전체 및 Docker 이미지 마감

---

## 10. 테스트 전략

```
src/test/java/com/space/backend/
├── unit/
│   ├── domain/booking/BookingDomainServiceTest.java
│   ├── domain/booking/CancelPreviewTest.java
│   └── application/
│       ├── BookingServiceTest.java
│       └── PaymentServiceTest.java
└── integration/
    ├── AuthControllerIntegrationTest.java
    ├── SpaceControllerIntegrationTest.java
    ├── BookingControllerIntegrationTest.java
    ├── PaymentControllerIntegrationTest.java
    └── AdminBookingControllerIntegrationTest.java
```

유닛 테스트는 순수 도메인 로직(`BookingDomainService`, `CancelPreview` 위약금 계산)을 검증합니다. 통합 테스트는 Testcontainers + PostgreSQL 실 DB로 수행하며, 네이버/카카오/PG 외부 API는 WireMock으로 모킹합니다. `BookingExpiryScheduler`는 `pendingExpiresAt`을 과거로 조작하는 시나리오로 별도 통합 테스트를 작성합니다.

---

## 11. ADR (Architecture Decision Records)

| ID | 결정 | 이유 |
|---|---|---|
| ADR-1 | JPA `ddl-auto=validate` | Flyway가 스키마 소유권 유지 |
| ADR-2 | 수동 OAuth2 코드 교환 | Flutter WebView 흐름과 호환 |
| ADR-3 | PostgreSQL EXCLUDE 제약 유지 | 예약 중복을 DB 레벨에서 강제 |
| ADR-4 | RestClient 사용 | Spring 6.1 표준 동기 HTTP 클라이언트 |
| ADR-5 | Repository 인터페이스는 domain에 배치 | 의존성 역전 원칙 유지 |
| ADR-6 | UUID 유지 | 기존 스키마/데이터 이관 호환성 |
| ADR-7 | AES-GCM 소셜 ID 암호화 유지 | 기존 암호화 정책 계승 |
| ADR-8 | `CANCEL_REQUESTED` 상태 추가 | 사용자 취소 신청 → 관리자 환불 승인 2단계 흐름 명시적 분리 |
| ADR-9 | `pendingExpiresAt` + `@Scheduled` 스케줄러 | 결제 미완료 예약 자동 만료로 슬롯 점유 방지 |
| ADR-10 | `cancel-preview` 엔드포인트 분리 | 취소 확정 전 환불 금액 사전 고지 UX 요구사항 대응 |
| ADR-11 | PG 결제 실패 콜백 엔드포인트 명시 | 실패/취소 시 Booking 상태 롤백 보장 |
