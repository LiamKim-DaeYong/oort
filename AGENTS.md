# AGENTS.md

## Collaboration

Oort는 사용자와 Codex가 함께 진행하는 프로젝트다.

Codex는 단순히 완성된 코드를 대신 작성하는 도구가 아니라, 함께 문제를 정의하고 설계 선택지를 검토하며 구현과 검증을 진행하는 개발 파트너로 참여한다.

중요한 방향, 설계 선택, 트레이드오프는 한 번에 확정하지 않고 차근차근 논의하며 결정한다.

## Decisions To Discuss

다음 결정은 구현 전에 함께 논의한다.

- 프로젝트의 목표와 범위
- 주요 기술 스택 선택
- 서비스 경계와 패키지 구조
- 동기/비동기 처리 방식 같은 핵심 아키텍처 선택
- 성능, 장애, 운영 관점의 실험 목표
- 이력서나 블로그에 남길 핵심 주장

반복적인 보일러플레이트, 단순 설정, 문서 정리, 작은 리팩터링은 Codex가 먼저 제안하거나 진행할 수 있다.

## Working Method

Oort는 다음 방식으로 진행한다.

- Walking Skeleton: 전체 흐름을 아주 얇게 먼저 연결한다.
- Vertical Slice: 작업은 계층별이 아니라 기능 단위로 작게 나눈다.
- Build-Measure-Learn: 만들고, 관찰하고, 배운 뒤 다음 결정을 이어간다.

중요한 설계 결정은 `docs/decisions/`에 남긴다.
작업 기록, 실험 결과, 회고는 `docs/notes/`에 남긴다.
학습 내용은 `docs/learning/`에 남긴다.
블로그 재료는 `docs/blog/`에 남긴다.

## Documentation Rule

모든 내용을 ADR로 남기지 않는다.

`docs/decisions/`에는 되돌리기 비용이 크거나 아키텍처 흐름을 바꾸는 결정을 남긴다.
`docs/notes/`에는 킥오프, 작업 기록, 실험 결과, 회고, 임시 합의를 남긴다.
`docs/learning/`에는 프로젝트를 진행하며 이해한 백엔드 개념과 면접 대비 내용을 주제별로 남긴다.
`docs/blog/`에는 블로그 완성 글이 아니라, 사용자가 자기 말로 글을 쓰기 위한 재료를 남긴다.

Codex는 블로그 완성본을 대신 작성하지 않는다. 문제, 원인, 실험, 결과, 트레이드오프, 이력서 문장 후보 같은 블로그 재료를 bullet note로 정리한다.

## Scope Rule

현재 Walking Skeleton에 필요하지 않은 기능과 인프라는 먼저 추가하지 않는다.

Kafka, Redis, 멱등성, 재시도, 다중 벤더, 장애 격리는 필요성이 관찰되거나 해당 주제를 다룰 시점이 되었을 때 도입한다.

## Test Rule

테스트는 빠른 피드백과 실제 동작 검증을 구분해서 작성한다.

기본 테스트 프레임워크는 Kotest로 둔다. Assertion은 Kotest assertions를 사용하고, mocking은 MockK를 사용한다.

domain/application 동작 테스트는 DescribeSpec을 우선 사용한다. 다만 Spring Boot 통합 테스트나 라이브러리 호환성이 중요한 테스트는 JUnit 사용을 허용한다.

첫 구현에서는 domain/application 테스트를 우선 작성하고, API와 DB 통합 테스트는 Walking Skeleton이 연결된 뒤 추가한다.

DB 통합 테스트가 필요할 때는 H2로 PostgreSQL을 흉내 내기보다 PostgreSQL Testcontainers를 사용한다.

모든 계층을 억지로 테스트하지 않는다. 핵심 흐름, 상태 전이, 장애/실패 처리처럼 설명 가치가 있는 동작을 우선 검증한다.

## Code Style Rule

Kotlin 코드 스타일은 ktlint로 관리한다.

detekt는 초기에는 도입하지 않는다. 정적 분석이 필요해질 때 별도로 검토한다.

## Dependency Rule

의존성 버전은 각 `build.gradle.kts`에 직접 흩뿌리지 않고 Gradle Version Catalog(`gradle/libs.versions.toml`)에서 관리한다.

새 의존성을 추가할 때는 기존 catalog 구조를 우선 확인하고, 필요한 경우 catalog에 version/library/plugin alias를 추가한다.

Spring Boot, Kotlin, 테스트/품질 도구는 도입 시점의 최신 안정 버전을 우선 검토한다.

## Git Rule

초기 브랜치 전략은 단순하게 가져간다.

- `main`: 항상 동작 가능한 기준 브랜치
- `feature/*`: 기능 또는 작업 단위 브랜치

