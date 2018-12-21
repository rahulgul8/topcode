package com.doppler.entities;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The topic entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Topic extends IdentifiableEntity {

  /**
   * The name.
   */
  @NotBlank
  private String name;

  /**
   * The icon url.
   */
  @NotBlank
  private String iconUrl;
}
