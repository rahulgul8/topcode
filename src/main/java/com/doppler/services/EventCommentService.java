package com.doppler.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.doppler.entities.EventComment;
import com.doppler.entities.EventCommentLike;
import com.doppler.entities.requests.EventCommentRequest;
import com.doppler.entities.requests.PagingAndSortingSearchRequest;
import com.doppler.entities.responses.SearchResponse;
import com.doppler.repositories.EventCommentLikeRepository;
import com.doppler.repositories.EventCommentRepository;
import com.doppler.security.SecurityUtils;

/**
 * The service provides event comment related operations.
 */
@Service
@Transactional
public class EventCommentService extends BaseService {

  /**
   * The event comment repository.
   */
  @Autowired
  private EventCommentRepository eventCommentRepository;

  /**
   * The event comment like repository.
   */
  @Autowired
  private EventCommentLikeRepository eventCommentLikeRepository;

  /**
   * Search events.
   * 
   * @param eventId the event id.
   * @param criteria the search criteria
   * @return the search result
   */
  @Transactional(readOnly = true)
  public SearchResponse<EventComment> search(UUID eventId, PagingAndSortingSearchRequest criteria) {
    // Make sure the event exists
    getEventById(eventId);

    // Get the root comments
    Pageable pageable =
        createPageRequest(criteria, Arrays.asList("createdAt", "likeCount"), "createdAt", "desc");
    Page<EventComment> page =
        eventCommentRepository.findByEventIdAndParentCommentIdIsNull(eventId, pageable);

    List<EventComment> comments = page.getContent();

    // Populate
    populateComments(comments);

    SearchResponse<EventComment> searchResponse = new SearchResponse<>();
    searchResponse.setCount(page.getTotalElements());
    searchResponse.setRows(comments);

    return searchResponse;
  }

  /**
   * Create a new event comment.
   * 
   * @param eventId the event id
   * @param request the request
   * @return the created event comment
   */
  public EventComment create(UUID eventId, EventCommentRequest request) {
    // Make sure the event exists
    getEventById(eventId);

    // Validate parent comment
    validateParentComment(eventId, request);

    // Create
    EventComment eventComment = new EventComment();
    eventComment.setContent(request.getContent());
    eventComment.setEventId(eventId);
    eventComment.setParentCommentId(request.getParentCommentId());
    eventComment.setUser(SecurityUtils.getCurrentUser());

    eventComment = eventCommentRepository.save(eventComment);

    // Populate
    populateComments(Arrays.asList(eventComment));

    return eventComment;
  }

  /**
   * Update an existing event comment.
   * 
   * @param eventId the event id
   * @param commentId the comment id
   * @param request the request
   * @return the created event comment
   */
  public EventComment update(UUID eventId, UUID commentId, EventCommentRequest request) {
    // Make sure the event exists
    getEventById(eventId);

    // Get
    EventComment eventComment = getComment(eventId, commentId, true);

    // Validate parent comment
    validateParentComment(eventId, request);

    // Make sure to not have itself as parent
    if (commentId.equals(request.getParentCommentId())) {
      throw new IllegalArgumentException("Comment cannot be sub-comment of itself");
    }

    // Make sure to have not more than 2 levels
    if (request.getParentCommentId() != null
        && eventCommentRepository.existsByParentCommentId(commentId)) {
      throw new IllegalArgumentException("Comments support only 2 levels");
    }

    // Update
    BeanUtils.copyProperties(request, eventComment);
    eventComment = eventCommentRepository.save(eventComment);

    // Populate
    populateComments(Arrays.asList(eventComment));

    return eventComment;
  }

  /**
   * Delete an existing event comment.
   * 
   * @param eventId the event id
   * @param commentId the comment id
   * @return the deleted comment
   */
  public EventComment delete(UUID eventId, UUID commentId) {
    // Make sure the event exists
    getEventById(eventId);

    // Get
    EventComment eventComment = getComment(eventId, commentId, true);

    // Soft delete
    eventComment.setDeleted(true);
    eventComment.setContent("This comment has been deleted");
    eventComment = eventCommentRepository.save(eventComment);

    // Populate
    populateComments(Arrays.asList(eventComment));

    return eventComment;
  }

  /**
   * Like an existing event comment.
   * 
   * @param eventId the event id
   * @param commentId the comment id
   * @return the updated event comment
   */
  public EventComment like(UUID eventId, UUID commentId) {
    // Make sure the event exists
    getEventById(eventId);

    // Get comment
    EventComment eventComment = getComment(eventId, commentId, false);

    // Not allowed to like user's own comment
    if (eventComment.getUser().getId().equals(SecurityUtils.getCurrentUser().getId())) {
      throw new AccessDeniedException("You are not allowed to like your own comment");
    }

    // Make sure not liked yet
    boolean liked = eventCommentLikeRepository.existsByEventCommentIdAndUserId(commentId,
        SecurityUtils.getCurrentUser().getId());
    if (liked) {
      throw new IllegalArgumentException("You already liked this comment");
    }

    // Create the like
    EventCommentLike eventCommentLike = new EventCommentLike();
    eventCommentLike.setEventCommentId(commentId);
    eventCommentLike.setUserId(SecurityUtils.getCurrentUser().getId());
    eventCommentLikeRepository.save(eventCommentLike);

    // Increase the like count
    eventComment.setLikeCount(eventComment.getLikeCount() + 1);
    eventComment = eventCommentRepository.save(eventComment);

    // Populate comment
    populateComments(Arrays.asList(eventComment));

    return eventComment;
  }

