package com.doppler.repositories;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import com.doppler.entities.Notification;

/**
 * The repository provides operations on Notification entity.
 */
@Repository
public interface NotificationRepository extends BaseRepository<Notification> {

  /**
   * Find the notifications of a specified user.
   * 
   * @param userId the user id
   * @param pageable the paging criteria
   * @return the notification
   */
  Page<Notification> findByUserId(UUID userId, Pageable pageable);
}
