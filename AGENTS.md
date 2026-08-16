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
구현에 참고할 현재 설계 문서는 `docs/design/`에 남긴다.
작업 기록, 실험 결과, 회고는 `docs/notes/`에 남긴다.
학습 내용은 `docs/learning/`에 남긴다.
블로그 재료는 `docs/blog/`에 남긴다.

## Documentation Rule

모든 내용을 ADR로 남기지 않는다.

`docs/decisions/`에는 되돌리기 비용이 크거나 아키텍처 흐름을 바꾸는 결정을 남긴다.
`docs/design/`에는 도메인 모델, 상태 전이, API flow, 정책 초안 같은 현재 설계 문서를 남긴다.
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

모든 변경 작업은 GitHub Issue를 먼저 만들고 시작한다. Issue 번호는 브랜치, 커밋, PR을 연결하는 작업 식별자다.

- 브랜치는 `feature/{issue-number}-{short-kebab-case}` 형식을 사용한다.
- 커밋 메시지는 Conventional Commits 형식 뒤에 `(#<issue-number>)`을 붙인다.
- PR 본문에는 반드시 `Closes #<issue-number>`를 넣는다.
- Issue는 PR을 만들기 전에 닫지 않는다. PR이 main에 merge되어 GitHub가 자동으로 닫게 한다.

커밋 메시지는 Conventional Commits 형식을 가볍게 따른다.

```text
type(scope): 한글 설명 (#<issue-number>)
```

`scope`는 선택이지만 가능하면 적는다.

예:

```text
docs: 프로젝트 킥오프 문서 정리 (#1)
chore(build): Gradle 멀티프로젝트 구성 (#3)
feat(notification): 알림 생성 API 추가 (#13)
test(notification): 알림 상태 전이 테스트 추가 (#13)
fix(mock-server): 실패 응답 처리 보정 (#13)
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

## Codex Worktree Rule

Codex가 저장소에 남는 변경을 수행할 때는 원본 checkout과 분리된 Git worktree를 기본 작업 공간으로 사용한다. IntelliJ에서 사용하는 원본 checkout은 `main` 기준의 사용자 작업 공간으로 유지하며, Codex는 그 checkout의 `main`에서 파일 수정, 브랜치 전환, 커밋, push를 수행하지 않는다. 사용자는 원본 checkout에서 변경을 검토하고 PR을 병합한다.

로컬 작업 예외는 다음처럼 좁게 둔다.

- 원본 checkout에서는 상태 확인, diff 확인, 테스트처럼 파일을 바꾸지 않는 작업만 기본적으로 허용한다.
- 사용자가 특정 로컬 checkout 또는 기존 worktree에서의 직접 작업을 명시적으로 요청한 경우에만 그 위치를 사용할 수 있다. 이때 대상 경로와 브랜치를 먼저 확인하고, `main`이 아니며 관련 없는 미커밋 변경이 없어야 한다.
- 새 worktree를 만들 수 없거나 기존 task worktree를 이어받는 경우에도, 작업 위치만 달라질 뿐 기존 Issue, 브랜치, 커밋, PR 규칙은 그대로 적용한다.

하나의 변경 작업은 하나의 Issue, `feature/{issue-number}-{short-kebab-case}` 브랜치, 별도 worktree, PR을 연결한다. worktree는 같은 Issue의 브랜치를 checkout하며, PR 본문의 `Closes #<issue-number>` 규칙을 따른다. worktree 생성은 Issue와 브랜치 규칙을 우회하기 위한 수단이 아니다.

병렬 작업은 서로 다른 Issue, 브랜치, worktree로 나눈다. 같은 branch 또는 같은 worktree를 동시에 사용하지 않으며, 같은 파일을 수정해야 하면 선행 작업의 커밋 또는 PR 상태를 확인한 뒤 순차로 진행한다. 특히 공통 문서와 빌드 설정은 병렬 변경 충돌 가능성이 높으므로 한 작업으로 조정하거나 담당 순서를 합의한다.

작업 시작과 커밋 직전에는 `git status`와 diff로 미커밋 변경을 확인한다. 관련 없는 변경이 있으면 stash, reset, 삭제, 덮어쓰기를 하지 않고 사용자에게 확인을 요청한다. 커밋에는 현재 Issue와 관련된 파일만 명시적으로 stage하며, 다른 작업의 미커밋 변경을 포함하지 않는다.

## Issue Rule

Jira 같은 별도 이슈 트래커는 사용하지 않고 GitHub Issues로 작업을 관리한다.

Issue는 작업 하나 또는 실험 하나 단위로 만든다.

코드, 설정, 문서, 실험 결과처럼 저장소에 남는 변경은 모두 Issue를 먼저 만든다. 아직 변경하지 않는 설계 논의나 조사만 수행할 때는 Issue를 바로 만들지 않아도 된다.

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

