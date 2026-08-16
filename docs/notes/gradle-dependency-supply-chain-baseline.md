# Gradle 의존성 공급망 기준선

## 목적

Oort의 Gradle 빌드가 내려받는 의존성 artifact의 무결성을 검증하고, 직접·전이 의존성의 해석 결과를 로컬과 CI에서 재현한다.

## 적용 범위

- `gradle/verification-metadata.xml`: Gradle plugin과 프로젝트 의존성 artifact의 SHA-256 checksum을 기록한다. 파일이 존재하면 Gradle은 매 빌드에서 다운로드한 artifact를 검증한다.
- `notification/gradle.lockfile`, `mock-server/gradle.lockfile`: 각 애플리케이션 모듈의 해석 가능한 configuration에 대한 dependency graph 버전을 기록한다.
- dependency locking은 Gradle plugin classpath를 잠그지 않는다. plugin artifact의 무결성은 verification metadata로 검증한다.

## 의존성 변경 절차

1. version catalog 또는 build script에서 의존성을 추가·갱신한다.
2. 다음 명령으로 metadata와 lockfile을 갱신한다.

   ```powershell
   .\gradlew.bat --write-verification-metadata sha256 --write-locks ktlintCheck test build
   .\gradlew.bat --write-verification-metadata sha256 help
   ```

   두 번째 명령은 일반 build task가 해석하지 않는 Gradle plugin classpath checksum을 포함한다. macOS/Linux에서는 `./gradlew`를 사용한다.

3. `verification-metadata.xml`의 새 checksum과 각 `gradle.lockfile`의 dependency diff를 PR에서 검토한다.
4. 생성 결과와 함께 기본 검증을 실행한다.

   ```powershell
   .\gradlew.bat ktlintCheck test build
   ```

## 검토 기준

- 요청한 직접 의존성 또는 그 전이 의존성 변경만 포함되는지 확인한다.
- 예상하지 못한 repository, group, artifact 또는 대량의 version 이동이 있으면 merge하지 않고 원인을 조사한다.
- dependency verification 오류는 우회하거나 끄지 않는다. artifact 출처와 변경 이유를 확인한 뒤 metadata를 갱신한다.
