package com.moretale.domain.vocabulary.dto.response;

import com.moretale.domain.story.entity.Story;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// 단어가 저장된 동화 목록 조회용 DTO
@Getter
@Builder
public class VocabularyStoryResponse {

    private Long storyId;
    private String title;
    private String primaryLanguage;
    private String secondaryLanguage;
    private LocalDateTime createdAt;   // 도서관 카드 생성일자 표시용
    private long wordCount;            // 해당 동화에서 저장한 단어 수

    public static VocabularyStoryResponse from(Story story, long wordCount) {
        return VocabularyStoryResponse.builder()
                .storyId(story.getStoryId())
                .title(story.getTitle())
                .primaryLanguage(story.getPrimaryLanguage())
                .secondaryLanguage(story.getSecondaryLanguage())
                .createdAt(story.getCreatedAt())
                .wordCount(wordCount)
                .build();
    }
}
