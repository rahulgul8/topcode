package com.doppler.entities;

import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The survey question entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true, exclude = {"surveyAnswerOptions"})
@EqualsAndHashCode(callSuper = true)
public class SurveyQuestion extends IdentifiableEntity {

  /**
   * The question.
   */
  @NotBlank
  private String question;

  /**
   * The type.
   */
  @NotBlank
  private String type;

  /**
   * The survey answer options.
   */
  @OneToMany
  @JoinColumn(name = "survey_question_id")
  private List<SurveyAnswerOption> surveyAnswerOptions;

  /**
   * The event id.
   */
  @NotNull
  @JsonIgnore
  private UUID eventId;
}
