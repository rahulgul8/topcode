package com.doppler.entities;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The user setting. This entity is not mapped to a database table.
 */
@Getter
@Setter
@ToString
public class UserSetting {

  /**
   * The flag to indicate that the user wants to be notified whenever a new event gets launched.
   */
  @NotNull
  private Boolean notifiedByNewEvents;
}
