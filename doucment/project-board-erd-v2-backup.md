# Project Board ERD

## 개체 관계 다이어그램 (Entity Relationship Diagram)

```mermaid
erDiagram
    ARTICLE {
        bigint id PK
        varchar(50) user_account_id FK
        varchar(255) title
        varchar(65535) content
        bigint view_count "DEFAULT 0"
        datetime created_at
        varchar(100) created_by
        datetime modified_at
        varchar(100) modified_by
    }

    ARTICLE_COMMENT {
        bigint id PK
        bigint article_id FK
        varchar(50) user_account_id FK
        bigint parent_comment_id
        varchar(2000) content
        datetime created_at
        varchar(100) created_by
        datetime modified_at
        varchar(100) modified_by
    }

    USER_ACCOUNT {
        varchar(50) user_id PK
        varchar(255) user_password
        varchar(100) email
        varchar(100) nickname
        varchar(255) memo
        datetime created_at
        varchar(100) created_by
        datetime modified_at
        varchar(100) modified_by
    }

    HASHTAG {
        bigint id PK
        varchar(50) hashtag_name UK
        datetime created_at
        varchar(100) created_by
        datetime modified_at
        varchar(100) modified_by
    }

    ARTICLE_HASHTAG {
        bigint id PK
        bigint article_id FK
        bigint hashtag_id FK
    }

    ARTICLE_VIEW {
        bigint id PK
        bigint article_id FK
        varchar(45) viewer_ip
        varchar(50) user_id
        datetime created_at
        varchar(100) created_by
        datetime modified_at
        varchar(100) modified_by
    }

    ARTICLE_LIKE {
        bigint id PK
        bigint article_id FK
        varchar(50) user_id FK
        datetime created_at
        varchar(100) created_by
        datetime modified_at
        varchar(100) modified_by
    }

    ARTICLE_BOOKMARK {
        bigint id PK
        bigint article_id FK
        varchar(50) user_id FK
        datetime created_at
        varchar(100) created_by
        datetime modified_at
        varchar(100) modified_by
    }

    %% 관계 정의
    USER_ACCOUNT ||--o{ ARTICLE : "작성"
    USER_ACCOUNT ||--o{ ARTICLE_COMMENT : "작성"
    USER_ACCOUNT ||--o{ ARTICLE_LIKE : "좋아요"
    USER_ACCOUNT ||--o{ ARTICLE_BOOKMARK : "북마크"

    ARTICLE ||--o{ ARTICLE_COMMENT : "댓글보유"
    ARTICLE ||--o{ ARTICLE_HASHTAG : "해시태그연결"
    ARTICLE ||--o{ ARTICLE_VIEW : "조회기록"
    ARTICLE ||--o{ ARTICLE_LIKE : "좋아요받음"
    ARTICLE ||--o{ ARTICLE_BOOKMARK : "북마크됨"

    HASHTAG ||--o{ ARTICLE_HASHTAG : "사용됨"

    ARTICLE_COMMENT ||--o{ ARTICLE_COMMENT : "대댓글"
```

## 주요 변경사항 (v2.0)

### 새로 추가된 테이블

1. **ARTICLE_VIEW**: 게시글 조회 추적

   - IP와 사용자 ID 기반 24시간 중복 조회 방지
   - 조회수 계산을 위한 기록 보관

2. **ARTICLE_LIKE**: 게시글 좋아요 관계

   - 사용자와 게시글 간의 좋아요 관계 매핑
   - 복합 유니크 제약조건 (article_id, user_id)

3. **ARTICLE_BOOKMARK**: 게시글 북마크 관계
   - 사용자와 게시글 간의 북마크 관계 매핑
   - 복합 유니크 제약조건 (article_id, user_id)

### 기존 테이블 변경사항

- **ARTICLE**: `view_count` 필드 추가 (기본값 0)

## 인덱스 및 제약조건

### 성능 최적화를 위한 인덱스

- `ARTICLE_VIEW`: article_id, viewer_ip, user_id, created_at
- `ARTICLE_LIKE`: article_id, user_id, created_at
- `ARTICLE_BOOKMARK`: article_id, user_id, created_at

### 데이터 무결성을 위한 제약조건

- 각 상호작용 테이블의 복합 유니크 제약조건으로 중복 방지
- 외래키 제약조건으로 참조 무결성 보장
