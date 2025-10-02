# Hashtag Board 게시판 프로젝트

Spring Boot, JPA, QueryDSL, Thymeleaf, Spring Security 등을 활용하여 제작한 통합 커뮤니티 플랫폼입니다.

**데모 사이트:**  
[https://project-board-lee-1cfd46ae17d1.herokuapp.com/](https://project-board-lee-1cfd46ae17d1.herokuapp.com/)

## ✨ 주요 기능

### 📝 게시판 기능
- **게시글, 댓글**
  - 게시글 및 댓글 CRUD (생성, 조회, 수정, 삭제)
  - 게시글 검색 기능 (제목, 내용, ID, 닉네임, 해시태그)
  - **게시글 조회수 기능** (24시간 중복 조회 방지)
  - 대댓글 기능
- **해시태그**
  - 게시글 작성 시 해시태그를 생성 및 추가
  - 해시태그를 통한 게시글 검색
- **상호작용 기능**
  - **좋아요 시스템**: 게시글에 좋아요/좋아요 취소 기능
  - **북마크 시스템**: 게시글 북마크 저장 및 관리
  - **북마크 목록**: 사용자별 북마크한 게시글 목록 페이지

### 💰 암호화폐 게시판 (코인판)
- **실시간 시세 정보**
  - WebSocket 기반 실시간 암호화폐 시세 업데이트
  - Upbit, Binance, Bithumb 거래소 데이터 통합
- **김치 프리미엄 위젯**
  - 국내외 거래소 가격 차이 실시간 계산
  - 24시간 변동률 및 거래량 표시
  - 사용자 커스텀 코인 선택 기능 (최대 6개)
- **암호화폐 전용 게시판**
  - 코인별 게시판 및 자유 토론 게시판
  - 최신 댓글 및 인기 게시글 표시

### 👤 사용자 계정
- 회원가입 및 로그인 기능
- **OAuth 2.0 소셜 로그인**: Kakao, Naver 연동
- 인증된 사용자만 게시글 및 댓글 작성, 수정, 삭제 가능

### 📄 페이지네이션
- 게시글 목록 페이지네이션 기능

## 🆕 최신 업데이트

### v3.0 - 암호화폐 통합 기능 추가 (2025.10)

- **암호화폐 거래소 API 통합**: Upbit, Binance, Bithumb
- **김치 프리미엄 계산**: 실시간 국내외 가격 차이 및 프리미엄율
- **사용자 코인 추가 기능**: LocalStorage 기반 개인화 코인 목록
- **WebSocket 실시간 시세**: 실시간 가격 업데이트
- **암호화폐 전용 게시판**: 코인별 토론 게시판

### v2.0 - 상호작용 기능 추가 (2025.6)

- **조회수 시스템**: IP와 사용자 기반 24시간 중복 조회 방지
- **좋아요 기능**: Ajax 기반 실시간 좋아요/취소 토글
- **북마크 시스템**: 개인 북마크 저장 및 전용 목록 페이지

---

## ⚙️ 기술 스택

### Backend

- Java 17
- Spring Boot 3.3.5
- Spring Data JPA
- QueryDSL
- Spring Security (OAuth 2.0 client 연동)
- Spring Data REST (HAL Explorer)
- **Spring WebFlux** (Reactive 외부 API 호출)
- **WebSocket & STOMP** (실시간 통신)
- H2, MySQL, PostgreSQL
- Lombok

### Frontend

- Thymeleaf
- HTML, CSS, JavaScript (ES6+)
- Bootstrap 5
- Ajax (Fetch API)
- **WebSocket & SockJS** (실시간 데이터 수신)

### External APIs

- **Upbit API**: 국내 암호화폐 시세
- **Binance API**: 국제 암호화폐 시세
- **Bithumb API**: 국내 암호화폐 시세
- **환율 API**: USD/KRW 환율 정보

### DevOps & Tools

- Gradle
- Git, GitHub
- Heroku
- IntelliJ IDEA
- Swagger (SpringDoc)

---

## 📝 ERD & Use-Case

### ERD (Entity-Relationship Diagram)

> 📋 **ERD 다이어그램**: [상세 ERD 보기](doucment/project-board-erd.md)

GitHub에서 Mermaid 다이어그램을 자동으로 렌더링합니다.

**주요 엔티티:**

- `Article`: 게시글 (조회수 필드, 카테고리 포함)
- `ArticleComment`: 댓글 (대댓글 지원)
- `UserAccount`: 사용자 계정
- `Hashtag`: 해시태그
- `ArticleView`: 조회수 추적 (중복 방지)
- `ArticleLike`: 좋아요 관계
- `ArticleBookmark`: 북마크 관계
- `Coin`: 암호화폐 정보
- `Exchange`: 거래소 정보
- `CoinPrice`: 암호화폐 가격 이력

### Use-Case Diagram

> 📋 **Use-Case 다이어그램**: [상세 Use-Case 보기](doucment/use-case.md)

GitHub에서 Mermaid 다이어그램을 자동으로 렌더링합니다.

---

## 📁 프로젝트 구조

```
.
├── build.gradle           # 프로젝트 의존성 및 빌드 설정
├── Procfile               # Heroku 배포를 위한 프로세스 설정
├── doucment/              # ERD, 유스케이스 다이어그램 등 문서
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/genielee/projectboard/
│   │   │       ├── config/         # Spring Boot 설정
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   ├── WebSocketConfig.java      # WebSocket 설정
│   │   │       │   └── WebClientConfig.java      # WebFlux 설정
│   │   │       ├── controller/     # API 엔드포인트
│   │   │       │   ├── ArticleController.java
│   │   │       │   ├── ArticleLikeController.java
│   │   │       │   ├── ArticleBookmarkController.java
│   │   │       │   ├── CryptoBoardController.java     # 암호화폐 게시판
│   │   │       │   ├── PremiumController.java         # 김치 프리미엄 API
│   │   │       │   ├── MarketDataRestController.java  # 시세 REST API
│   │   │       │   └── CryptoWebSocketController.java # 실시간 시세
│   │   │       ├── domain/         # JPA 엔티티
│   │   │       │   ├── Article.java
│   │   │       │   ├── ArticleView.java
│   │   │       │   ├── ArticleLike.java
│   │   │       │   ├── ArticleBookmark.java
│   │   │       │   ├── Coin.java               # 암호화폐
│   │   │       │   ├── Exchange.java           # 거래소
│   │   │       │   ├── CoinPrice.java          # 가격 이력
│   │   │       │   └── ...
│   │   │       ├── dto/            # Data Transfer Objects
│   │   │       │   └── response/
│   │   │       │       ├── KimchiPremium.java
│   │   │       │       ├── MarketPrice.java
│   │   │       │       └── ...
│   │   │       ├── repository/     # Spring Data JPA 리포지토리
│   │   │       │   ├── ArticleViewRepository.java
│   │   │       │   ├── CoinRepository.java
│   │   │       │   ├── ExchangeRepository.java
│   │   │       │   └── ...
│   │   │       └── service/        # 비즈니스 로직
│   │   │           ├── ArticleViewService.java
│   │   │           ├── PremiumCalculationService.java
│   │   │           ├── MarketDataService.java
│   │   │           ├── ExchangeRateService.java
│   │   │           └── exchange/
│   │   │               ├── UpbitService.java
│   │   │               ├── BinanceService.java
│   │   │               └── BithumbService.java
│   │   └── resources/
│   │       ├── static/         # CSS, JS, 이미지 파일
│   │       └── templates/      # Thymeleaf 템플릿
│   │           ├── articles/
│   │           │   ├── detail.html
│   │           │   ├── index.html
│   │           │   └── bookmarks.html
│   │           └── crypto/     # 암호화폐 게시판 템플릿
│   │               ├── main.html
│   │               ├── board.html
│   │               └── coin-board.html
│   └── test/               # 테스트 코드
└── ...
```

## 🎯 주요 기능 상세

### 게시판 시스템

#### 조회수 시스템
- IP와 사용자 ID 기반 24시간 중복 조회 방지
- `ArticleView` 엔티티로 조회 이력 관리
- 게시글 목록과 상세 페이지에 조회수 표시

#### 좋아요/북마크 시스템
- Ajax 기반 실시간 토글 기능
- 복합키 제약조건으로 중복 방지
- 사용자별 상태 표시 및 개수 카운팅

### 암호화폐 시스템

#### 실시간 시세
- WebSocket을 통한 실시간 가격 업데이트
- Upbit, Binance, Bithumb 3대 거래소 통합
- 코인별 24시간 변동률 및 거래량 표시

#### 김치 프리미엄
- 국내외 거래소 가격 차이 실시간 계산
- 프리미엄율(%) 자동 계산 및 색상 표시
- 사용자 커스텀 코인 선택 (LocalStorage 저장)
- 거래량 원화 환산 (억 단위)

#### 암호화폐 게시판
- 코인별 전용 게시판
- 자유 토론 게시판
- 실시간 댓글 및 인기글 표시
- 조회수 기반 정렬
