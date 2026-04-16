package com.moretale.domain.quiz.dto;

import com.moretale.domain.quiz.entity.QuizAnswerRecord;
import com.moretale.domain.quiz.entity.QuizResult;
import lombok.*;

import java.util.List;

/**
 * POST /api/quiz/submit 응답 DTO
 * - 채점 결과 + 꿀단지 보상 정보 포함
 * - 프론트에서 결과 화면 렌더링에 필요한 모든 정보 제공
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultResponse {

    // 채점 결과
    private Long resultId;
    private Integer score;           // 점수 (0~100)
    private Integer totalQuestions;
    private Integer correctCount;
    private Boolean isPerfect;       // 100점 여부

    // 꿀단지 보상 정보
    private HoneyJarRewardInfo honeyJarReward;

    // 문항별 정오 내역
    private List<AnswerResultDto> answerResults;

    // 프론트에서 사용할 상태 메시지
    private String resultMessage;

    public static QuizResultResponse of(
            QuizResult result,
            HoneyJarRewardInfo rewardInfo,
            List<AnswerResultDto> answerResults
    ) {
        String message = buildResultMessage(result.getScore(), result.getIsPerfect());

        return QuizResultResponse.builder()
                .resultId(result.getResultId())
                .score(result.getScore())
                .totalQuestions(result.getTotalQuestions())
                .correctCount(result.getCorrectCount())
                .isPerfect(result.getIsPerfect())
                .honeyJarReward(rewardInfo)
                .answerResults(answerResults)
                .resultMessage(message)
                .build();
    }

    private static String buildResultMessage(int score, boolean isPerfect) {
        if (isPerfect) return "🎉 완벽해요! 모든 문제를 맞혔어요!";
        if (score >= 80) return "👏 훌륭해요! 거의 다 맞혔어요!";
        if (score >= 60) return "😊 잘했어요! 조금만 더 읽어봐요!";
        return "📚 동화를 다시 읽고 도전해봐요!";
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HoneyJarRewardInfo {
        private Integer earnedHoneyJars;        // 이번에 획득한 꿀단지 수
        private Integer currentHoneyJarCount;   // 현재 보유 꿀단지 수
        private Boolean canGenerateFree;        // 무료 생성 가능 여부 (20개 이상)
        private Boolean autoUsedForFreeGeneration; // 20개 달성 시 자동 차감 여부
        private String rewardMessage;           // 보상 관련 메시지
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerResultDto {
        private Long questionId;
        private Integer questionOrder;
        private String questionText;
        private String submittedAnswer;
        private String correctAnswer;
        private Boolean isCorrect;
        private String explanation;

        public static AnswerResultDto from(QuizAnswerRecord record) {
            return AnswerResultDto.builder()
                    .questionId(record.getQuestion().getQuestionId())
                    .questionOrder(record.getQuestion().getQuestionOrder())
                    .questionText(record.getQuestion().getQuestionText())
                    .submittedAnswer(record.getSubmittedAnswer())
                    .correctAnswer(record.getQuestion().getCorrectAnswer())
                    .isCorrect(record.getIsCorrect())
                    .explanation(record.getQuestion().getExplanation())
                    .build();
        }
    }
}
