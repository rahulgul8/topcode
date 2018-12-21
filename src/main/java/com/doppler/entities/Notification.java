package com.doppler.entities;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The notification entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Notification extends IdentifiableEntity {

  /**
   * The icon url.
   */
  private String iconUrl;

  /**
   * The content.
   */
  @NotBlank
  private String content;

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
  @JsonIgnore
  private UUID userId;

  /**
   * The flag indicates whether the user has read the notification or not.
   */
  private boolean read;

  /**
   * The object type related to this notification (e.g. Event that is shared from another user).
   */
  private String relatedObjectType;

  /**
   * The object id related to this notification (e.g. Event Id that is shared from another user).
   */
  private UUID relatedObjectId;

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
