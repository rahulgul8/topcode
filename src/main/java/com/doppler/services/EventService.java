package com.doppler.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import com.doppler.entities.Event;
import com.doppler.entities.Notification;
import com.doppler.entities.User;
import com.doppler.entities.requests.EventSearchRequest;
import com.doppler.entities.requests.UserIdRequest;
import com.doppler.entities.responses.SearchResponse;
import com.doppler.repositories.EventRepository;
import com.doppler.repositories.NotificationRepository;
import com.doppler.repositories.UserEventRepository;
import com.doppler.repositories.UserRepository;
import com.doppler.security.SecurityUtils;
import com.doppler.services.config.NotificationContentConfiguration;
import com.doppler.util.ApiConstants;

/**
 * The service provides event related operations.
 */
@Service
@Transactional
public class EventService extends BaseService {
	
	 
	  private static final String EVENTS_URL = "/events";

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

  @Autowired
  private BackendAPIService apiService;
  
 
  
  
	/**
	 * Search events.
	 * 
	 * @param criteria
	 *            the search criteria
	 * @return the search result
	 */
	@Transactional(readOnly = true)
	public SearchResponse<Event> search(EventSearchRequest criteria) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ApiConstants.BASE_URI + EVENTS_URL)
				.queryParam("limit", criteria.getLimit()).queryParam("offset", criteria.getOffset())
				.queryParam("sortBy", criteria.getSortBy()).queryParam("sortDirection", criteria.getSortDirection())
				.queryParam("eventStatus", criteria.getStatus()).queryParam("topicId", criteria.getTopicId());

		SearchResponse<Event> events = apiService.getForEntity(SearchResponse.class, builder.toUriString());
		return events;
	}

	/**
	 * Get an event by id.
	 * 
	 * @param id
	 *            the id
	 * @return the event
	 */
	@Transactional(readOnly = true)
	public Event get(UUID id) {
		Event event = apiService.getForEntity(Event.class, ApiConstants.BASE_URI + EVENTS_URL + "/{id}", id);
		return event;
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
