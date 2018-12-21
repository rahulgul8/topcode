package com.doppler.entities;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The user reward point entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserRewardPoint extends IdentifiableEntity {

  /**
   * The description.
   */
  @NotBlank
  private String description;

  /**
   * The points.
   */
  @NotNull
  @Min(1)
  private Integer points;

  /**
   * The created at.
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(updatable = false)
  @JsonProperty(access = Access.READ_ONLY)
  private Date createdAt;

  /**
   * The user id.
   */
  @NotNull
  @Column(columnDefinition = "uuid")
  private UUID userId;

  /**
   * Execute operations before persisting a new entity.
   */
  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = new Date();
    }
  }
}
