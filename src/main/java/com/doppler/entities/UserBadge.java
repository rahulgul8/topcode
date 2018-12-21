package com.doppler.entities;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The user badge entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserBadge extends IdentifiableEntity {

  /**
   * The badge id.
   */
  @NotNull
  @Column(columnDefinition = "uuid")
  private UUID badgeId;

  /**
   * The user id.
   */
  @NotNull
  @Column(columnDefinition = "uuid")
  private UUID userId;

  /**
   * The date time that the user has gained this badge, null if not gained yet.
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(updatable = false)
  private Date gainedAt;

  /**
   * Execute operations before persisting a new entity.
   */
  @PrePersist
  protected void onCreate() {
    if (gainedAt == null) {
      gainedAt = new Date();
    }
  }
}
