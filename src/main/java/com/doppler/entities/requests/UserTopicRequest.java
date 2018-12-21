package com.doppler.entities.requests;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The user topic update request.
 */
@Getter
@Setter
@ToString
public class UserTopicRequest {

  /**
   * The id.
   */
  private UUID id;

  /**
   * The topic id.
   */
  @NotNull
  private UUID topicId;
}
