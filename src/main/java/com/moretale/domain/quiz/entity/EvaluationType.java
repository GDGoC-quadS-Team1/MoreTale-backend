package com.moretale.domain.quiz.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EvaluationType {
    VOCABULARY("단어 이해"),    // 단어 뜻을 알아야 풀 수 있는 문제
    STORY("줄거리 이해");       // 내용을 읽어야 풀 수 있는 문제

    private final String description;
}