Task Issue 본문은 최소한 다음 구조를 사용한다.

```md
## 목표

## 범위

## 완료 조건

## 참고 사항
```

초기 label은 최소로 둔다.

- `type:task`
- `type:bug`
- `type:experiment`
- `type:docs`
- `service:notification`
- `service:mock-server`
- `status:blocked`

모든 Issue에는 `type:*` label을 정확히 하나 붙인다. 특정 실행 서비스에 영향을 주는 작업에만 `service:*` label을 하나 추가한다. 실제로 진행이 막힌 경우에만 `status:blocked`를 추가한다.

라벨은 다음 순서로 선택한다.

1. 작업 성격에 맞는 `type:*` label을 정확히 하나 선택한다.
2. 실행 서비스의 코드나 설정에 영향을 주는 경우에만 해당 `service:*` label을 하나 추가한다.
3. 외부 권한, 결정 또는 상태 변화가 없어 진행할 수 없는 경우에만 `status:blocked`를 추가하고, 재개하면 제거한다.

| 분류 | 선택 기준 |
| --- | --- |
| `type:task` | 기능, 설정, 일반 구현 작업 |
| `type:bug` | 기대 동작과 실제 동작의 차이를 수정하는 작업 |
| `type:experiment` | 가설, 측정 지표, 결과 해석이 있는 실험 작업 |
| `type:docs` | 실행 동작을 바꾸지 않는 문서·협업 규칙 작업 |
| `service:notification` | notification 서비스의 코드 또는 실행 설정에 영향을 주는 작업 |
| `service:mock-server` | mock-server 서비스의 코드 또는 실행 설정에 영향을 주는 작업 |
| `status:blocked` | 실제 진행이 외부 요인으로 막힌 경우에만 사용 |

`area:*`, priority, 진행 중, 리뷰 대기 같은 label은 현재 사용하지 않는다. 새로운 서비스나 횡단 주제 label은 실제로 필터링·분류할 필요가 반복될 때 별도로 논의한다.

혼자 진행하는 프로젝트이므로 issue assignee는 기본적으로 `LiamKim-DaeYong`으로 둔다. assignee 적용이 실패하면 비워두고 진행한다.

Milestone과 Project board는 이슈가 많아지거나 흐름 관리가 필요해질 때 도입한다.

## GitHub Template Rule

GitHub Issues는 최소한 task와 experiment 템플릿을 사용한다.

- `task`: 구현, 문서, 설정 같은 일반 작업
- `experiment`: 부하 테스트, 장애 주입, 성능 비교 같은 실험 작업

PR 템플릿은 다음 항목을 기본으로 한다.

```md
## 요약

<!-- 작업의 목적과 사용자 가치를 1~3문장으로 기록합니다. -->

Closes #

## 변경 사항

<!-- 주요 변경과 영향을 받는 영역을 기록합니다. -->

## 검증

<!-- 실행한 검증 명령과 결과를 기록합니다. 미실행 항목이 있다면 이유를 남깁니다. -->

## 참고 사항

<!-- 설계 판단, 제약, 남은 위험 또는 리뷰 시 확인할 점을 기록합니다. 없으면 없음으로 남깁니다. -->
```

템플릿은 작업을 돕기 위한 최소 구조로 유지한다. 작성 부담이 커질 정도로 세분화하지 않는다.

## Branch Protection Rule

혼자 진행하는 프로젝트이므로 브랜치 보호는 개발 흐름을 막지 않는 수준으로 둔다.

CI가 도입되면 build, test, ktlintCheck 같은 기계적 검증만 required check 후보로 둔다.

## Local Development Rule

로컬 개발과 검증의 공통 진입점은 Gradle Wrapper와 Docker Compose로 둔다.

- 빌드, 테스트, ktlint 검증은 Gradle Wrapper로 실행한다.
- PostgreSQL과 mock-server 같은 로컬 의존성은 Docker Compose로 실행한다.
- macOS/Linux에서는 `./gradlew`, Windows에서는 `.\gradlew.bat`를 사용한다.
- 특정 운영체제 전용 셸 스크립트는 공통 진입점으로 추가하지 않는다.

Walking Skeleton의 HTTP 스모크 검증은 재사용 가능한 교차 플랫폼 방식이 필요해질 때 별도 작업으로 설계한다.

실행 가능한 실험 스크립트와 raw 결과는 `experiments/`에 남긴다.

실험 해석과 회고는 `docs/notes/`에 남기고, 블로그 재료는 `docs/blog/`에 남긴다.

Issue 생성과 브랜치 생성 같은 workflow 자동화는 같은 수동 흐름이 반복되고 입력·예외가 안정화된 뒤 별도 Issue로 도입한다. 현재는 검증 하네스(`check`, `up`, `down`, `smoke`)를 유지하고 GitHub workflow는 명시적인 단계로 수행한다.
