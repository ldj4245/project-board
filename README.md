# Hashtag Board 게시판 프로젝트

Spring Boot, JPA, QueryDSL, Thymeleaf, Spring Security 등을 활용하여 제작한 게시판 프로젝트입니다.

**데모 사이트:**  
[https://project-board-lee-1cfd46ae17d1.herokuapp.com/](https://project-board-lee-1cfd46ae17d1.herokuapp.com/)

## ✨ 주요 기능

- **게시글, 댓글**
  - 게시글 및 댓글 CRUD (생성, 조회, 수정, 삭제)
  - 게시글 검색 기능 (제목, 내용, ID, 닉네임, 해시태그)
- **해시태그**
  - 게시글 작성 시 해시태그를 생성 및 추가
  - 해시태그를 통한 게시글 검색
- **사용자 계정**
  - 회원가입 및 로그인 기능 (Spring Security OAuth 2.0 - Kakao, Google, Naver 연동)
  - 인증된 사용자만 게시글 및 댓글 작성, 수정, 삭제 가능
- **페이지네이션**
  - 게시글 목록 페이지네이션 기능

---

## ⚙️ 기술 스택

### Backend

- Java 17
- Spring Boot 2.7.0
- Spring Data JPA
- QueryDSL
- Spring Security (OAuth 2.0 client 연동)
- Spring Data REST (HAL Explorer)
- H2, MySQL, PostgreSQL
- Lombok

### Frontend

- Thymeleaf
- HTML, CSS
- Bootstrap 5

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

### Use-Case Diagram

![Use-Case](doucment/use-case.svg)

---

## 📁 프로젝트 구조

```
.
├── build.gradle/       # 프로젝트 의존성 및 빌드 설정
├── Procfile/           # Heroku 배포를 위한 프로세스 설정
├── doucment/           # ERD, 유스케이스 다이어그램 등 문서
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/genielee/projectboard/
│   │   │       ├── config/         # Spring Boot 설정 (Security, JPA)
│   │   │       ├── controller/     # API 엔드포인트를 처리하는 컨트롤러
│   │   │       ├── domain/         # JPA 엔티티
│   │   │       ├── dto/            # Data Transfer Objects
│   │   │       ├── exception/      # 커스텀 예외
│   │   │       ├── repository/     # Spring Data JPA 리포지토리
│   │   │       └── service/        # 비즈니스 로직
│   │   └── resources/
│   │       ├── static/         # CSS, JS, 이미지 파일
│   │       └── templates/      # Thymeleaf 템플릿 (HTML)
│   └── test/               # 테스트 코드
└── ...
```
