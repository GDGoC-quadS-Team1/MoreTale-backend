package com.moretale.domain.vocabulary.repository;

import com.moretale.domain.story.entity.Story;
import com.moretale.domain.vocabulary.entity.VocabularyEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VocabularyEntryRepository extends JpaRepository<VocabularyEntry, Long> {

    // 내 전체 단어장 조회 (페이징)
    Page<VocabularyEntry> findByUser_UserId(Long userId, Pageable pageable);

    // 특정 동화 기준 단어장 조회 (페이징)
    Page<VocabularyEntry> findByUser_UserIdAndStory_StoryId(Long userId, Long storyId, Pageable pageable);

    // 단어가 저장된 동화 목록 조회 (중복 제거)
    @Query("""
        SELECT DISTINCT v.story
        FROM VocabularyEntry v
        WHERE v.user.userId = :userId
        ORDER BY v.story.createdAt DESC
        """)
    List<Story> findDistinctStoriesByUserId(@Param("userId") Long userId);

    // 단일 항목 조회 (소유권 확인용)
    Optional<VocabularyEntry> findByVocabularyIdAndUser_UserId(Long vocabularyId, Long userId);

    // 중복 저장 여부 확인 (같은 사용자 + 같은 동화 + 같은 정규화 단어)
    boolean existsByUser_UserIdAndStory_StoryIdAndNormalizedWord(
            Long userId, Long storyId, String normalizedWord
    );

    // 특정 동화의 단어 수 조회
    long countByUser_UserIdAndStory_StoryId(Long userId, Long storyId);

    // Story 삭제 시 연관된 단어장 전체 삭제용
    void deleteAllByStory(Story story);
}
