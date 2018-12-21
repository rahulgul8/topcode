package com.doppler.services;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.doppler.entities.Notification;
import com.doppler.entities.requests.PagingAndSortingSearchRequest;
import com.doppler.entities.responses.SearchResponse;
import com.doppler.repositories.NotificationRepository;
import com.doppler.security.SecurityUtils;

/**
 * The service provides notification related operations.
 */
@Service
@Transactional
public class NotificationService extends BaseService {

  /**
   * The notification repository.
   */
  @Autowired
  private NotificationRepository notificationRepository;

  /**
   * Search notifications of the current user.
   * 
   * @param criteria the search criteria
   * @return the search result
   */
  @Transactional(readOnly = true)
  public SearchResponse<Notification> search(PagingAndSortingSearchRequest criteria) {
    // Sort by createdAt descending
    criteria.setSortBy("createdAt");
    criteria.setSortDirection("desc");
    Pageable pageable =
        createPageRequest(criteria, Arrays.asList("createdAt"), "createdAt", "desc");

    SearchResponse<Notification> searchResponse = new SearchResponse<>();
    Page<Notification> page =
        notificationRepository.findByUserId(SecurityUtils.getCurrentUser().getId(), pageable);

    searchResponse.setCount(page.getTotalElements());
    searchResponse.setRows(page.getContent());

    return searchResponse;
  }

  /**
   * Mark a notification as read.
   * 
   * @param notificationId the notification id
   * @return the updated notification
   */
  public Notification markAsRead(UUID notificationId) {

    // Validate
    Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
    if (!optionalNotification.isPresent()) {
      throw new EntityNotFoundException("Notification does not exist with id = " + notificationId);
    }

    Notification notification = optionalNotification.get();

    // Check if it's another user's notification
    if (!notification.getUserId().equals(SecurityUtils.getCurrentUser().getId())) {
      throw new AccessDeniedException("You are not allowed to access other user's notification");
    }

    // Mark as read
    notification.setRead(true);

    return notificationRepository.save(notification);
  }
}
