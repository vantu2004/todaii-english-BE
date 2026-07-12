package com.todaii.english.client.video;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.DictionaryWord;
import com.todaii.english.core.entity.video.Video;
import com.todaii.english.shared.enums.CefrLevel;

@Repository
public interface VideoRepository
    extends JpaRepository<Video, Long>, JpaSpecificationExecutor<Video> {
  public Page<Video> findAllByEnabledTrueOrderByCreatedAtDesc(Pageable pageable);

  public Page<Video> findAllByEnabledTrueOrderByViewsDesc(Pageable pageable);

  @Query(
      """
			    SELECT DISTINCT v
			    FROM Video v
			    JOIN v.topics t
			    WHERE v.enabled = true
			      AND v.id <> ?1
			      AND t.id IN ?2
			""")
  public List<Video> findRelatedByTopics(Long videoId, List<Long> topicIds);

  @Query(
      """
			    SELECT v
			    FROM Video v
			    WHERE v.enabled = true
			      AND v.id <> ?1
			      AND v.cefrLevel = ?2
			""")
  public List<Video> findFallbackByCefr(Long videoId, CefrLevel level, Pageable pageable);

  @Query(
      """
			    SELECT v FROM Video v
			    WHERE v.enabled = true
			      AND v.createdAt BETWEEN ?1 AND ?2
			""")
  public Page<Video> findByCreatedDateRange(
      LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);

  @Query(
      """
			    SELECT d FROM Video v
			    JOIN v.words d
			    WHERE v.id = ?1
			    ORDER BY d.word ASC
			""")
  public Page<DictionaryWord> findPagedWordsByVideoId(Long id, Pageable pageable);

  // Recommend: Lấy ngẫu nhiên N video theo CEFR level
  @Query(
      value =
          "SELECT * FROM videos v WHERE v.enabled = true AND v.cefr_level = :cefrLevel ORDER BY"
              + " RAND() LIMIT :limit",
      nativeQuery = true)
  List<Video> findRandomByCefrLevel(
      @Param("cefrLevel") String cefrLevel, @Param("limit") int limit);

  // Recommend fallback: Lấy ngẫu nhiên N video bất kỳ
  @Query(
      value = "SELECT * FROM videos v WHERE v.enabled = true ORDER BY RAND() LIMIT :limit",
      nativeQuery = true)
  List<Video> findRandomVideos(@Param("limit") int limit);

  // Recommend: Lấy ngẫu nhiên N video theo CEFR, loại bỏ video đã completed bởi user
  @Query(
      value =
          "SELECT v.* FROM videos v WHERE v.enabled = true AND v.cefr_level = :cefrLevel"
              + " AND v.id NOT IN (SELECT cp.content_id FROM content_progress cp"
              + " WHERE cp.user_id = :userId AND cp.content_type = 'VIDEO'"
              + " AND cp.completed = true)"
              + " ORDER BY RAND() LIMIT :limit",
      nativeQuery = true)
  List<Video> findRandomByCefrLevelExcludeCompleted(
      @Param("userId") Long userId,
      @Param("cefrLevel") String cefrLevel,
      @Param("limit") int limit);

  // Lấy videos user đang xem dở (có progress, chưa completed)
  @Query(
      value =
          "SELECT v.* FROM videos v INNER JOIN content_progress cp"
              + " ON v.id = cp.content_id AND cp.content_type = 'VIDEO'"
              + " WHERE v.enabled = true AND cp.user_id = :userId AND cp.completed = false"
              + " ORDER BY cp.updated_at DESC LIMIT :limit",
      nativeQuery = true)
  List<Video> findInProgressByUser(@Param("userId") Long userId, @Param("limit") int limit);
}
