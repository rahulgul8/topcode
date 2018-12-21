package com.doppler.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The event comment entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EventComment extends IdentifiableEntity {

  /**
   * The event id.
   */
  @NotNull
  @Column
  @JsonIgnore
  private UUID eventId;

  /**
   * The content.
   */
  @NotBlank
  @Size(max = 1024)
  private String content;

  /**
   * The user who created the comment.
   */
  @NotNull
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  /**
   * The parent comment id. Null if this is the root comment.
   */
  @Column
  private UUID parentCommentId;

  /**
   * The like count. Should be updated whenever getting liked or un-liked.
   */
  private int likeCount;

  /**
   * The created at.
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(updatable = false)
  @JsonProperty(access = Access.READ_ONLY)
  private Date createdAt;

  /**
   * The deleted flag.
   */
  private boolean deleted;

  /**
   * The child comments.
   */
  @Transient
  private List<EventComment> childComments = new ArrayList<>();

  /**
   * The flag to indicate that the comment liked by current logged-in user or not.
   */
  @Transient
  private boolean likedByMe;

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
