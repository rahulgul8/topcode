package com.doppler.entities;

import java.util.Date;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.hibernate.validator.constraints.URL;
import org.springframework.util.CollectionUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The event entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Event extends IdentifiableEntity {

  /**
   * The title.
   */
  private String title;

  /**
   * The description.
   */
  private String description;

  /**
   * The points for registering.
   */
  private int pointsForRegistering;

  /**
   * The points for scanning ticket.
   */
  private int pointsForScanningTicket;

  /**
   * The points for submitting survey.
   */
  private int pointsForSubmittingSurvey;

  /**
   * The points for submitting quiz.
   */
  private int pointsForSubmittingQuiz;

  /**
   * The topic id.
   */
  private String topicId;

  /**
   * The location.
   */
  private String location;

  /**
   * The current booking.
   */
  private int currentBooking;

  /**
   * The max capacity.
   */
  private int maxCapacity;

  /**
   * The waiting capacity.
   */
  private int waitingCapacity;

  /**
   * The tags.
   */
  @ElementCollection
  private List<String> tags;

  /**
   * The portrait image url (to be displayed in the event list).
   */
  @URL
  private String portraitImageUrl;

  /**
   * The landscape image url (to be displayed in the event detail page).
   */
  @URL
  private String landscapeImageUrl;

  /**
   * The sessions.
   */
  @OneToMany(orphanRemoval = true)
  @JoinColumn(name = "event_id")
  @OrderBy("start")
  private List<EventSession> sessions;

  /**
   * The first session start date time. NOTE: whenever creating/updating/deleting the event
   * sessions, this property also needs to be updated.
   */
  @Temporal(TemporalType.TIMESTAMP)
  @JsonIgnore
  private Date firstSessionStart;

  /**
   * The last session end date time. NOTE: whenever creating/updating/deleting the event sessions,
   * this property also needs to be updated.
   */
  @Temporal(TemporalType.TIMESTAMP)
  @JsonIgnore
  private Date lastSessionEnd;

  /**
   * The flag to indicate whether the current logged-in user has registered to the event.
   */
  @Transient
  private Boolean isMyEvent;

  /**
   * Update the first session start and last session end before creating or updating the entity.
   */
  @PrePersist
  @PreUpdate
  protected void populateDates() {
    if (CollectionUtils.isEmpty(sessions)) {
      firstSessionStart = null;
      lastSessionEnd = null;

      return;
    }

    EventSession firstSession = sessions.get(0);
    firstSessionStart = firstSession.getStart();

    EventSession lastSession = sessions.get(sessions.size() - 1);
    lastSessionEnd = lastSession.getEnd();
  }
}
