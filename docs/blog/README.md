# Blog

블로그 글을 쓰기 위한 재료, 제목 후보, 글 구조, 이력서 문장 후보를 정리한다.

완성된 블로그 글을 대신 작성하는 공간이 아니다. 최종 글은 사용자가 자기 말로 작성한다.

## File Naming

블로그 재료는 주제명으로 관리한다.

예:

- `sync-notification-baseline.md`
- `kafka-async-notification.md`
- `idempotent-notification-send.md`
- `vendor-failure-isolation.md`

## Title Rule

제목에는 `Oort` prefix를 강제하지 않는다.

각 글은 프로젝트명보다 문제, 질문, 결과가 먼저 드러나도록 쓴다.

예:

- `동기 알림 발송 구조는 어디서 병목이 생길까`
- `Kafka를 붙이면 알림 요청 API는 왜 빨라질까`
- `At-least-once 환경에서 중복 발송을 어떻게 막을까`
- `외부 발송사 장애가 전체 서비스 장애로 번지지 않게 하기`

## Tags

Oort 관련 글은 블로그 플랫폼에서 `oort` 태그로 묶는다.

세부 주제 태그를 함께 붙인다.

예:

```yaml
tags:
  - oort
  - notification
  - backend
  - performance
```

## Article Structure

최종 블로그 글은 다음 흐름을 기본으로 한다.

```text
문제 제기 -> 현재 구조 -> 관찰 -> 원인 분석 -> 선택 -> 트레이드오프 -> 정리
```

각 섹션의 역할은 다음과 같다.

- `문제 제기`: 이 글에서 다룰 문제를 먼저 적는다. 기술명보다 왜 이 문제가 생겼는지를 앞에 둔다.
- `현재 구조`: 문제가 발생한 당시의 구조를 설명한다. 필요하면 간단한 flow나 다이어그램을 넣는다.
- `관찰`: 부하 테스트, 로그, 메트릭, 실패 상황 등 실제로 확인한 내용을 적는다. 숫자가 있으면 여기에 둔다.
- `원인 분석`: 관찰한 현상이 왜 발생했는지 설명한다. 추측과 확인된 사실을 구분한다.
- `선택`: 어떤 방향으로 개선하거나 다음 구현을 선택했는지 적는다. 기술을 도입했다면 여기서 이유를 설명한다.
- `트레이드오프`: 좋아진 점과 감수한 비용을 함께 적는다.
- `정리`: 이번 글에서 배운 점과 다음 글 또는 다음 실험으로 이어질 질문을 남긴다.

## Tone Guide

- 정답을 설명하기보다 문제를 관찰하고 이해한 과정을 설명한다.
- 과장하지 않는다.
- 숫자가 없으면 성능 개선을 주장하지 않는다.
- 기술명을 제목과 도입부에 앞세우지 않는다.
- 모르는 부분은 모른다고 쓰고, 다음에 확인할 질문으로 남긴다.
- 면접에서 직접 말할 수 있는 문장으로 정리한다.

## Suggested Format

```md
# Blog Notes: Topic

## Title Candidates

## One-line Summary

## Problem

## Context

## Experiment

## Result

## Analysis

## Trade-offs

## What I Learned

## Resume Sentence

## Interview Questions
```
