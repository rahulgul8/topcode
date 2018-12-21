package com.doppler.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import com.doppler.entities.UserReward;

/**
 * The repository provides operations on User Reward entity.
 */
@Repository
public interface UserRewardRepository extends BaseRepository<UserReward> {

  /**
   * Get all user rewards of a specified user.
   * 
   * @param userId the user id
   * @return the user rewards
   */
  List<UserReward> findAllByUserId(UUID userId);
}
