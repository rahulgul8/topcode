package com.doppler.entities;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The user event entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserEvent extends IdentifiableEntity {

  /**
   * The event id.
   */
  @NotNull
  private UUID eventId;

  /**
   * The user.
   */
  @NotNull
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  /**
   * The flag indicates whether the QR code ticket has been scanned or not.
   */
  private boolean ticketScanned;

  /**
   * The flag indicates whether the user has submitted the survey or not.
   */
  private boolean submittedSurvey;

  /**
   * The flag indicates whether the user has submitted the quiz or not.
   */
  private boolean submittedQuiz;
}
