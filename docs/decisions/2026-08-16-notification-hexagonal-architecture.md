# Notification Hexagonal Architecture

## Context

notification 서비스는 HTTP API로 발송 요청을 받고, PostgreSQL에 상태를 저장한 뒤 외부 발송사를 HTTP로 호출한다.

초기 Walking Skeleton은 `api`, `application`, `domain`, `infrastructure` 최상위 계층으로 시작했다. 이 구조에서는 domain의 `Notification`이 JPA 어노테이션을 가지고, repository가 Spring Data JPA를 직접 상속했다. application service도 Spring `@Service`에 직접 의존했다.

이 서비스는 이후 외부 발송사, 동기·비동기 처리, 실패 처리를 비교·실험할 대상이다. 현재도 Web, JPA, HTTP라는 실제 외부 의존성이 있으므로, 의존성 경계를 지금 명시하는 비용이 이후 전환 비용보다 작다.

## Decision

notification 서비스를 현재 구현 범위에서 Hexagonal Architecture로 전환한다.

- `io.oort.NotificationApplication`을 bootstrap root로 두고, `io.oort.notification`을 애플리케이션 모듈로 둔다.
- `domain`은 상태 전이와 도메인 타입만 가진다.
- `application.port.input`은 use case와 요청·응답 모델을, `application.port.output`은 영속성과 발송사 호출 계약을 가진다.
- `application.service`는 input port를 구현하고 output port에만 의존한다.
- HTTP controller는 `adapter.input.web`으로, JPA 구현은 `adapter.output.persistence`로, 외부 발송사 HTTP client는 `adapter.output.vendor`로 둔다.
- Spring bean 조립과 `Clock`은 `config`에서 담당한다.

## Consequences

- domain과 application은 Spring Web, Spring Data JPA, HTTP client에 직접 의존하지 않는다.
- JPA entity와 domain model을 분리하므로 저장·조회 시 명시적인 변환이 필요하다.
- 외부 발송사 또는 영속성 구현을 교체해도 application service와 domain의 변경 범위를 줄일 수 있다.
- domain/application 테스트는 Spring context 없이 빠르게 유지한다.

## Alternatives Considered

### 기존 계층형 구조 유지

현재 파일 수는 적지만, 기술 계층이 최상위에 노출되어 이후 기능 또는 adapter 추가 시 변경 관련 코드를 함께 찾기 어렵다. 또한 framework 의존성이 domain까지 침투한 상태를 유지하게 된다.

### 재시도, worker, outbox까지 미리 추가

현재 Walking Skeleton에서 실제로 쓰이지 않는 정책과 인프라다. port/adapter 경계만 먼저 도입하고, 비동기 처리나 재시도는 문제가 관찰되는 후속 작업에서 추가한다.
