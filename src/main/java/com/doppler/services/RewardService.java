package com.doppler.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import com.doppler.entities.Reward;
import com.doppler.entities.User;
import com.doppler.entities.UserReward;
import com.doppler.entities.requests.PagingAndSortingSearchRequest;
import com.doppler.entities.responses.SearchResponse;
import com.doppler.repositories.RewardRepository;
import com.doppler.repositories.UserRepository;
import com.doppler.repositories.UserRewardRepository;
import com.doppler.security.SecurityUtils;
import com.doppler.util.ApiConstants;

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
  
  private static final String REWARD_URL = "/rewards";

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
  
  @Autowired
  private BackendAPIService restApiService;

  /**
   * Search rewards.
   * 
   * @param criteria the search criteria
   * @return the search result
   */
  @Transactional(readOnly = true)
  public SearchResponse<Reward> search(PagingAndSortingSearchRequest criteria) {
    SearchResponse<Reward> searchResponse = new SearchResponse<>();
	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ApiConstants.BASE_URI + REWARD_URL)
			.queryParam("limit", criteria.getLimit()).queryParam("offset", criteria.getOffset())
			.queryParam("sortBy", criteria.getSortBy()).queryParam("sortDirection", criteria.getSortDirection());

	restApiService.getForEntity(searchResponse.getClass(), builder.toUriString());
    return searchResponse;
  }

  /**
   * Redeem a reward.
   * 
   * @param rewardId the reward id
   * @return the created user reward
   */
  public UserReward redeem(UUID rewardId) {
	  return null;
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
