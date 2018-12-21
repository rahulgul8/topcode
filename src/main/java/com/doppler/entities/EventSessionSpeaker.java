package com.doppler.entities;

import javax.persistence.Embeddable;
import org.hibernate.validator.constraints.URL;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The event session speaker.
 */
@Embeddable
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class EventSessionSpeaker {

  /**
   * The name.
   */
  private String name;

  /**
   * The photo url.
   */
  @URL
  private String photoUrl;
}
