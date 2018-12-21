package com.doppler.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.doppler.entities.Topic;
import com.doppler.entities.User;
import com.doppler.entities.UserRewardPoint;
import com.doppler.entities.UserTopic;
import com.doppler.entities.requests.UserTopicRequest;
import com.doppler.repositories.TopicRepository;
import com.doppler.repositories.UserRepository;
import com.doppler.repositories.UserRewardPointRepository;
import com.doppler.repositories.UserTopicRepository;
import com.doppler.security.SecurityUtils;
import com.doppler.services.config.UserRewardPointDescriptionConfiguration;

/**
 * The service provides user topic related operations.
 */
@Service
@Transactional
public class UserTopicService extends BaseService {

  /**
   * The topic repository.
   */
  @Autowired
  private TopicRepository topicRepository;

  /**
   * The user topic repository.
   */
  @Autowired
  private UserTopicRepository userTopicRepository;

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
   * The points earned for completing profile.
   */
  @Value("${user-reward-point.pointsForCompletingProfile}")
  private int pointsForCompletingProfile;

  /**
   * The user reward point description configuration.
   */
  @Autowired
  private UserRewardPointDescriptionConfiguration userRewardPointDescriptionConfiguration;

  /**
   * Get all user topics.
   * 
   * @return the user topics
   */
  @Transactional(readOnly = true)
  public List<UserTopic> search() {
    return userTopicRepository.findByUserId(SecurityUtils.getCurrentUser().getId());
  }

  /**
   * Update user topics.
   * 
   * @param request the request
   * @return the user reward point if this is a new user, otherwise null
   */
  public UserRewardPoint update(List<UserTopicRequest> request) {

    // Validate
    validateList(request, "request");
    List<UUID> topicIds =
        request.stream().map(UserTopicRequest::getTopicId).collect(Collectors.toList());
    validateList(topicIds, "request[].topicId");

    // Topic ids must exist
    List<UUID> allTopicIds =
        topicRepository.findAll().stream().map(Topic::getId).collect(Collectors.toList());
    for (UUID topicId : topicIds) {
      if (!allTopicIds.contains(topicId)) {
        throw new IllegalArgumentException("Topic does not exist with id = " + topicId);
      }
    }

    User currentUser = SecurityUtils.getCurrentUser();

    // Delete
    List<UUID> ids = request.stream().map(UserTopicRequest::getId).filter(id -> id != null)
        .collect(Collectors.toList());
    ids.add(UUID.randomUUID());
    userTopicRepository.deleteByUserIdAndIdNotIn(currentUser.getId(), ids);

    // Save
    List<UserTopic> userTopics = request.stream().map(userTopicRequest -> {
      UserTopic userTopic = new UserTopic();
      BeanUtils.copyProperties(userTopicRequest, userTopic);
      userTopic.setUserId(currentUser.getId());
      return userTopic;
    }).collect(Collectors.toList());

    userTopics = userTopicRepository.saveAll(userTopics);

    // Add points for new user
    if (userTopics.size() > 0 && currentUser.getIsNew()) {
      currentUser.setIsNew(false);
      currentUser.setPoints(currentUser.getPoints() + pointsForCompletingProfile);
      userRepository.save(currentUser);

      // Create user reward point
      UserRewardPoint userRewardPoint = new UserRewardPoint();
      userRewardPoint.setPoints(pointsForCompletingProfile);
      userRewardPoint.setUserId(currentUser.getId());
      userRewardPoint.setDescription(userRewardPointDescriptionConfiguration.getCompleteProfile());

      return userRewardPointRepository.save(userRewardPoint);
    }

    return null;
  }
}
