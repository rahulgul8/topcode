package com.doppler.services.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

/**
 * The user reward point description configurations, should be loaded from properties.
 */
@Configuration
@ConfigurationProperties(prefix = "user-reward-point.description")
@Getter
@Setter
public class UserRewardPointDescriptionConfiguration {

  /**
   * The content template for registering to an event.
   */
  private String registerEvent;

  /**
   * The content template for scanning ticket.
   */
  private String scanTicket;

  /**
   * The content template for completing profile.
   */
  private String completeProfile;

  /**
   * The content template for submitting survey.
   */
  private String submitSurvey;

  /**
   * The content template for submitting survey.
   */
  private String submitQuiz;
}
