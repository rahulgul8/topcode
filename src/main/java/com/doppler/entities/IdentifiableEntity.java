package com.doppler.entities;

import java.util.UUID;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The base class for all entities with UUID primary key.
 */
@MappedSuperclass
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class IdentifiableEntity {

  /**
   * The entity id.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;
}
