# Notification Walking Skeleton

이 문서는 notification 서비스의 첫 Walking Skeleton 범위를 정리한다.

Walking Skeleton은 완성된 기능이 아니다. 전체 시스템이 어떤 모양으로 연결될지 가장 얇은 흐름으로 확인하기 위한 기준이다.

## Goal

첫 목표는 알림 발송 요청이 notification 서비스와 mock-server를 거쳐 다시 조회 가능한 상태로 남는지 확인하는 것이다.

```text
Client
  -> notification
  -> mock-server
  -> notification
  -> Client
```

이 흐름이 연결되면 이후 성능, 실패, 비동기 처리, 멱등성 같은 주제를 실제 코드 위에서 다룰 수 있다.

## Flow

초기 흐름은 다음과 같다.

1. 클라이언트가 알림 발송을 요청한다.
2. notification 서비스가 요청을 저장한다.
3. notification 서비스가 mock-server에 발송을 요청한다.
4. mock-server가 성공 또는 실패 응답을 반환한다.
5. notification 서비스가 발송 결과를 저장한다.
6. 클라이언트가 알림 상태를 조회한다.

## Components

초기 흐름에 등장하는 구성 요소는 세 개만 둔다.

- Client: HTTP 요청을 보내는 호출자다. 처음에는 curl, HTTPie, Postman, k6 script가 될 수 있다.
- notification: 알림 요청을 접수하고, 상태를 저장하고, mock-server를 호출한다.
- mock-server: 실제 발송사를 대신하는 외부 시스템 역할을 한다.

Kafka, Redis, 별도 worker는 아직 구성 요소에 넣지 않는다.

## API Shape To Decide

Walking Skeleton에는 발송 요청 API와 상태 조회 API가 필요하다.

다만 endpoint URL은 클라이언트와 맺는 계약이므로 구현 전에 따로 결정한다. 이 문서에서는 필요한 API의 역할만 먼저 정리한다.

필요한 API 역할은 두 개다.

```text
POST {notification-request-url}
GET {notification-status-url}
```

발송 요청 API는 알림 발송을 요청한다.

초기 요청은 다음 정보만 포함한다.

- channel
- recipient
- title
- content

상태 조회 API는 저장된 알림 상태를 조회한다.

초기 응답은 다음 정보만 포함한다.

- id
- channel
- recipient
- title
- content
- status
- requestedAt
- completedAt

필드 이름과 타입은 구현하면서 조정할 수 있다. 이 문서에서는 어떤 정보가 필요한지만 정한다.

## API URL Decision Criteria

API URL은 다음 기준으로 결정한다.

- 리소스 이름이 notification 서비스의 책임을 정확히 드러내는가?
- 요청 접수와 실제 발송 결과 조회를 자연스럽게 표현하는가?
- 나중에 대량 발송, 예약 발송, 템플릿이 들어와도 과하게 좁아지지 않는가?
- 외부에 공개됐을 때 설명하기 쉬운가?
- 버저닝을 URL에 둘지, 문서와 호환성 정책으로 관리할지 판단했는가?

현재 후보는 다음과 같다.

```text
POST /notifications
GET /notifications/{id}
```

```text
POST /notification-requests
GET /notification-requests/{id}
```

```text
POST /api/v1/notifications
GET /api/v1/notifications/{id}
```

Walking Skeleton 구현 전에 이 후보 중 하나를 선택하거나 다른 후보를 추가한다.

## Initial Status

초기 상태는 세 개만 둔다.

- PENDING: 요청은 저장됐지만 아직 결과가 정해지지 않았다.
- SENT: mock-server 발송 요청이 성공했다.
- FAILED: mock-server 발송 요청이 실패했다.

재시도, 부분 실패, 취소, 만료 같은 상태는 아직 다루지 않는다.

## Persistence

notification 서비스는 발송 요청과 결과를 PostgreSQL에 저장한다.

처음에는 조회와 실험을 위해 필요한 최소 정보만 저장한다.

- 요청 식별자
- 발송 채널
- 수신자
- 제목
- 내용
- 상태
- 요청 시각
- 완료 시각

스키마 변경은 Flyway migration으로 관리한다.

## Mock Server

mock-server는 실제 SMS, Email, Push 발송사를 구현하지 않는다.

초기에는 notification 서비스가 외부 시스템을 호출한다는 사실을 확인할 수 있으면 충분하다.

처음에는 다음 기능만 필요하다.

- 알림 발송 요청을 받는다.
- 성공 응답을 반환한다.

지연 주입, 실패 주입, 채널별 응답 차이는 Walking Skeleton 이후 동기 발송 baseline을 측정할 때 추가한다.

## Out Of Scope

이번 범위에서 제외하는 것은 다음과 같다.

- Kafka 기반 비동기 처리
- Redis
- 멱등키
- 재시도
- 다중 발송사
- 대량 발송
- 예약 발송
- 템플릿 관리
- 사용자 수신 동의 검증
- 인증과 권한
- 운영용 dashboard

이 항목들은 중요하지 않아서 제외하는 것이 아니다. 첫 흐름을 얇게 연결한 뒤 실제 문제가 관찰되는 시점에 하나씩 도입한다.

## Questions To Observe

Walking Skeleton을 만들면서 다음 질문을 확인한다.

- notification 서비스가 맡는 책임과 mock-server가 맡는 책임이 자연스럽게 분리되는가?
- API URL이 서비스 책임과 장기 확장 방향을 자연스럽게 표현하는가?
- 요청 저장과 외부 호출, 결과 저장의 순서가 설명 가능한가?
- 실패 응답을 받았을 때 상태를 어디까지 남길 수 있는가?
- 처음부터 동기 호출로 만들었을 때 어떤 한계가 생길 가능성이 있는가?
- API 요청과 응답이 이후 실험에 필요한 정보를 충분히 담는가?

## Next Step

이 문서가 받아들여지면 다음 작업은 구현을 위한 가장 작은 vertical slice다.

1. mock-server에 알림 발송 endpoint를 만든다.
2. notification에 알림 생성 API를 만든다.
3. notification에서 mock-server를 동기 호출한다.
4. PostgreSQL에 요청과 결과를 저장한다.
5. 상태 조회 API를 만든다.
6. `scripts/smoke.ps1`로 전체 흐름을 확인한다.
