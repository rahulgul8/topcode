package com.doppler.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import com.doppler.entities.UserBadge;

/**
 * The repository provides operations on User Badge entity.
 */
@Repository
public interface UserBadgeRepository extends BaseRepository<UserBadge> {

  /**
   * Find the badges of a specified user.
   * 
   * @param userId the user id
   * @return the badges
   */
  List<UserBadge> findByUserId(UUID userId);
}
