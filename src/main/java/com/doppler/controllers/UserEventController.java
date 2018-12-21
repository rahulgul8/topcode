package com.doppler.controllers;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.doppler.entities.User;
import com.doppler.entities.UserRewardPoint;
import com.doppler.entities.requests.UserIdRequest;
import com.doppler.services.UserEventService;

/**
 * The controller defines user event related endpoints.
 */
@RestController
@RequestMapping("/events/{eventId}")
public class UserEventController {

  /**
   * The user event service.
   */
  @Autowired
  private UserEventService service;

  /**
   * Register to an event.
   * 
   * @param eventId the event id
   * @return the user reward point
   */
  @PostMapping("/register")
  public UserRewardPoint register(@PathVariable("eventId") UUID eventId) {
    return service.register(eventId);
  }

  /**
   * Get all attendees of an event.
   * 
   * @param eventId the event id
   * @return the attendees
   */
  @GetMapping("/attendees")
  public List<User> getAllAttendees(@PathVariable("eventId") UUID eventId) {
    return service.getAllAttendees(eventId);
  }


  /**
   * Scan ticket.
   * 
   * @param eventId the event id
   * @param request the request
   * @return the user reward point
   */
  @PostMapping("/scanTicket")
  public UserRewardPoint scanTicket(@PathVariable("eventId") UUID eventId,
      @Valid @RequestBody UserIdRequest request) {
    return service.scanTicket(eventId, request);
  }
}
