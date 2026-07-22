# Documents

Oort의 설계 결정, 작업 기록, 학습 기록, 블로그 재료를 남기는 공간이다.

- `decisions/`: 프로젝트의 중요한 설계 결정과 그 이유를 기록한다.
- `notes/`: 작업 기록, 실험 결과, 회고, 임시 합의를 날짜별로 기록한다.
- `learning/`: 프로젝트를 진행하며 이해한 백엔드 개념과 면접 대비 내용을 주제별로 정리한다.
- `blog/`: 블로그 글을 쓰기 위한 재료, 제목 후보, 글 구조, 이력서 문장 후보를 정리한다.

## File Naming

문서 파일명은 찾기 쉽도록 최소 규칙만 둔다.

- `decisions/`: `0001-kebab-case-title.md`
- `notes/`: `YYYY-MM-DD-kebab-case-title.md`
- `learning/`: `kebab-case-topic.md`
- `blog/`: `kebab-case-topic.md`

문서 내부 형식은 지금 고정하지 않는다. 필요해지면 프로젝트를 진행하며 조정한다.

## Policies And Flows

정책 문서와 flow 문서는 초기에는 `notes/`에 남긴다.

예:

- `YYYY-MM-DD-notification-sync-flow.md`
- `YYYY-MM-DD-notification-status-policy.md`
- `YYYY-MM-DD-retry-policy.md`
- `YYYY-MM-DD-idempotency-policy.md`

아직 정책과 flow가 안정되지 않았기 때문에 별도 폴더를 먼저 만들지 않는다. 문서가 많아지거나 내용이 안정되면 `policies/`, `flows/` 같은 폴더로 분리할 수 있다.

구현 전에 큰 설계 문서를 먼저 만들기보다, 필요한 경우 가벼운 flow note를 작성하고 구현과 관찰 결과에 따라 업데이트한다.

## Learning Notes

학습 문서는 `learning/`에 주제별로 남긴다.

외부 자료를 단순 요약하기보다, Oort를 진행하며 직접 마주친 개념, 헷갈렸던 지점, 프로젝트에서 확인한 내용, 면접에서 설명할 수 있는 문장을 함께 정리한다.

## Blog Notes

블로그 완성 글은 프로젝트 문서에 대신 작성하지 않는다. `blog/`에는 사용자가 자기 말로 글을 쓰기 위한 재료를 남긴다.

블로그 제목에는 `Oort` prefix를 강제하지 않는다. 각 글은 문제, 질문, 결과 중심의 제목을 사용한다.

Oort 관련 글은 블로그 플랫폼에서 `oort` 태그로 묶고, 세부 주제 태그를 함께 붙인다.

## Experiments

실행 가능한 실험 스크립트와 raw 결과는 `experiments/`에 남긴다.

실험의 해석, 회고, 다음 결정은 `docs/notes/`에 남긴다.

블로그에 옮길 재료는 `docs/blog/`에 별도로 정리한다.
