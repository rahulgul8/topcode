package com.doppler.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import com.doppler.entities.EventComment;

/**
 * The repository provides operations on Event Comment entity.
 */
@Repository
public interface EventCommentRepository extends BaseRepository<EventComment> {

  /**
   * Find the comments of a specified event.
   * 
   * @param eventId the event id
   * @param pageable the paging criteria
   * @return the comments
   */
  Page<EventComment> findByEventId(UUID eventId, Pageable pageable);

  /**
   * Find the root comments of a specified event.
   * 
   * @param eventId the event id
   * @param pageable the paging criteria
   * @return the comments
   */
  Page<EventComment> findByEventIdAndParentCommentIdIsNull(UUID eventId, Pageable pageable);

  /**
   * Find child comments by parent comment ids.
   * 
   * @param parentCommentIds the parent comment ids
   * @return the child comments
   */
  List<EventComment> findByParentCommentIdIn(List<UUID> parentCommentIds);

  /**
   * Check if the specified parent comment has any child comment.
   * 
   * @param parentCommentId the parent comment id
   * @return true if exists, otherwise false
   */
  boolean existsByParentCommentId(UUID parentCommentId);
}
