package com.doppler.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The survey answer option entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SurveyAnswerOption extends IdentifiableEntity {

  /**
   * The answer option.
   */
  @NotBlank
  private String answerOption;

  /**
   * The survey question.
   */
  @NotNull
  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "survey_question_id")
  private SurveyQuestion surveyQuestion;
}
