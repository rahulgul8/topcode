package com.doppler.services.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

/**
 * The notification content configurations, should be loaded from properties.
 */
@Configuration
@ConfigurationProperties(prefix = "notification.content")
@Getter
@Setter
public class NotificationContentConfiguration {

  /**
   * The content template for sharing event.
   */
  private String shareEvent;
}
