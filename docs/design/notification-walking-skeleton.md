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

## API Contract

Walking Skeleton에는 발송 요청 API와 상태 조회 API가 필요하다.

초기 API URL은 다음으로 결정한다.

```text
POST /api/v1/notifications
GET /api/v1/notifications/{notificationId}
```

`POST /api/v1/notifications`는 알림 발송을 요청한다.

초기 요청은 다음 정보만 포함한다.

- channel
- recipient
- title
- content

`GET /api/v1/notifications/{notificationId}`는 저장된 알림 상태를 조회한다.

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

이 URL로 결정한 이유는 다음과 같다.

- `notifications`는 알림 발송 단위 리소스로 설명하기 쉽다.
- `notification-requests`보다 상태 조회, 이력, 재시도, 결과 추적까지 확장하기 자연스럽다.
- `api/v1`은 초기 공개 API 계약을 명시한다. 버전 없는 내부 API로 시작할 수도 있지만, 포트폴리오와 API 문서화 관점에서는 버전이 있는 편이 설명하기 쉽다.

검토했지만 선택하지 않은 후보는 다음과 같다.

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

첫 번째 후보는 간단하지만 API 계약의 버전 경계가 드러나지 않는다.

두 번째 후보는 요청 접수라는 초기 흐름에는 맞지만, 나중에 상태 조회, 이력, 재시도, 발송 결과 추적까지 포함하면 리소스 이름이 좁아질 수 있다.

세 번째 후보는 선택한 URL과 거의 같지만 path parameter 이름을 `id`보다 `notificationId`로 명시하는 쪽이 문서와 코드에서 의미를 더 분명히 드러낸다.

## Status Model

상태는 URL처럼 변경 비용이 큰 계약이다.

상태 값은 DB, API 응답, 운영 지표, 실험 결과, 블로그 설명에 함께 남는다. 따라서 단순히 구현하기 쉬운 이름보다 나중에 의미가 흔들리지 않는 이름을 선택해야 한다.

Walking Skeleton에는 최소한 다음 의미를 표현할 수 있어야 한다.

- Oort가 요청을 접수하고 저장한 상태
- Oort가 외부 발송 시스템을 호출 중인 상태
- 외부 발송 시스템이 발송 요청을 수락한 상태
- Oort가 처리 중 실패를 확인한 상태

초기 상태는 다음 네 개로 결정한다.

```text
ACCEPTED
DISPATCHING
DISPATCHED
FAILED
```

의미는 다음과 같다.

- `ACCEPTED`: Oort가 요청을 접수하고 저장했다.
- `DISPATCHING`: Oort가 외부 발송 시스템을 호출하고 있다.
- `DISPATCHED`: 외부 발송 시스템이 발송 요청을 수락했다.
- `FAILED`: Oort가 처리 중 실패를 확인했다.

`DELIVERED`는 초기 상태에 넣지 않는다. 실제 사용자 도달 확인이나 vendor callback을 다루는 시점에 별도로 검토한다.

재시도, 부분 실패, 취소, 만료 같은 상태는 아직 다루지 않는다.

## Benchmarking Rationale

notification이나 messaging API를 제공하는 서비스의 공식 문서를 참고했다.

이 조사는 결정의 근거를 남기기 위한 것이다. Oort가 외부 서비스를 그대로 따라 하지는 않지만, 공개 API와 상태 모델을 설계할 때 어떤 표현이 오래 버티는지 확인했다.

| Service | URL Shape | Status Shape |
| --- | --- | --- |
| Twilio Messaging | `Messages` 리소스를 생성하고 조회한다. | `accepted`, `queued`, `sending`, `sent`, `delivered`, `undelivered`, `failed`처럼 접수, 처리, 발송사 수락, 전달 확인, 실패를 구분한다. |
| Firebase Cloud Messaging | `messages:send` action으로 메시지 발송을 요청한다. | 성공 응답으로 message name을 반환하며, Oort처럼 상태 조회 리소스를 직접 제공하는 형태와는 다르다. |
| OneSignal | `notifications` 리소스를 생성하고 조회한다. | 단일 상태 enum보다 `successful`, `failed`, `errored`, `received`, `completed_at` 같은 결과와 집계 필드를 함께 사용한다. |
| SendGrid | `mail/send`로 발송하고, 이후 상태는 Event Webhook으로 관찰한다. | `processed`, `delivered`, `deferred`, `bounce`, `dropped`처럼 이메일 전달 과정의 이벤트를 구분한다. |
| Mailgun | message 발송과 event/log 조회를 분리한다. | `accepted`, `delivered`, `temporary fail`, `permanent fail`, `rejected`처럼 접수, 전달, 일시 실패, 영구 실패를 구분한다. |
| Amazon SNS | `Publish` action으로 메시지를 발행한다. | 발행 성공 시 message id를 반환하고, delivery status는 별도 logging으로 관찰한다. |

이 비교에서 얻은 결론은 다음과 같다.

- URL은 `request`보다 `message` 또는 `notification` 리소스 이름을 많이 쓴다.
- `send`, `publish`처럼 action 중심 API를 쓰는 서비스도 있다. 이 경우 상태 조회 리소스가 약하거나 별도 관측 체계에 가깝다.
- 상태는 단순히 성공/실패만 두기보다 접수, 처리 중, 외부 시스템 수락, 최종 전달, 실패를 구분하는 경우가 많다.
- `sent`와 `delivered`는 같은 의미가 아니다. `sent`는 외부 시스템이나 carrier에 넘겨진 상태에 가깝고, `delivered`는 수신 측 전달 확인에 가깝다.
- Oort의 초기 mock-server는 실제 사용자 도달을 확인하지 않으므로 `DELIVERED`를 초기 상태로 쓰면 의미가 과해질 수 있다.

따라서 Walking Skeleton에서는 `/api/v1/notifications`를 발송 단위 리소스로 사용하고, 상태는 실제 전달 완료가 아니라 Oort 내부 처리와 외부 발송 시스템 수락 여부를 표현하는 범위로 제한한다.

## Known Tradeoffs

이번 API URL과 상태 모델은 완벽한 이름을 찾은 결과가 아니라, 현재 범위에서 가장 덜 좁고 의미가 덜 흔들리는 선택이다.

- `notifications`는 발송 요청과 발송 결과를 모두 포괄하는 넓은 이름이다. 대신 상태 조회, 이력, 재시도, 발송 결과 추적까지 같은 발송 단위 리소스로 확장하기 쉽다.
- `DISPATCHED`는 실제 사용자 도달을 의미하지 않는다. 이 의미를 지키기 위해 delivery 확인이 들어오기 전까지 `DELIVERED`를 사용하지 않는다.
- `FAILED`는 초기에는 실패를 하나로 묶는다. 재시도 가능 여부, 벤더 실패, 요청 검증 실패 같은 구분은 필요성이 확인되면 별도 필드나 상태로 분리한다.
- `/api/v1`은 초기 공개 API 계약을 명시하지만, 실제 버전 운영 정책까지 정한 것은 아니다. 버전 호환성 정책은 API가 외부 계약으로 커질 때 별도로 다룬다.

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
