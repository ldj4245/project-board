# Hashtag Board 게시판 프로젝트

Spring Boot, JPA, QueryDSL, Thymeleaf, Spring Security 등을 활용하여 제작한 게시판 프로젝트입니다.

**데모 사이트:**  
[https://project-board-lee-1cfd46ae17d1.herokuapp.com/](https://project-board-lee-1cfd46ae17d1.herokuapp.com/)

## ✨ 주요 기능

- **게시글, 댓글**
  - 게시글 및 댓글 CRUD (생성, 조회, 수정, 삭제)
  - 게시글 검색 기능 (제목, 내용, ID, 닉네임, 해시태그)
  - **게시글 조회수 기능** (24시간 중복 조회 방지)
- **해시태그**
  - 게시글 작성 시 해시태그를 생성 및 추가
  - 해시태그를 통한 게시글 검색
- **사용자 계정**
  - 회원가입 및 로그인 기능 (Spring Security OAuth 2.0 - Kakao, Google, Naver 연동)
  - 인증된 사용자만 게시글 및 댓글 작성, 수정, 삭제 가능
- **상호작용 기능**
  - **좋아요 시스템**: 게시글에 좋아요/좋아요 취소 기능
  - **북마크 시스템**: 게시글 북마크 저장 및 관리
  - **북마크 목록**: 사용자별 북마크한 게시글 목록 페이지
- **페이지네이션**
  - 게시글 목록 페이지네이션 기능

## 🆕 최신 업데이트

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
- H2, MySQL, PostgreSQL
- Lombok

### Frontend

- Thymeleaf
- HTML, CSS, JavaScript (ES6+)
- Bootstrap 5
- Ajax (Fetch API)

### DevOps & Tools

- Gradle
- Git, GitHub
- Heroku
- IntelliJ IDEA
- Swagger (SpringDoc)

---

## 📝 ERD & Use-Case

### ERD (Entity-Relationship Diagram)

![ERD](doucment/project-board-erd.svg)

> 📋 **최신 ERD**: [상세 ERD 보기](doucment/project-board-erd.md) (Mermaid 다이어그램 포함)

**주요 엔티티:**

- `Article`: 게시글 (조회수 필드 포함)
- `ArticleComment`: 댓글
- `UserAccount`: 사용자 계정
- `Hashtag`: 해시태그
- `ArticleView`: 조회수 추적 (중복 방지)
- `ArticleLike`: 좋아요 관계
- `ArticleBookmark`: 북마크 관계

### Use-Case Diagram

![Use-Case](doucment/use-case.svg)

> 📋 **최신 Use-Case**: [상세 Use-Case 보기](doucment/use-case.md) (Mermaid 다이어그램 포함)

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
│   │   │       ├── config/         # Spring Boot 설정 (Security, JPA)
│   │   │       ├── controller/     # API 엔드포인트를 처리하는 컨트롤러
│   │   │       │   ├── ArticleController.java
│   │   │       │   ├── ArticleLikeController.java     # 좋아요 API
│   │   │       │   └── ArticleBookmarkController.java # 북마크 API
│   │   │       ├── domain/         # JPA 엔티티
│   │   │       │   ├── Article.java            # 게시글 (viewCount 포함)
│   │   │       │   ├── ArticleView.java        # 조회수 추적
│   │   │       │   ├── ArticleLike.java        # 좋아요 관계
│   │   │       │   ├── ArticleBookmark.java    # 북마크 관계
│   │   │       │   └── ...
│   │   │       ├── dto/            # Data Transfer Objects
│   │   │       ├── repository/     # Spring Data JPA 리포지토리
│   │   │       │   ├── ArticleViewRepository.java
│   │   │       │   ├── ArticleLikeRepository.java
│   │   │       │   └── ArticleBookmarkRepository.java
│   │   │       └── service/        # 비즈니스 로직
│   │   │           ├── ArticleViewService.java      # 조회수 관리
│   │   │           ├── ArticleLikeService.java      # 좋아요 관리
│   │   │           └── ArticleBookmarkService.java  # 북마크 관리
│   │   └── resources/
│   │       ├── static/         # CSS, JS, 이미지 파일
│   │       └── templates/      # Thymeleaf 템플릿 (HTML)
│   │           └── articles/
│   │               ├── detail.html    # 게시글 상세 (좋아요/북마크 버튼)
│   │               ├── index.html     # 게시글 목록 (조회수 표시)
│   │               └── bookmarks.html # 북마크 목록 페이지
│   └── test/               # 테스트 코드
└── ...
```

## 🎯 주요 기능 상세

### 조회수 시스템

- IP와 사용자 ID 기반 24시간 중복 조회 방지
- `ArticleView` 엔티티로 조회 이력 관리
- 게시글 목록과 상세 페이지에 조회수 표시

### 좋아요/북마크 시스템

- Ajax 기반 실시간 토글 기능
- 복합키 제약조건으로 중복 방지
- 사용자별 상태 표시 및 개수 카운팅
