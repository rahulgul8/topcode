package com.doppler.controllers;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.doppler.entities.Event;
import com.doppler.entities.requests.EventSearchRequest;
import com.doppler.entities.requests.UserIdRequest;
import com.doppler.entities.responses.SearchResponse;
import com.doppler.services.EventService;

/**
 * The controller defines event related endpoints.
 */
@RestController
@RequestMapping("/events")
public class EventController {

  /**
   * The event service.
   */
  @Autowired
  private EventService service;

  /**
   * Search events.
   *
   * @param criteria the search criteria
   * @return the search result
   */
  @GetMapping
  public SearchResponse<Event> search(@Valid @ModelAttribute EventSearchRequest criteria) {
    return service.search(criteria);
  }

  /**
   * Get an event by id.
   * 
   * @param eventId the event id
   * @return the event
   */
  @GetMapping("/{eventId}")
  public Event get(@PathVariable("eventId") UUID eventId) {
    return service.get(eventId);
  }

  /**
   * Share an event to other users.
   * 
   * @param request the request
   */
  @PostMapping("/{eventId}/share")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void share(@PathVariable("eventId") UUID eventId,
      @Valid @RequestBody List<UserIdRequest> request) {
    service.share(eventId, request);
  }
}
