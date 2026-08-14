# 2026-08-14 Issue-first GitHub Workflow

## Decision

저장소에 남는 모든 변경은 GitHub Issue를 먼저 만들고 시작한다. Issue 번호는 작업의 기준 식별자이며 브랜치, 커밋, PR이 같은 번호를 참조한다.

## Workflow

1. Issue를 만들고 Goal, Scope, Notes를 작성한다.
2. `feature/{issue-number}-{short-kebab-case}` 브랜치를 만든다.
3. 커밋 메시지 끝에 `(#<issue-number>)`을 붙인다.
4. draft PR 본문에 `Closes #<issue-number>`을 넣는다.
5. Issue는 PR을 만들기 전에 닫지 않고, main merge 때 GitHub가 자동으로 닫게 한다.

변경하지 않는 설계 논의나 조사는 Issue 없이 시작할 수 있다. 코드, 설정, 문서, 실험 결과처럼 저장소에 남는 변경은 모두 이 흐름을 따른다.

## Harness Boundary

현재 `check`, `up`, `down`, `smoke`는 로컬 개발·검증 하네스 역할을 맡는다.

Issue 생성과 브랜치 생성을 자동화하는 새 workflow 스크립트는 아직 도입하지 않는다. 이 흐름을 반복하면서 필요한 입력, label, assignee 예외가 안정화되면 별도 Issue로 작업을 시작한다.
