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
│   │   │       └── minji/
│   │   │           └── hi_erp/
│   │   │               ├── HiErpApplication.java
│   │   │               │
│   │   │               ├── controller/                       # 웹 & API 컨트롤러
│   │   │               │   ├── AccountController.java
│   │   │               │   ├── AdminController.java
│   │   │               │   ├── ApiKeyController.java
│   │   │               │   ├── AttendanceController.java
│   │   │               │   ├── EmailController.java
│   │   │               │   ├── EmailVerifyController.java
│   │   │               │   ├── EventController.java
│   │   │               │   ├── NoticeApiController.java
│   │   │               │   ├── NoticeController.java
│   │   │               │   ├── ValidController.java
│   │   │               │   └── ViewController.java
│   │   │               │
│   │   │               ├── service/                          # 비즈니스 로직
│   │   │               │   ├── AdminService.java
│   │   │               │   ├── CustomUserDetails.java
│   │   │               │   ├── CustomUserDetailsService.java
│   │   │               │   ├── EmailService.java
│   │   │               │   ├── EmailVerifyService.java
│   │   │               │   ├── EventService.java
│   │   │               │   ├── NoticeService.java
│   │   │               │   └── UserService.java
│   │   │               │
│   │   │               ├── entity/                           # JPA 도메인 엔티티
│   │   │               │   ├── Attendance.java               # 근태
│   │   │               │   ├── BaseTimeEntity.java           # 생성/수정일 공통 엔티티
│   │   │               │   ├── EmailToken.java               # 이메일 인증 토큰
│   │   │               │   ├── Event.java                    # 캘린더/일정
│   │   │               │   ├── Notice.java                   # 공지사항
│   │   │               │   └── Users.java                    # 사용자/사원
│   │   │               │
│   │   │               ├── repository/                       # JPA 리포지토리
│   │   │               │   ├── EmailTokenRepository.java
│   │   │               │   ├── EventRepository.java
│   │   │               │   ├── NoticeRepository.java
│   │   │               │   └── UserRepository.java
│   │   │               │
│   │   │               ├── dto/                              # 데이터 전송 객체
│   │   │               │   ├── AttendanceDto.java
│   │   │               │   ├── ChangePasswordRequestDto.java
│   │   │               │   ├── EventDto.java
│   │   │               │   ├── MailDto.java
│   │   │               │   ├── NoticeRequestDto.java
│   │   │               │   ├── NoticeResponseDto.java
│   │   │               │   ├── UserJoinDto.java
│   │   │               │   └── UserLoginDto.java
│   │   │               │
│   │   │               ├── security/config/                  # 보안 및 인프라 설정
│   │   │               │   ├── RedisConfig.java
│   │   │               │   └── SecurityConfig.java
│   │   │               │
│   │   │               ├── enums/                            # 공통 열거형
│   │   │               │   ├── Gender.java
│   │   │               │   └── Role.java
│   │   │               │
│   │   │               └── scheduler/                        # 주기적 스케줄링 작업
│   │   │                   └── NoticeViewScheduler.java
│   │   │
│   │   └── resources/
│   │       ├── static/                                       # 정적 자원 (css, js)
│   │       │   ├── css/
│   │       │   └── js/
│   │       ├── templates/                                    # Thymeleaf 뷰 템플릿
│   │       │   ├── account/
│   │       │   ├── admin/
│   │       │   ├── board/
│   │       │   ├── error/
│   │       │   ├── layout/
│   │       │   ├── mail/
│   │       │   ├── modal/
│   │       │   ├── calendar.html
│   │       │   └── main.html
│   │       ├── application.properties                        # 기본 환경 설정
│   │       └── application-API-KEY.properties                # API 키 및 보안 프로퍼티
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── minji/
│                   └── hi_erp/                               # 단위 및 통합 테스트
│                       ├── AccountControllerTest.java
│                       ├── EmailVerifyServiceTest.java
│                       ├── EventDeleteTest.java
│                       ├── HiErpApplicationTests.java
│                       ├── MailRenderTest.java
│                       ├── MailTest.java
│                       ├── RedisTest.java
│                       ├── ResetPasswordTest.java
│                       └── UserTest.java
│
├── .gitignore
├── build.gradle
├── settings.gradle
└── README.md
```

## 📂 프론트앤드 Directory Structure

```text
src/main/resources/
├── static/                                       # 정적 자원 (Static Assets)
│   ├── css/                                      # 글로벌 & 페이지별 스타일시트
│   └── js/                                       # Fetch API / AJAX 이벤트 스크립트
│
└── templates/                                    # Thymeleaf 뷰 템플릿 Engine
    ├── account/                                  # 계정 / 회원가입 / 인증 뷰
    │   ├── change-password.html                  # 비밀번호 변경
    │   ├── find-id-success.html                  # 아이디 찾기 완료
    │   ├── find-password.html                    # 비밀번호 찾기 신청
    │   ├── find-password-success.html            # 비밀번호 찾기 안내 완료
    │   ├── join.html                             # 회원가입 양식
    │   ├── join-success.html                     # 회원가입 완료 안내
    │   ├── login.html                            # 로그인
    │   ├── main.html                             # 계정 관리 서브 메인
    │   └── mypage.html                           # 마이페이지 (개인정보 수정)
    │
    ├── admin/                                    # 관리자 전용 뷰
    │   └── admin-setting.html                    # 관리자 대시보드 & 권한 설정
    │
    ├── board/                                    # 게시판 (공지사항) 뷰
    │   ├── detail.html                           # 게시글 상세 보기
    │   ├── notice.html                           # 공지사항 목록
    │   └── write.html                            # 게시글 작성 및 수정
    │
    ├── error/                                    # 예외 처리 뷰
    │   └── 404.html                              # 404 Not Found 페이지
    │
    ├── layout/                                   # 공통 프레임워크 레이아웃
    │   ├── user-layout.html                      # 기본 페이지 틀 (Layout Dialect)
    │   └── fragments/                            # 재사용 컴포넌트 조각
    │       └── sidebar.html                      # 좌측 사이드바 내비게이션
    │
    ├── mail/                                     # 발송용 이메일 HTML 템플릿
    │   ├── email-layout.html                     # 이메일 공통 레이아웃
    │   ├── email-verify.html                     # 회원가입 이메일 인증 템플릿
    │   ├── mail-test.html                        # 메일 발송 테스트 페이지
    │   └── reset-password-email.html             # 비밀번호 재설정 인증 메일 템플릿
    │
    ├── modal/                                    # 레이어 팝업 (모달) 템플릿
    │   ├── eventDetailModal.html                 # 캘린더 일정 상세 조회/삭제 모달
    │   └── eventModal.html                       # 캘린더 일정 등록/수정 모달
    │
    ├── calendar.html                             # 캘린더 / 일정 관리 메인 페이지
    └── main.html                                 # ERP 서비스 메인 대시보드
```

# 🚀 Getting Started
Prerequisites
- JDK 17 이상

- IDE (IntelliJ IDEA 또는 Eclipse)

- MySQL / MariaDB (또는 H2 Database)
