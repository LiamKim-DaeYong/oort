# GitHub Main Branch Protection

## Goal

`main`은 항상 검증 가능한 기준 브랜치로 유지하고, merge가 끝난 작업 브랜치는 자동으로 정리한다.

## Repository Settings

`main`에만 다음 ruleset을 적용한다.

- Pull Request를 통해서만 변경한다.
- `Gradle Check`가 성공해야 merge할 수 있다.
- force push를 허용하지 않는다.
- `main` 브랜치 삭제를 허용하지 않는다.
- 필수 reviewer approval 수는 0명으로 유지한다.
- bypass 대상은 별도로 두지 않는다.

Repository의 general setting에서 merge된 Pull Request의 head branch 자동 삭제를 활성화한다.

## Rationale

혼자 진행하는 프로젝트에서도 Issue, branch, Pull Request, CI를 연결해 변경 의도와 검증 결과를 남긴다.

필수 reviewer approval은 실제 리뷰어가 없는 상황에서 작업을 막기 때문에 강제하지 않는다. 대신 Pull Request와 성공한 CI를 병합 기준으로 둔다.

ruleset의 삭제 금지는 `main`에만 적용한다. merge된 feature branch 자동 삭제와 충돌하지 않는다.

## Verification

- GitHub repository settings에서 `main` ruleset이 active 상태인지 확인한다.
- Pull Request의 merge box에서 `Gradle Check`가 필수 상태 검사로 표시되는지 확인한다.
- merge 후 source feature branch가 원격에서 자동 삭제되는지 다음 Pull Request로 확인한다.
