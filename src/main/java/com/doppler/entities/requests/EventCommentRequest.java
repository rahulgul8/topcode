package com.doppler.entities.requests;

import java.util.UUID;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The event comment create/update request.
 */
@Getter
@Setter
@ToString
public class EventCommentRequest {

  /**
   * The content.
   */
  @NotBlank
  private String content;

  /**
   * The parent comment id.
   */
  private UUID parentCommentId;
}
