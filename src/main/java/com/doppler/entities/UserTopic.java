package com.doppler.entities;

import java.util.UUID;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The user topic entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserTopic extends IdentifiableEntity {

  /**
   * The topic id.
   */
  @NotNull
  private UUID topicId;

  /**
   * The user id.
   */
  @NotNull
  private UUID userId;
}
