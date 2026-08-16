# SonarQube Cloud 품질 피드백 관찰

## 목적

main과 Pull Request에서 SonarQube Cloud 분석 결과를 확인하고, 초기 피드백이 실제로 유효한지 관찰한다.

## 초기 운영 기준

- GitHub Actions가 main과 Pull Request(draft 포함)에서 Gradle 기반 분석을 실행한다.
- `SONAR_TOKEN`은 GitHub Actions Secret으로만 제공한다.
- 분석은 Quality Gate 결과를 표시하지만, `sonar.qualitygate.wait`를 사용하지 않는다.
- 최초 3~5개 PR 동안 Quality Gate는 merge required check으로 설정하지 않는다.

## 최초 분석 결과

자동 분석으로 main의 `c62b907` 커밋을 분석했다.

- Security: A, 열린 이슈 0개
- Reliability: A, 열린 이슈 0개
- Maintainability: A, 열린 이슈 6개
- Coverage: 아직 수집하지 않음
- Quality Gate: 첫 분석 시점에는 계산되지 않음

이 결과는 GitHub Actions 분석을 연결하기 전의 기준선이다. 이후 PR 분석에서 이슈의 유효성, 노이즈, CI 실행 시간과 운영 부담을 함께 기록한다.

## 관찰 기록

| PR | 분석 결과 | 유효한 피드백 | 노이즈 또는 운영 부담 | 결정 |
| --- | --- | --- | --- | --- |
| 초기 기준선 | main 자동 분석 | 관찰 시작 | Coverage 미수집, Quality Gate 미계산 | GitHub Actions 분석 연결 후 재평가 |
