package com.doppler.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import com.doppler.entities.EventCommentLike;

/**
 * The repository provides operations on Event Comment Like entity.
 */
@Repository
public interface EventCommentLikeRepository extends BaseRepository<EventCommentLike> {

  /**
   * Count the likes of a specified event comment.
   * 
   * @param eventCommentId the event comment id
   * @return the number of likes
   */
  long countByEventCommentId(UUID eventCommentId);

  /**
   * Check if a specified event comment is liked by a specified user.
   * 
   * @param eventCommentId the event comment id
   * @param userId the user id
   * @return true if the user has liked the comment, otherwise false
   */
  boolean existsByEventCommentIdAndUserId(UUID eventCommentId, UUID userId);

  /**
   * Unlike a comment.
   * 
   * @param eventCommentId the event comment id
   * @param userId the user id
   */
  void deleteByEventCommentIdAndUserId(UUID eventCommentId, UUID userId);

  /**
   * Find by the likes of specified comments and specified user.
   * 
   * @param eventCommentIds the event comment id
   * @param userId the user id
   * @return the likes
   */
  List<EventCommentLike> findByEventCommentIdInAndUserId(List<UUID> eventCommentIds, UUID userId);
}