커밋 메시지는 Conventional Commits 형식을 가볍게 따른다.

```text
type(scope): 한글 설명
```

`scope`는 선택이지만 가능하면 적는다.

예:

```text
docs: 프로젝트 킥오프 문서 정리
chore(build): Gradle 멀티프로젝트 구성
feat(notification): 알림 생성 API 추가
test(notification): 알림 상태 전이 테스트 추가
fix(mock-server): 실패 응답 처리 보정
```

기본 type은 다음을 사용한다.

- `feat`: 기능 추가
- `fix`: 버그 수정
- `test`: 테스트 추가/수정
- `docs`: 문서 수정
- `refactor`: 동작 변경 없는 구조 개선
- `chore`: 빌드, 설정, 의존성, 기타 작업

필요해지면 `perf`, `ci` 같은 type을 추가한다.

커밋은 나중에 읽었을 때 의미 있는 단위로 나눈다. `작업`, `수정`, `update`, `fix` 같은 모호한 메시지만 단독으로 쓰지 않는다.

## Issue Rule

Jira 같은 별도 이슈 트래커는 사용하지 않고 GitHub Issues로 작업을 관리한다.

Issue는 작업 하나 또는 실험 하나 단위로 만든다.

GitHub CLI로 issue나 PR 본문을 작성할 때 긴 Markdown을 `--body` 인자로 직접 넘기지 않는다. PowerShell에서 백틱, 따옴표, 줄바꿈이 깨질 수 있으므로 임시 파일 또는 템플릿 파일을 만든 뒤 `--body-file`을 사용한다. 임시 파일은 사용 후 삭제한다.

좋은 예:

- `Gradle 멀티프로젝트 스캐폴딩`
- `notification 알림 생성 API 구현`
- `mock-server 알림 발송 mock 구현`
- `동기 발송 baseline 부하 테스트`

너무 큰 예:

- `M1 구현`
- `알림 서비스 만들기`
- `Kafka 적용`

Issue 본문은 최소한 다음 구조를 사용한다.

```md
## Goal

## Scope

## Notes
```

초기 label은 최소로 둔다.

- `type:task`
- `type:bug`
- `type:experiment`
- `type:docs`
- `area:notification`
- `area:mock-server`
- `area:build`
- `area:docs`
- `status:blocked`

혼자 진행하는 프로젝트이므로 issue assignee는 기본적으로 `LiamKim-DaeYong`으로 둔다. assignee 적용이 실패하면 비워두고 진행한다.

Milestone과 Project board는 이슈가 많아지거나 흐름 관리가 필요해질 때 도입한다.

## GitHub Template Rule

GitHub Issues는 최소한 task와 experiment 템플릿을 사용한다.

- `task`: 구현, 문서, 설정 같은 일반 작업
- `experiment`: 부하 테스트, 장애 주입, 성능 비교 같은 실험 작업

PR 템플릿은 다음 항목을 기본으로 한다.

```md
## Summary

## Changes

## Verification

## Notes
```

템플릿은 작업을 돕기 위한 최소 구조로 유지한다. 작성 부담이 커질 정도로 세분화하지 않는다.

## Review Rule

GitHub PR 리뷰 보조 도구로 CodeRabbit을 활용할 수 있다.

CodeRabbit은 코드 변경의 위험, 누락된 테스트, 설계상 어색한 부분을 확인하는 용도로 사용한다.

poem, 과한 요약, 장식성 메시지처럼 리뷰 판단에 직접 도움이 되지 않는 출력은 설정에서 제거한다.

CodeRabbit 설정 파일은 GitHub 연동 시점에 공식 설정 문서를 확인한 뒤 추가한다.

## Branch Protection Rule

혼자 진행하는 프로젝트이므로 브랜치 보호는 개발 흐름을 막지 않는 수준으로 둔다.

CodeRabbit approval은 merge required 조건으로 두지 않는다. CodeRabbit은 리뷰 보조 도구이며 최종 판단은 사용자가 한다.

CI가 도입되면 build, test, ktlintCheck 같은 기계적 검증만 required check 후보로 둔다.

## Harness Rule

로컬 개발과 검증의 공통 진입점은 PowerShell 스크립트로 둔다.

초기 스크립트는 다음 이름을 기준으로 한다.

- `scripts/check.ps1`: ktlint, test, build 같은 기본 검증
- `scripts/up.ps1`: 로컬 의존성 실행
- `scripts/down.ps1`: 로컬 의존성 종료
- `scripts/smoke.ps1`: Walking Skeleton 핵심 흐름 확인

실행 가능한 실험 스크립트와 raw 결과는 `experiments/`에 남긴다.

실험 해석과 회고는 `docs/notes/`에 남기고, 블로그 재료는 `docs/blog/`에 남긴다.
