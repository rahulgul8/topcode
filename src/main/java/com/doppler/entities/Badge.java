package com.doppler.entities;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The badge entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Badge extends IdentifiableEntity {

  /**
   * The title.
   */
  @NotBlank
  private String title;

  /**
   * The icon url.
   */
  @NotBlank
  private String iconUrl;

  /**
   * The date time that the current logged-in user has gained this badge, null if not gained yet.
   */
  @Transient
  private Date gainedAt;
}
