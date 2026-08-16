# Notification identifier strategy: UUID v7

## Context

notification의 식별자는 생성 직후 API 응답과 후속 조회에 사용되고 PostgreSQL의 primary key로 저장된다. 현재는 domain이 UUID v4를 직접 생성한다. UUID v4는 표준적이고 충돌 위험이 낮지만, 무작위 값이라 B-tree primary key에 삽입할 때 locality가 낮다.

현재 Compose 기준 PostgreSQL 16을 사용 중이라 native UUID v7 생성 함수는 사용할 수 없다. PostgreSQL 18에는 UUID v7 생성 함수가 추가됐다.

## Decision

신규 notification ID는 애플리케이션의 `NotificationIdGenerator` output port를 통해 RFC 9562 UUID v7으로 생성한다. 구현은 JUG(`java-uuid-generator`) adapter가 맡고, domain은 생성된 ID를 입력으로 받는다.

PostgreSQL 18의 `uuidv7()`을 사용하더라도 ID 생성 책임을 DB로 옮기지 않는다. 애플리케이션에서 생성하면 DB 버전과 구현에 묶이지 않고, persistence round-trip 전에 ID를 사용할 수 있다. `NotificationIdGenerator` port는 domain/application 테스트에서 생성 ID를 결정적으로 제어한다. 따라서 PostgreSQL 업그레이드만으로 ID 생성 주체를 변경하지 않으며, DB 생성 전환은 별도 성능·운영 근거가 있을 때만 검토한다.

PostgreSQL 컬럼 타입과 HTTP API의 UUID 문자열 계약은 유지한다. 기존 UUID v4 데이터는 유효한 UUID이므로 재작성하지 않고 계속 조회한다. 이 전환은 새 데이터에만 적용하는 forward-only 변경이며 Flyway migration은 필요 없다.

Snowflake는 이번에는 선택하지 않는다. 64-bit 정수 ID와 높은 발급 처리량이 명시적으로 필요한 상황은 아직 없고, worker ID 할당, 시계 역행, 시퀀스 고갈 같은 운영 정책을 지금 도입할 이유가 부족하다.

## Consequences

- 시간 순서가 앞부분에 반영된 UUID v7은 UUID v4보다 PostgreSQL B-tree 삽입 locality에 유리할 수 있다.
- ID 생성 책임은 애플리케이션에 남으므로 PostgreSQL 버전과 무관하고, persistence 전에 ID를 사용할 수 있다.
- UUID v7에는 생성 시각 정보가 일부 포함된다. ID는 인증 또는 권한 검증 수단으로 사용하지 않는다.
- PostgreSQL 18로 업그레이드하더라도 ID 생성 주체를 자동으로 DB로 옮기지 않는다. DB 생성 전환은 별도 성능·운영 근거가 있을 때 검토한다.
- 대량 발급, 샤드 간 순서 보장, 숫자형 ID 요구가 관찰되면 Snowflake와 UUID v7을 다시 비교한다.

## Alternatives considered

### Keep UUID v4

구현과 호환성은 가장 단순하지만, ID 계약을 초기에 정할 수 있는 시점에 시간 정렬 특성을 포기할 이유가 부족하다.

### Snowflake

짧은 64-bit ID와 높은 처리량에는 적합하지만, 현재 규모에서는 생성기 운영 정책의 비용이 이점보다 크다.

### Database-generated UUID v7

현재 PostgreSQL 16에서는 지원하지 않는다. PostgreSQL 18에서는 사용할 수 있지만, 생성 책임을 DB로 옮기면 DB 버전과 구현에 결합되고 발급된 ID를 사용하려면 persistence round-trip이 필요하다.

## References

- [RFC 9562: Universally Unique IDentifiers](https://www.rfc-editor.org/rfc/rfc9562.html)
- [PostgreSQL 18 release notes](https://www.postgresql.org/docs/18/release-18.html)
- [JUG Java UUID Generator](https://github.com/cowtowncoder/java-uuid-generator)
