package com.doppler.entities.requests;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The request includes user id.
 */
@Getter
@Setter
@ToString
public class UserIdRequest {

  /**
   * The user id.
   */
  @NotNull
  private UUID userId;
}
