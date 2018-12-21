package com.doppler.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import com.doppler.entities.UserRewardPoint;

/**
 * The repository provides operations on User Reward entity.
 */
@Repository
public interface UserRewardPointRepository extends BaseRepository<UserRewardPoint> {

  /**
   * Find the rewards of a specified user.
   * 
   * @param userId the user id
   * @return the user rewards
   */
  List<UserRewardPoint> findByUserId(UUID userId);
}
