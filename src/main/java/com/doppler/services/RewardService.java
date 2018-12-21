package com.doppler.services;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.doppler.entities.Reward;
import com.doppler.entities.User;
import com.doppler.entities.UserReward;
import com.doppler.entities.requests.PagingAndSortingSearchRequest;
import com.doppler.entities.responses.SearchResponse;
import com.doppler.repositories.RewardRepository;
import com.doppler.repositories.UserRepository;
import com.doppler.repositories.UserRewardRepository;
import com.doppler.security.SecurityUtils;

/**
 * The service provides reward related operations.
 */
@Service
@Transactional
public class RewardService extends BaseService {

  /**
   * The milliseconds in an hour.
   */
  private static final long MILLISECONDS_IN_HOUR = 60 * 60 * 1000;

  /**
   * The reward repository.
   */
  @Autowired
  private RewardRepository rewardRepository;

  /**
   * The user repository.
   */
  @Autowired
  private UserRepository userRepository;

  /**
   * The user reward repository.
   */
  @Autowired
  private UserRewardRepository userRewardRepository;

  /**
   * Search rewards.
   * 
   * @param criteria the search criteria
   * @return the search result
   */
  @Transactional(readOnly = true)
  public SearchResponse<Reward> search(PagingAndSortingSearchRequest criteria) {
    Pageable pageable =
        createPageRequest(criteria, Arrays.asList("title", "pointsRequired"), "title", "asc");

    SearchResponse<Reward> searchResponse = new SearchResponse<>();
    Page<Reward> page = rewardRepository.findAll(pageable);

    searchResponse.setCount(page.getTotalElements());
    searchResponse.setRows(page.getContent());

    return searchResponse;
  }

  /**
   * Redeem a reward.
   * 
   * @param rewardId the reward id
   * @return the created user reward
   */
  public UserReward redeem(UUID rewardId) {

    // Get the reward
    Optional<Reward> optionalReward = rewardRepository.findById(rewardId);
    if (!optionalReward.isPresent()) {
      throw new EntityNotFoundException("Reward does not exist with id = " + rewardId);
    }

    Reward reward = optionalReward.get();

    // Check and deduct user points
    User user = SecurityUtils.getCurrentUser();
    if (reward.getPointsRequired() > user.getPoints()) {
      throw new IllegalArgumentException("You don't have enough points to redeem the reward");
    }

    user.setPoints(user.getPoints() - reward.getPointsRequired());
    userRepository.save(user);

    // Create user reward
    UserReward userReward = new UserReward();
    userReward.setExpiredAt(new Date(
        System.currentTimeMillis() + reward.getExpirationInHours() * MILLISECONDS_IN_HOUR));
    userReward.setReward(reward);
    userReward.setUserId(user.getId());

    return userRewardRepository.save(userReward);
  }

  /**
   * Get all rewards of the current user.
   *
   * @return the user rewards
   */
  @Transactional(readOnly = true)
  public List<UserReward> getAllCurrentUserRewards() {
    return userRewardRepository.findAllByUserId(SecurityUtils.getCurrentUser().getId());
  }
}
