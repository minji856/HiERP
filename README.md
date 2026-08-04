# 🏢 HiERP - Enterprise Resource Planning System

> **HiERP**는 기업 내 자원과 업무 프로세스(계정 관리, 사내 메일, 일정 관리, 게시판, 시스템 관리)를 통합하여 효율적인 협업 환경을 제공하는 **Spring Boot 기반 웹 ERP 시스템**입니다.

---

## 🛠 Tech Stack

### Backend
- **Java**: 17
- **Framework**: Spring Boot 3.x
- **Security**: Spring Security
- **Persistence**: Spring Data JPA
- **Build Tool**: Gradle

### Frontend
- **Template Engine**: Thymeleaf (Thymeleaf Layout Dialect)
- **Styling & Script**: HTML5, CSS3, JavaScript

---

## ✨ Key Features (주요 기능)

### 1. 📊 메인 대시보드 (`main.html`)
- 로그인 후 접속하는 사내 메인 화면
- 주요 업무 현황, 공지사항 요약, 주요 일정 실시간 확인

### 2. 📅 일정 관리 (`calendar.html`)
- 개인 및 부서 단위 업무 일정 등록/수정/삭제
- 달력 기반의 스케줄링 시각화

### 3. 👤 계정 관리 (`templates/account/`)
- 사용자 프로필 관리 및 비밀번호 변경
- 사용자 권한 확인 및 조직 정보 조회

### 4. ✉️ 사내 메일 시스템 (`templates/mail/`)
- 임직원 간 내부 메일 작성, 발송 및 수신함 관리
- 첨부파일 및 메일 읽음 처리 기능

### 5. 📌 사내 게시판 (`templates/board/`)
- 전사 공지사항 및 부서별 자유게시판
- 게시글 작성, 수정, 삭제 및 댓글 소통 기능

### 6. ⚙️ 관리자 전용 메뉴 (`templates/admin/`)
- 시스템 관리자 전용 권한 설정 및 계정 승인/관리
- 공통 코드 및 시스템 설정 제어

### 7. 🧩 공통 컴포넌트 & 레이아웃 (`templates/layout/`, `modal/`, `error/`)
- Thymeleaf 레이아웃 분리로 통일된 UI 제공
- 공통 모달(Modal) 팝업 처리 및 커스텀 에러 페이지 제공

# 전체구조
```
HiERP/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           ├── domain/                      # 비즈니스 로직 (컨트롤러, 서비스, 리포지토리)
│   │   │           │   ├── account/                 # 계정 및 사원 관리
│   │   │           │   ├── admin/                   # 관리자 전용 기능
│   │   │           │   ├── board/                   # 사내 게시판
│   │   │           │   ├── calendar/                # 일정 관리
│   │   │           │   └── mail/                    # 사내 메일
│   │   │           └── global/                      # 공통 설정 (Security, Exception 등)
│   │   │
│   │   └── resources/
│   │       ├── static/                              # 정적 자원
│   │       │   ├── css/                             # 스타일시트
│   │       │   └── js/                              # 자바스크립트 파일
│   │       ├── templates/                           # Thymeleaf 뷰 템플릿
│   │       │   ├── account/                         # 계정/사원 관련 뷰
│   │       │   ├── admin/                           # 시스템 관리자 뷰
│   │       │   ├── board/                           # 게시판 뷰
│   │       │   ├── error/                           # 예외 처리 페이지
│   │       │   ├── layout/                          # 공통 레이아웃 (Header, Sidebar, Footer)
│   │       │   ├── mail/                            # 메일 송수신 뷰
│   │       │   ├── modal/                           # 공통 모달 팝업 템플릿
│   │       │   ├── calendar.html                    # 캘린더/일정 관리 뷰
│   │       │   └── main.html                        # 메인 대시보드 뷰
│   │       ├── application.properties               # 메인 환경 설정
│   │       └── application-API-KEY.properties       # API 키 및 보안 관련 설정
│   │
│   └── test/                                        # 단위 및 통합 테스트
│       └── java/
│           └── com/
│               └── example/
│
├── .gitignore
├── build.gradle
├── settings.gradle
└── README.md
```

## 📂 Directory Structure

```text
src/main/
├── java/com/example/             # 백엔드 자바 로직 (Domain & Global)
└── resources/
    ├── static/                 # css, js 등 정적 파일
    ├── templates/              # Thymeleaf HTML 페이지
    │   ├── account/            # 계정 관리 뷰
    │   ├── admin/              # 관리자 뷰
    │   ├── board/              # 게시판 뷰
    │   ├── error/              # 에러 페이지
    │   ├── layout/             # 공통 레이아웃
    │   ├── mail/               # 메일 시스템 뷰
    │   ├── modal/              # 모달 팝업
    │   ├── calendar.html       # 일정 페이지
    │   └── main.html           # 메인 대시보드
    ├── application.properties          # 메인 설정
    └── application-API-KEY.properties  # API 키 설정
```

# 🚀 Getting Started
Prerequisites
- JDK 17 이상

- IDE (IntelliJ IDEA 또는 Eclipse)

- MySQL / MariaDB (또는 H2 Database)
