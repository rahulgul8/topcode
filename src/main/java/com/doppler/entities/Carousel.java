package com.doppler.entities;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The carousel entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Carousel extends IdentifiableEntity {

  /**
   * The title.
   */
  @NotBlank
  private String title;

  /**
   * The media url.
   */
  @NotBlank
  private String mediaUrl;
}
