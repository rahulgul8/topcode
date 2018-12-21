package com.doppler.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import com.doppler.entities.UserEvent;

/**
 * The repository provides operations on User Event entity.
 */
@Repository
public interface UserEventRepository extends BaseRepository<UserEvent> {

  /**
   * Find the event registrations of a specified user.
   * 
   * @param userId the user id
   * @return the user events
   */
  List<UserEvent> findByUserId(UUID userId);

  /**
   * Count the number of attendees of a specified event.
   * 
   * @param eventId the event id
   * @return the number of attendees
   */
  long countByEventId(UUID eventId);

  /**
   * Find the event registrations of a specified event.
   * 
   * @param eventId the event id
   * @return the event registrations
   */
  List<UserEvent> findByEventId(UUID eventId);

  /**
   * Find the event registration of a specified user and a specified event.
   * 
   * @param userId the user id
   * @param eventId the event id
   * @return the user event, null if not found
   */
  UserEvent findByUserIdAndEventId(UUID userId, UUID eventId);
}
