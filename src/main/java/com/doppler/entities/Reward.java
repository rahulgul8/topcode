package com.doppler.entities;

import javax.persistence.Entity;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The reward entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Reward extends IdentifiableEntity {

  /**
   * The title.
   */
  @NotBlank
  private String title;

  /**
   * The icon url.
   */
  @NotBlank
  private String iconUrl;

  /**
   * The points required to redeem the reward.
   */
  @NotNull
  @Min(1)
  private Integer pointsRequired;

  /**
   * The expiration in hours for the redeemed rewards.
   */
  @NotNull
  @Min(1)
  @JsonIgnore
  private Integer expirationInHours;
}
