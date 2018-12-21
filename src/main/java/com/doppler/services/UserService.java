package com.doppler.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.doppler.entities.Badge;
import com.doppler.entities.Reward;
import com.doppler.entities.Topic;
import com.doppler.entities.User;
import com.doppler.entities.UserBadge;
import com.doppler.entities.UserRewardPoint;
import com.doppler.entities.UserSetting;
import com.doppler.entities.requests.UserSearchRequest;
import com.doppler.entities.responses.SearchResponse;
import com.doppler.repositories.BadgeRepository;
import com.doppler.repositories.UserBadgeRepository;
import com.doppler.repositories.UserRepository;
import com.doppler.repositories.UserRewardPointRepository;
import com.doppler.security.SecurityUtils;
import com.doppler.util.ApiConstants;

/**
 * The service provides topic related operations.
 */
@Service
@Transactional
public class UserService extends BaseService {

	/**
	 * The user repository.
	 */
	@Autowired
	private UserRepository userRepository;

	/**
	 * The badge repository.
	 */
	@Autowired
	private BadgeRepository badgeRepository;

	/**
	 * The user badge repository.
	 */
	@Autowired
	private UserBadgeRepository userBadgeRepository;

	/**
	 * The user reward point repository.
	 */
	@Autowired
	private UserRewardPointRepository userRewardPointRepository;

	private RestTemplate restTemplate = new RestTemplate();
	
	@Autowired
	private BackendAPIService backendAPIService;

	@Autowired
	LoginRestService loginService;

	private static final String BADGE_URL = "/badges";
	private static final String REWARD_URL = "/rewards";

	/**
	 * Get user setting.
	 * 
	 * @return the user setting
	 */
	@Transactional(readOnly = true)
	public UserSetting getSetting() {
		UserSetting setting = new UserSetting();
		setting.setNotifiedByNewEvents(SecurityUtils.getCurrentUser().isNotifiedByNewEvents());

		return setting;
	}

	/**
	 * Update user setting.
	 * 
	 * @param userSetting
	 *            the user setting
	 * @return the updated user setting
	 */
	public UserSetting updateSetting(UserSetting userSetting) {
		User user = SecurityUtils.getCurrentUser();
		user.setNotifiedByNewEvents(userSetting.getNotifiedByNewEvents());
		userRepository.save(user);

		return userSetting;
	}

	/**
	 * Search users.
	 *
	 * @param criteria
	 *            the search criteria
	 * @return the search result
	 */
	@Transactional(readOnly = true)
	public SearchResponse<User> search(UserSearchRequest criteria) {
		criteria.setSortBy("fullName");
		criteria.setKeyword(criteria.getKeyword() == null ? "" : criteria.getKeyword());
		Pageable pageable = createPageRequest(criteria, Arrays.asList("fullName"), "fullName", "asc");

		SearchResponse<User> searchResponse = new SearchResponse<>();
		Page<User> page = userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
				criteria.getKeyword(), criteria.getKeyword(), pageable);

		searchResponse.setCount(page.getTotalElements());
		searchResponse.setRows(page.getContent());

		return searchResponse;
	}

	/**
	 * Get the badges of current logged-in user.
	 * 
	 * @return the badges
	 */
	@Transactional(readOnly = true)
	public List<Badge> getAllCurrentUserBadges() {
		List<Badge> badges = badgeRepository.findAll();
		List<UserBadge> userBadges = userBadgeRepository.findByUserId(SecurityUtils.getCurrentUser().getId());

		badges.forEach(badge -> {
			userBadges.forEach(userBadge -> {
				if (userBadge.getBadgeId().equals(badge.getId())) {
					badge.setGainedAt(userBadge.getGainedAt());
				}
			});
		});

		return badges;
	}

	/**
	 * Get the reward points of current logged-in user.
	 * 
	 * @return the reward points
	 */
	@Transactional(readOnly = true)
	public List<UserRewardPoint> getAllCurrentUserRewardPoints() {
		return userRewardPointRepository.findByUserId(SecurityUtils.getCurrentUser().getId());
	}
	
	

	
	public List<Badge> getBadgesForCurrentUser() {
		return backendAPIService.getForList(Badge.class, ApiConstants.BASE_URI + BADGE_URL);
	}
	
	public List<UserRewardPoint> getRewardsForCurrentUser() {
		String token = loginService.userLogin();
		ResponseEntity<List> response = restTemplate.exchange(ApiConstants.BASE_URI + REWARD_URL, HttpMethod.GET,
				constructHttpEntityWithRequestHeaders(token), List.class);
		return (List<UserRewardPoint>) response.getBody();
	}
}
