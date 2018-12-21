package com.doppler.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import com.doppler.entities.UserTopic;

/**
 * The repository provides operations on User Topic entity.
 */
@Repository
public interface UserTopicRepository extends BaseRepository<UserTopic> {

  /**
   * Find the topics of a specified user.
   * 
   * @param userId the user id
   * @return the user topics
   */
  List<UserTopic> findByUserId(UUID userId);

  /**
   * Delete the user topics which ids are not in a list of specified ids.
   * 
   * @param userId the user id
   * @param ids the ids
   */
  void deleteByUserIdAndIdNotIn(UUID userId, List<UUID> ids);
}
