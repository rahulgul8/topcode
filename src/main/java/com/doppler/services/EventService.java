package com.doppler.services;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.doppler.entities.Event;
import com.doppler.entities.Notification;
import com.doppler.entities.User;
import com.doppler.entities.UserEvent;
import com.doppler.entities.requests.EventSearchRequest;
import com.doppler.entities.requests.EventStatus;
import com.doppler.entities.requests.UserIdRequest;
import com.doppler.entities.responses.SearchResponse;
import com.doppler.repositories.EventRepository;
import com.doppler.repositories.NotificationRepository;
import com.doppler.repositories.UserEventRepository;
import com.doppler.repositories.UserRepository;
import com.doppler.security.SecurityUtils;
import com.doppler.services.config.NotificationContentConfiguration;

/**
 * The service provides event related operations.
 */
@Service
@Transactional
public class EventService extends BaseService {

  /**
   * The event repository.
   */
  @Autowired
  private EventRepository eventRepository;

  /**
   * The user event repository.
   */
  @Autowired
  private UserEventRepository userEventRepository;

  /**
   * The user repository.
   */
  @Autowired
  private UserRepository userRepository;
  /**
   * The notification repository.
   */
  @Autowired
  private NotificationRepository notificationRepository;

  /**
   * The notification content configuration.
   */
  @Autowired
  private NotificationContentConfiguration notificationContentConfiguration;

  /**
   * Search events.
   * 
   * @param criteria the search criteria
   * @return the search result
   */
  @Transactional(readOnly = true)
  public SearchResponse<Event> search(EventSearchRequest criteria) {
    Pageable pageable =
        createPageRequest(criteria, Arrays.asList("firstSessionStart"), "firstSessionStart", "asc");

    // Upcoming or past events
    Date lastSessionEndFrom = null;
    Date lastSessionEndTo = null;

    if (criteria.getStatus() == EventStatus.UPCOMING) {
      lastSessionEndFrom = new Date();
    } else if (criteria.getStatus() == EventStatus.PAST) {
      lastSessionEndTo = new Date();
    }

    // Get all user events
    UUID userId = SecurityUtils.getCurrentUser().getId();
    List<UserEvent> userEvents = userEventRepository.findByUserId(userId);
    List<UUID> myEventIds =
        userEvents.stream().map(UserEvent::getEventId).collect(Collectors.toList());

    SearchResponse<Event> searchResponse = new SearchResponse<>();
    Page<Event> page = null;

    // Filter only my events
    if (criteria.isOnlyMyEvents()) {

      // No registered events
      if (CollectionUtils.isEmpty(myEventIds)) {
        return searchResponse;
      }

      page = eventRepository.search(uuidToString(criteria.getTopicId()), lastSessionEndFrom,
          lastSessionEndTo, myEventIds, pageable);
    } else {
      // Or filter all events
      page = eventRepository.search(uuidToString(criteria.getTopicId()), lastSessionEndFrom,
          lastSessionEndTo, pageable);
    }

    // Set isMyEvent flag
    page.getContent().forEach(x -> {
      x.setIsMyEvent(myEventIds.contains(x.getId()));
    });

    searchResponse.setCount(page.getTotalElements());
    searchResponse.setRows(page.getContent());

    return searchResponse;
  }

  /**
   * Get an event by id.
   * 
   * @param id the id
   * @return the event
   */
  @Transactional(readOnly = true)
  public Event get(UUID id) {
    return getEventById(id);
  }

  /**
   * Share event to other users.
   * 
   * @param eventId the event id
   * @param request the request
   */
  public void share(UUID eventId, List<UserIdRequest> request) {
    // Validate
    validateList(request, "request");
    List<UUID> userIds =
        request.stream().map(UserIdRequest::getUserId).collect(Collectors.toList());
    validateList(userIds, "request[].userId");

    // Get the event
    Event event = get(eventId);

    // Get the users
    List<User> users = userRepository.findByIdIn(userIds);

    User currentUser = SecurityUtils.getCurrentUser();

    // Build the notifications
    List<Notification> notifications = users.stream().map(user -> {
      Notification notification = new Notification();
      notification.setContent(String.format(notificationContentConfiguration.getShareEvent(),
          currentUser.getFullName(), event.getTitle()));
      notification.setIconUrl(currentUser.getPhotoUrl());
      notification.setRelatedObjectType(Event.class.getSimpleName());
      notification.setRelatedObjectId(event.getId());
      notification.setUserId(user.getId());

      return notification;
    }).collect(Collectors.toList());

    notificationRepository.saveAll(notifications);
  }
}
