package com.doppler.entities;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.doppler.entities.deserializer.CustomerDateAndTimeDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The event session entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EventSession extends IdentifiableEntity {

  /**
   * The title.
   */
  private String title;

  /**
   * The location.
   */
  private String location;

  /**
   * The start date time.
   */
  @Temporal(TemporalType.TIMESTAMP)
  @NotNull
  @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
  private Date start;

  /**
   * The end date time.
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "\"end\"")
  @NotNull
  @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
  private Date end;

  /**
   * The speaker.
   */
  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "name", column = @Column(name = "speaker_name")),
      @AttributeOverride(name = "photoUrl", column = @Column(name = "speaker_photo_url"))})
  private EventSessionSpeaker speaker;
}
