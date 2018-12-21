package com.doppler.controllers;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.doppler.entities.Reward;
import com.doppler.entities.UserReward;
import com.doppler.entities.requests.PagingAndSortingSearchRequest;
import com.doppler.entities.responses.SearchResponse;
import com.doppler.services.RewardService;

/**
 * The controller defines reward related endpoints.
 */
@RestController
public class RewardController {

  /**
   * The reward service.
   */
  @Autowired
  private RewardService service;

  /**
   * Search events.
   *
   * @param criteria the search criteria
   * @return the search result
   */
  @GetMapping("/rewards")
  public SearchResponse<Reward> search(
      @Valid @ModelAttribute PagingAndSortingSearchRequest criteria) {
    return service.search(criteria);
  }

  /**
   * Redeem a reward.
   * 
   * @param rewardId the reward id
   * @return the created user reward
   */
  @PostMapping("/rewards/{rewardId}/redeem")
  public UserReward redeem(@PathVariable("rewardId") UUID rewardId) {
    return service.redeem(rewardId);
  }

  /**
   * Get all rewards of the current user.
   *
   * @return the user rewards
   */
  @GetMapping("/myRewards")
  public List<UserReward> getAllCurrentUserRewards() {
    return service.getAllCurrentUserRewards();
  }
}
