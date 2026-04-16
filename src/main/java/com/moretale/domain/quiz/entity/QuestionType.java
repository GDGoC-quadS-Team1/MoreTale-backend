package com.moretale.domain.quiz.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionType {
    MULTIPLE_CHOICE("선다형"),   // 4지 선다형
    TRUE_FALSE("참/거짓");       // T/F형

    private final String description;
}
