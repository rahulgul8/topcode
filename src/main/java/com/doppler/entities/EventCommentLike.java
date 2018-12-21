package com.doppler.entities;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The event comment like entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EventCommentLike extends IdentifiableEntity {

  /**
   * The event comment id.
   */
  @NotNull
  @Column(columnDefinition = "uuid")
  private UUID eventCommentId;

  /**
   * The id of the user who liked the comment.
   */
  @NotNull
  @Column(columnDefinition = "uuid")
  private UUID userId;

  /**
   * The created at.
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(updatable = false)
  @JsonProperty(access = Access.READ_ONLY)
  private Date createdAt;

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
