# Oort

Oort는 여러 백엔드 프로젝트를 묶어가며 시스템의 문제를 직접 다뤄보는 포트폴리오 프로젝트다.

현재 첫 프로젝트 영역은 대용량 알림 발송 서비스를 다루는 `notification`이다.

## Name

Oort는 오르트 구름(Oort Cloud)에서 가져온 이름이다.

익숙한 경계 바깥으로 나아가 아직 직접 다뤄보지 못한 문제들을 마주한다는 의미를 담았다.

## Purpose

이 프로젝트의 목적은 단순히 동작하는 서비스를 완성하는 것이 아니다.

병목, 비동기 처리, 멱등성, 장애 격리, 관측 같은 백엔드 주제를 직접 구현하고 실험하며 설명 가능한 경험으로 남기는 것을 목표로 한다.

프로젝트는 처음부터 완성된 아키텍처를 정하지 않는다. 실제 회사에서 일하듯 문제를 정의하고, 작은 단위로 설계하고, 구현하고, 관찰한 뒤 다음 결정을 이어간다.

## Local Development

로컬 개발은 Gradle Wrapper와 Docker Compose를 기준으로 진행한다.

필요한 도구는 JDK 21과 Docker Compose를 포함한 Docker Engine이다.

### 기본 검증

macOS/Linux:

```bash
./gradlew ktlintCheck test build
```

Windows:

```powershell
.\gradlew.bat ktlintCheck test build
```

### 로컬 의존성 실행

PostgreSQL과 mock-server는 Docker Compose로 실행한다. mock-server 이미지를 만들기 전에 JAR를 생성한다.

macOS/Linux:

```bash
./gradlew :mock-server:bootJar
docker compose up -d --build postgres mock-server
```

Windows:

```powershell
.\gradlew.bat :mock-server:bootJar
docker compose up -d --build postgres mock-server
```

종료:

```bash
docker compose down
```

### notification 실행

현재 `notification` 애플리케이션은 IDE 또는 Gradle로 로컬 JVM에서 실행한다. Docker Compose는 PostgreSQL과 `mock-server`만 실행한다.

macOS/Linux:

```bash
./gradlew :notification:bootRun
```

Windows:

```powershell
.\gradlew.bat :notification:bootRun
```
