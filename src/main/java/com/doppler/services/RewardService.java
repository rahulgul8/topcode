package com.doppler.services;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import com.doppler.entities.Reward;
import com.doppler.entities.RewardRedeem;
import com.doppler.entities.User;
import com.doppler.entities.UserReward;
import com.doppler.entities.requests.PagingAndSortingSearchRequest;
import com.doppler.entities.responses.SearchResponse;
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
	 * The user reward repository.
	 */
	@Autowired
	private UserRewardRepository userRewardRepository;

	@Autowired
	private BackendAPIService restApiService;
	
	  @Autowired
	  private UserRepository userRepository;

	/**
	 * Search rewards.
	 * 
	 * @param criteria the search criteria
	 * @return the search result
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public SearchResponse<Reward> search(PagingAndSortingSearchRequest criteria) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ApiConstants.BASE_URI + REWARD_URL)
				.queryParam("limit", criteria.getLimit()).queryParam("offset", criteria.getOffset())
				.queryParam("sortBy", criteria.getSortBy()).queryParam("sortDirection", criteria.getSortDirection());
		return restApiService.getForEntity(SearchResponse.class, Reward.class, builder.toUriString());
	}

	public Reward getRewardByID(UUID id) {
		Reward reward = restApiService.getForEntity(Reward.class, ApiConstants.BASE_URI + REWARD_URL + "/{id}", id);
		return reward;
	}
	
	/**
	 * Redeem a reward.
	 * 
	 * @param rewardId the reward id
	 * @return the created user reward
	 */
	public UserReward redeem(UUID rewardId) {
		Reward reward = getRewardByID(rewardId);
		if (Objects.isNull(reward)) {
			throw new EntityNotFoundException("Entity  not available");
		}
		User user = SecurityUtils.getCurrentUser();
		if (reward.getPointsRequired() > user.getPoints()) {
			throw new IllegalArgumentException("You don't have enough points to redeem the reward");
		}

		user.setPoints(user.getPoints() - reward.getPointsRequired());
		userRepository.save(user);

		// Create user reward
		UserReward userReward = new UserReward();

		RewardRedeem output = restApiService.putForEntity(RewardRedeem.class,
				ApiConstants.BASE_URI + REWARD_URL + "/" + rewardId + "/redeem", null);
		if (Objects.nonNull(output)) {
			userReward.setRedemptionCode(output.getRedemptionCode());
		}
		return userReward;
	}

	/**
	 * Get all rewards of the current user.
	 *
	 * @return the user rewards
	 */
	@Transactional(readOnly = true)
	public List<UserReward> getAllCurrentUserRewards() {
		return restApiService.getForList(UserReward.class, ApiConstants.BASE_URI+REWARD_URL);
	}
}
