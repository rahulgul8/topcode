package com.doppler.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.doppler.entities.Badge;
import com.doppler.entities.UserRewardPoint;
import com.doppler.services.UserService;

/**
 * The controller defines profile related endpoints.
 */
@RestController
public class ProfileController {

  /**
   * The user service.
   */
  @Autowired
  private UserService service;

  /**
   * Get the badges of current logged-in user.
   * 
   * @return the badges
   */
  @GetMapping("/myBadges")
  public List<Badge> getAllCurrentUserBadges() {
    return service.getBadgesForCurrentUser();
  }

  /**
   * Get the reward points of current logged-in user.
   * 
   * @return the reward points
   */
  @GetMapping("/myRewardPoints")
  public List<UserRewardPoint> getAllCurrentUserRewardPoints() {
    return service.getRewardsForCurrentUser();
  }
}
