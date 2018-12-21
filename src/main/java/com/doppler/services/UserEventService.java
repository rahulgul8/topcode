package com.doppler.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.doppler.entities.Event;
import com.doppler.entities.User;
import com.doppler.entities.UserEvent;
import com.doppler.entities.UserRewardPoint;
import com.doppler.entities.requests.UserIdRequest;
import com.doppler.repositories.EventRepository;
import com.doppler.repositories.UserEventRepository;
import com.doppler.repositories.UserRepository;
import com.doppler.repositories.UserRewardPointRepository;
import com.doppler.security.SecurityUtils;
import com.doppler.services.config.UserRewardPointDescriptionConfiguration;

/**
 * The service provides user event related operations.
 */
@Service
@Transactional
public class UserEventService extends BaseService {

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
   * The user reward point repository.
   */
  @Autowired
  private UserRewardPointRepository userRewardPointRepository;

  /**
   * The user reward point description configuration.
   */
  @Autowired
  private UserRewardPointDescriptionConfiguration userRewardPointDescriptionConfiguration;

  /**
   * Register to an event.
   * 
   * @param eventId the event id
   * @return the user reward point
   */
  public UserRewardPoint register(UUID eventId) {
    Event event = getEventById(eventId);

    // Check if already registered
    if (event.getIsMyEvent()) {
      throw new IllegalArgumentException("You already registered to this event");
    }

    User currentUser = SecurityUtils.getCurrentUser();

    // Create user event
    UserEvent userEvent = new UserEvent();
    userEvent.setEventId(eventId);
    userEvent.setUser(currentUser);
    userEventRepository.save(userEvent);

    // Add points for user
    currentUser.setPoints(currentUser.getPoints() + event.getPointsForRegistering());
    userRepository.save(currentUser);

    // Create UserRewardPoint
    UserRewardPoint userRewardPoint = new UserRewardPoint();
    userRewardPoint.setDescription(String
        .format(userRewardPointDescriptionConfiguration.getRegisterEvent(), event.getTitle()));
    userRewardPoint.setPoints(event.getPointsForRegistering());
    userRewardPoint.setUserId(currentUser.getId());

    return userRewardPointRepository.save(userRewardPoint);
  }

  /**
   * Get all attendees of an event.
   * 
   * @param eventId the event id
   * @return the attendees
   */
  @Transactional(readOnly = true)
  public List<User> getAllAttendees(UUID eventId) {
    // Make sure the event exists
    getEventById(eventId);

    List<UserEvent> userEvents = userEventRepository.findByEventId(eventId);

    return userEvents.stream().map(UserEvent::getUser).collect(Collectors.toList());
  }

  /**
   * Scan ticket.
   * 
   * @param eventId the event id
   * @param request the request
   * @return the user reward point
   */
  public UserRewardPoint scanTicket(UUID eventId, UserIdRequest request) {

    // Get the event
    Optional<Event> optionalEvent = eventRepository.findById(eventId);
    if (!optionalEvent.isPresent()) {
      throw new EntityNotFoundException("Event does not exist with id = " + eventId);
    }

    Event event = optionalEvent.get();

    // Get the user
    Optional<User> optionalUser = userRepository.findById(request.getUserId());
    if (!optionalUser.isPresent()) {
      throw new IllegalArgumentException("User does not exist");
    }

    UserEvent userEvent = userEventRepository.findByUserIdAndEventId(request.getUserId(), eventId);

    // Check if not registered
    if (userEvent == null) {
      throw new IllegalArgumentException("User has not registered to this event yet");
    }

    if (userEvent.isTicketScanned()) {
      throw new IllegalArgumentException("Ticket has been scanned already");
    }

    // Mark as scanned
    userEvent.setTicketScanned(true);
    userEventRepository.save(userEvent);

    User user = optionalUser.get();

    // Add points for user
    user.setPoints(user.getPoints() + event.getPointsForScanningTicket());
    userRepository.save(user);

    // Create UserRewardPoint
    UserRewardPoint userRewardPoint = new UserRewardPoint();
    userRewardPoint.setDescription(
        String.format(userRewardPointDescriptionConfiguration.getScanTicket(), event.getTitle()));
    userRewardPoint.setPoints(event.getPointsForScanningTicket());
    userRewardPoint.setUserId(user.getId());

    return userRewardPointRepository.save(userRewardPoint);
  }
}
