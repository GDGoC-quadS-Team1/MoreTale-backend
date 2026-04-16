package com.moretale.domain.quiz.service.impl;

import com.moretale.domain.quiz.entity.*;
import com.moretale.domain.quiz.service.AIQuizService;
import com.moretale.domain.story.entity.Slide;
import com.moretale.domain.story.entity.Story;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * AI 기반 퀴즈 생성 구현체
 *
 * TODO: 실제 LLM API 연동 시 아래 프롬프트 구조 참고:
 *
 * [시스템 프롬프트]
 * "당신은 아동 교육용 퀴즈 생성 전문가입니다.
 *  다음 동화를 읽고, 아이의 단어 이해력과 줄거리 이해력을 동시에 평가하는 퀴즈를 생성하세요.
 *  - 단어를 모르면 풀 수 없는 VOCABULARY 유형 문제를 {vocabCount}개 생성하세요.
 *  - 줄거리를 이해해야 풀 수 있는 STORY 유형 문제를 {storyCount}개 생성하세요.
 *  - 문제 언어: {language}
 *  - 난이도: {difficulty}
 *  - 선다형(MULTIPLE_CHOICE)과 참/거짓(TRUE_FALSE)을 혼합하여 생성하세요.
 *  - 선다형은 4개의 보기를 제공하고, 정답 번호(1~4)를 반환하세요.
 *  - T/F는 정답을 TRUE 또는 FALSE로 반환하세요.
 *  - JSON 형식으로만 응답하세요."
 *
 * [유저 프롬프트]
 * "동화 제목: {title}
 *  동화 내용:
 *  {slide 텍스트 전체}
 *  핵심 단어 목록: {highlight 단어들}"
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIQuizServiceImpl implements AIQuizService {

    @Override
    public List<QuizQuestion> generateQuestions(
            Story story,
            QuizDifficulty difficulty,
            String language,
            int count
    ) {
        log.info("퀴즈 문제 생성 요청 - storyId={}, difficulty={}, language={}, count={}",
                story.getStoryId(), difficulty, language, count);

        // 동화 내용 수집
        String storyContent = collectStoryContent(story);
        String highlightWords = collectHighlightWords(story);

        log.info("동화 내용 수집 완료 - 길이={}, 핵심단어={}", storyContent.length(), highlightWords);

        // TODO: 실제 LLM API 호출
        // String prompt = buildPrompt(story, difficulty, language, count, storyContent, highlightWords);
        // String llmResponse = llmClient.call(prompt);
        // return parseQuizResponse(llmResponse);

        // 더미 데이터 반환 (실제 연동 전까지)
        return generateDummyQuestions(story, difficulty, count);
    }

    // 동화 전체 텍스트 수집
    private String collectStoryContent(Story story) {
        StringBuilder sb = new StringBuilder();
        sb.append("제목: ").append(story.getTitle()).append("\n\n");
        for (Slide slide : story.getSlides()) {
            if (slide.getTextKr() != null) {
                sb.append("[장면 ").append(slide.getOrder()).append("]\n");
                sb.append(slide.getTextKr()).append("\n\n");
            }
        }
        return sb.toString();
    }

    // 핵심 단어(하이라이트) 수집
    private String collectHighlightWords(Story story) {
        List<String> words = new ArrayList<>();
        for (Slide slide : story.getSlides()) {
            slide.getTokens().stream()
                    .filter(token -> Boolean.TRUE.equals(token.getHighlight()))
                    .map(token -> token.getText() + "(" + token.getTranslation() + ")")
                    .forEach(words::add);
        }
        return String.join(", ", words);
    }

    // 더미 퀴즈 문제 생성 (LLM 연동 전 테스트용)
    private List<QuizQuestion> generateDummyQuestions(
            Story story, QuizDifficulty difficulty, int count
    ) {
        List<QuizQuestion> questions = new ArrayList<>();
        int vocabCount = count / 2;       // 절반은 단어 이해
        int storyCount = count - vocabCount; // 나머지는 줄거리 이해

        // 단어 이해 선다형 문제
        for (int i = 0; i < vocabCount; i++) {
            QuizQuestion q = QuizQuestion.builder()
                    .questionType(QuestionType.MULTIPLE_CHOICE)
                    .evaluationType(EvaluationType.VOCABULARY)
                    .questionOrder(i + 1)
                    .questionText("'" + story.getTitle() + "'에서 나온 단어의 뜻으로 올바른 것은? (문제 " + (i + 1) + ")")
                    .correctAnswer("1")
                    .explanation("이 단어는 동화에서 핵심적인 역할을 합니다.")
                    .build();

            q.addOption(QuizOption.builder().optionOrder(1).optionText("정답 보기").build());
            q.addOption(QuizOption.builder().optionOrder(2).optionText("오답 보기 A").build());
            q.addOption(QuizOption.builder().optionOrder(3).optionText("오답 보기 B").build());
            q.addOption(QuizOption.builder().optionOrder(4).optionText("오답 보기 C").build());

            questions.add(q);
        }

        // 줄거리 이해 T/F 문제
        for (int i = 0; i < storyCount; i++) {
            QuizQuestion q = QuizQuestion.builder()
                    .questionType(QuestionType.TRUE_FALSE)
                    .evaluationType(EvaluationType.STORY)
                    .questionOrder(vocabCount + i + 1)
                    .questionText("'" + story.getTitle() + "'에서 주인공은 모험을 떠났다. (문제 " + (vocabCount + i + 1) + ")")
                    .correctAnswer("TRUE")
                    .explanation("동화에서 주인공은 용기를 내어 모험을 떠납니다.")
                    .build();

            questions.add(q);
        }

        log.info("더미 퀴즈 문제 생성 완료 - 단어이해={}개, 줄거리이해={}개", vocabCount, storyCount);
        return questions;
    }
}
