# webserver-backend
웹서버 5조 백엔드

## 개발환경
render + postqreSql + spring boot를 사용한 웹서버 프로젝트(mili road)입니다.

## 디렉토리 구조
```plaintext
webserver_backend/
├── src
│   └── main
│       ├── java
│       │   └── com
│       │       └── websever
│       │           └── websever
│       │               ├── controller      # 사용자의 요청 처리
│       │               │   └── basicController
│       │               ├── entity          # 데이터베이스 관리
│       │               │   └── basicEntity
│       │               ├── repository      # 데이터베이스랑 상호작용
│       │               │   └── basicRepository
│       │               ├── service         # 비즈니스 로직 처리
│       │               │   └── basicService
│       │               ├── WebSeverApplication
│       │               └── com.websever.websever.config         # render 전용 ping 던지기
│       │                   └── KeepAlive
│       └── resources
│           └── application.properties
├── .env                                  # 환경 변수
├── .gitattributes
├── .gitignore
├── build.gradle
├── Dockerfile                            # 도커 파일
├── gradlew
├── gradlew.bat
├── README.md
└── settings.gradle
```

## 실행

### 기본 실행
git clone 이후 > 터미널 내에서 ./gradlew build 진행 후 실행 <br>
baseUrl은 https://webserver-backend.onrender.com이며, api에 대한 기본틀은 /api/v1/임 <br>
ex) /api/v1/com.websever.websever.controller.auth/{userId} <br>

## 참고 사항
./gradlew build 진행 후 생기는 .grable 같은 패키지는 지극히 정상이며, 오히려 패키지 생성이 안 되어 있을 시 에러가 나니 참고 <br>
Dockerfile이나 application.properties 같은 파일은 기본 세팅 이외의 수정할 게 크게 없으니 수정하지 않을 것 <br>

## 파일명 지정 방법
파일명은 기능 및 페이지가 잘 드러날 수 있도록 하며, 너무 길지 않게 작성 <br>
길어질 경우 문자가 끝나는 사이에 대문자 삽입 ex)usercontroller > userController
