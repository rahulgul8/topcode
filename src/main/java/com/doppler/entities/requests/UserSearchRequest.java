package com.doppler.entities.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The user search request.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class UserSearchRequest extends PagingAndSortingSearchRequest {

  /**
   * The keyword filter.
   */
  private String keyword;
}