  /**
   * Un-like an existing event comment.
   * 
   * @param eventId the event id
   * @param commentId the comment id
   * @return the updated event comment
   */
  public EventComment unlike(UUID eventId, UUID commentId) {
    // Make sure the event exists
    getEventById(eventId);

    // Get comment
    EventComment eventComment = getComment(eventId, commentId, false);

    // Make sure liked already
    boolean liked = eventCommentLikeRepository.existsByEventCommentIdAndUserId(commentId,
        SecurityUtils.getCurrentUser().getId());
    if (!liked) {
      throw new EntityNotFoundException("You have not liked this comment yet");
    }

    // Delete the like
    eventCommentLikeRepository.deleteByEventCommentIdAndUserId(commentId,
        SecurityUtils.getCurrentUser().getId());

    // Decrease the like count
    eventComment.setLikeCount(eventComment.getLikeCount() - 1);
    eventComment = eventCommentRepository.save(eventComment);

    // Populate comment
    populateComments(Arrays.asList(eventComment));

    return eventComment;
  }

  /**
   * Validate the parent comment.
   * 
   * @param eventId the event id
   * @param request the request
   */
  private void validateParentComment(UUID eventId, EventCommentRequest request) {
    if (request.getParentCommentId() != null) {
      Optional<EventComment> parentComment =
          eventCommentRepository.findById(request.getParentCommentId());

      if (parentComment.isPresent()) {
        if (parentComment.get().getParentCommentId() != null) {
          throw new IllegalArgumentException("Comments support only 2 levels");
        }

        if (!parentComment.get().getEventId().equals(eventId)) {
          throw new IllegalArgumentException(
              "The parent comment does not belong to the specified event");
        }
      } else {
        throw new IllegalArgumentException("parentCommentId does not exist");
      }
    }
  }

  /**
   * Get the comment by event id and comment id.
   * 
   * @param eventId the event id
   * @param commentId the event comment id
   * @param updateOrDeleteComment true if updating or deleting comment
   * @return the event comment
   */
  private EventComment getComment(UUID eventId, UUID commentId, boolean updateOrDeleteComment) {
    // Get the comment
    Optional<EventComment> optionalEventComment = eventCommentRepository.findById(commentId);
    if (!optionalEventComment.isPresent()) {
      throw new EntityNotFoundException("Event Comment does not exist with id = " + commentId);
    }

    // Check event id
    EventComment eventComment = optionalEventComment.get();
    if (!eventComment.getEventId().equals(eventId)) {
      throw new IllegalArgumentException("The comment does not belong to the specified event");
    }

    // Check if deleted
    if (eventComment.isDeleted()) {
      throw new AccessDeniedException("The comment has been deleted already");
    }

    // Check owner
    if (updateOrDeleteComment
        && !eventComment.getUser().getId().equals(SecurityUtils.getCurrentUser().getId())) {
      throw new AccessDeniedException(
          "You are not allowed to update/delete the other user's comment");
    }

    return eventComment;
  }

  /**
   * Populate child comments and flags.
   * 
   * @param comments the root comment
   */
  private void populateComments(List<EventComment> comments) {
    List<UUID> commentIds = comments.stream().map(EventComment::getId).collect(Collectors.toList());

    // Get the child comments
    List<EventComment> childComments = eventCommentRepository.findByParentCommentIdIn(commentIds);

    // Map child comments to the parent comments
    comments.forEach(comment -> {
      comment.setChildComments(childComments.stream().filter(childComment -> {
        return childComment.getParentCommentId().equals(comment.getId());
      }).collect(Collectors.toList()));
    });

    // Calculate like by me for each comment
    childComments.forEach(childComment -> {
      commentIds.add(childComment.getId());
    });
    List<EventCommentLike> likes = eventCommentLikeRepository
        .findByEventCommentIdInAndUserId(commentIds, SecurityUtils.getCurrentUser().getId());

    comments.forEach(comment -> {
      boolean likedByMe =
          likes.stream().anyMatch(like -> like.getEventCommentId().equals(comment.getId()));
      comment.setLikedByMe(likedByMe);

      comment.getChildComments().forEach(childComment -> {
        boolean childLikedByMe =
            likes.stream().anyMatch(like -> like.getEventCommentId().equals(childComment.getId()));
        childComment.setLikedByMe(childLikedByMe);
      });
    });
  }
}
