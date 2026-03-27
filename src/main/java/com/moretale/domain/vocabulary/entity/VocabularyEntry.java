package com.moretale.domain.vocabulary.entity;

import com.moretale.domain.story.entity.Slide;
import com.moretale.domain.story.entity.Story;
import com.moretale.domain.story.entity.StoryToken;
import com.moretale.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "vocabulary_entries",
        indexes = {
                @Index(name = "idx_vocab_user_id", columnList = "user_id"),
                @Index(name = "idx_vocab_story_id", columnList = "story_id"),
                @Index(name = "idx_vocab_user_story", columnList = "user_id, story_id")
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VocabularyEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vocabulary_id")
    private Long vocabularyId;

    // 저장한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 단어가 속한 동화 (출처 동화) - 동화 삭제 시 관련 단어 자동 삭제
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Story story;

    // 단어가 속한 슬라이드 (출처 슬라이드) - 슬라이드 삭제 시 관련 단어 자동 삭제
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slide_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Slide slide;

    // 저장된 StoryToken (원본 토큰 참조) - 토큰 삭제 시 관련 단어 자동 삭제
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private StoryToken storyToken;

    // 단어 원문 (예: "사자를")
    @Column(name = "word", nullable = false, length = 100)
    private String word;

    // 정규화된 단어 (예: "사자") - 중복 판단 기준
    @Column(name = "normalized_word", nullable = false, length = 100)
    private String normalizedWord;

    // 번역어
    @Column(name = "translation", length = 200)
    private String translation;

    // 단어 뜻 설명
    @Column(name = "definition", columnDefinition = "TEXT")
    private String definition;

    // 원문 언어 (예: "ko")
    @Column(name = "source_language", length = 10)
    private String sourceLanguage;

    // 번역 대상 언어 (예: "vi")
    @Column(name = "target_language", length = 10)
    private String targetLanguage;

    // 단어 발음 오디오 URL
    @Column(name = "audio_url", length = 500)
    private String audioUrl;

    // 저장 일시
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ── 향후 확장 필드 (현재는 미사용, 스키마에만 준비) ──────────────────────

    // 즐겨찾기 여부
    @Column(name = "is_favorite", nullable = false)
    @Builder.Default
    private Boolean isFavorite = false;

    // 학습 상태 (UNSEEN / LEARNING / MASTERED)
    @Enumerated(EnumType.STRING)
    @Column(name = "learning_status", length = 20)
    @Builder.Default
    private LearningStatus learningStatus = LearningStatus.UNSEEN;

    // 사용자 메모
    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    // 마지막 복습 일시
    @Column(name = "last_reviewed_at")
    private LocalDateTime lastReviewedAt;

    // ── 편의 메서드 ────────────────────────────────────────────────────────────

    public void updateFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public void updateLearningStatus(LearningStatus learningStatus) {
        this.learningStatus = learningStatus;
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }

    public void updateLastReviewedAt(LocalDateTime lastReviewedAt) {
        this.lastReviewedAt = lastReviewedAt;
    }

    // 학습 상태 enum
    public enum LearningStatus {
        UNSEEN,    // 아직 학습 안 함
        LEARNING,  // 학습 중
        MASTERED   // 완전히 익힘
    }
}
