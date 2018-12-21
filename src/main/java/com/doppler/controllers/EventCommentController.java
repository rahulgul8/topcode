package com.doppler.controllers;

import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.doppler.entities.EventComment;
import com.doppler.entities.requests.EventCommentRequest;
import com.doppler.entities.requests.PagingAndSortingSearchRequest;
import com.doppler.entities.responses.SearchResponse;
import com.doppler.services.EventCommentService;

/**
 * The controller defines event comment related endpoints.
 */
@RestController
@RequestMapping("/events/{eventId}/comments")
public class EventCommentController {

  /**
   * The event comment service.
   */
  @Autowired
  private EventCommentService service;

  /**
   * Search event comments.
   *
   * @param criteria the search criteria
   * @return the search result
   */
  @GetMapping
  public SearchResponse<EventComment> search(@PathVariable("eventId") UUID eventId,
      @Valid @ModelAttribute PagingAndSortingSearchRequest criteria) {
    return service.search(eventId, criteria);
  }

  /**
   * Create a new event comment.
   * 
   * @param eventId the event id
   * @param request the request
   * @return the created event comment
   */
  @PostMapping
  public EventComment create(@PathVariable("eventId") UUID eventId,
      @Valid @RequestBody EventCommentRequest request) {
    return service.create(eventId, request);
  }

  /**
   * Update an existing event comment.
   * 
   * @param eventId the event id
   * @param commentId the comment id
   * @param request the request
   * @return the updated event comment
   */
  @PutMapping("/{commentId}")
  public EventComment update(@PathVariable("eventId") UUID eventId,
      @PathVariable("commentId") UUID commentId, @Valid @RequestBody EventCommentRequest request) {
    return service.update(eventId, commentId, request);
  }

  /**
   * Delete an existing event comment.
   * 
   * @param eventId the event id
   * @param commentId the comment id
   * @return the deleted comment
   */
  @DeleteMapping("/{commentId}")
  public EventComment delete(@PathVariable("eventId") UUID eventId,
      @PathVariable("commentId") UUID commentId) {
    return service.delete(eventId, commentId);
  }

  /**
   * Like an existing event comment.
   * 
   * @param eventId the event id
   * @param commentId the comment id
   * @return the updated event comment
   */
  @PostMapping("/{commentId}/like")
  public EventComment like(@PathVariable("eventId") UUID eventId,
      @PathVariable("commentId") UUID commentId) {
    return service.like(eventId, commentId);
  }

  /**
   * Un-like an existing event comment.
   * 
   * @param eventId the event id
   * @param commentId the comment id
   * @return the updated event comment
   */
  @PostMapping("/{commentId}/unlike")
  public EventComment unlike(@PathVariable("eventId") UUID eventId,
      @PathVariable("commentId") UUID commentId) {
    return service.unlike(eventId, commentId);
  }
}
