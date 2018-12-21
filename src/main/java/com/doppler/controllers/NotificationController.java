package com.doppler.controllers;

import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.doppler.entities.Notification;
import com.doppler.entities.requests.PagingAndSortingSearchRequest;
import com.doppler.entities.responses.SearchResponse;
import com.doppler.services.NotificationService;

/**
 * The controller defines notification related endpoints.
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {

  /**
   * The notification service.
   */
  @Autowired
  private NotificationService service;

  /**
   * Search notifications of the current user.
   * 
   * @param criteria the search criteria
   * @return the search result
   */
  @GetMapping
  public SearchResponse<Notification> search(
      @Valid @ModelAttribute PagingAndSortingSearchRequest criteria) {
    return service.search(criteria);
  }

  /**
   * Mark a notification as read.
   * 
   * @param notificationId the notification id
   * @return the updated notification
   */
  @PostMapping("/{notificationId}/read")
  public Notification markAsRead(@PathVariable("notificationId") UUID notificationId) {
    return service.markAsRead(notificationId);
  }
}
