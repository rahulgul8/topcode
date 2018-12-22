package com.doppler.entities;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The user reward entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserReward extends IdentifiableEntity {

  /**
   * The topic id.
   */
  @NotNull
  @ManyToOne
  @JoinColumn(name = "reward_id")
  private Reward reward;

  /**
   * The expiration date.
   */
  @Temporal(TemporalType.TIMESTAMP)
  private Date expiredAt;

  /**
   * The user id.
   */
  @NotNull
  @JsonIgnore
  private UUID userId;
  
  private String redemptionCode;
}
