package com.doppler.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The user entity.
 */
@Entity
@Table(name = "\"user\"")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class User extends IdentifiableEntity {

  /**
   * The email.
   */
  @Email
  @NotNull
  @Column(unique = true)
  private String email;

  /**
   * The full name. Should be synchronized with SAML.
   */
  private String fullName;

  /**
   * The division. Should be synchronized from SAML.
   */
  private String division;

  /**
   * The position. Should be synchronized from SAML.
   */
  private String position;

  /**
   * The location. Should be synchronized from SAML.
   */
  private String location;

  /**
   * The phone number. Should be synchronized from SAML.
   */
  @NotBlank
  private String phoneNumber;

  /**
   * The photo url. Should be synchronized from SAML.
   */
  private String photoUrl;

  /**
   * The flag to indicate that the user is a new or an existing user.
   */
  @NotNull
  private Boolean isNew = true;

  /**
   * The flag to indicate that the user wants to be notified whenever a new event gets launched.
   */
  @JsonIgnore
  private boolean notifiedByNewEvents = true;
  
  
  @JsonIgnore
  private String backendToken;

  /**
   * The points.
   */
  private int points;
}
