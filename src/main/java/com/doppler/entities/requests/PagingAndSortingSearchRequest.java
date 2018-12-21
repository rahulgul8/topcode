package com.doppler.entities.requests;

import javax.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The search request with paging and sorting criteria.
 */
@Getter
@Setter
@ToString
public class PagingAndSortingSearchRequest {

  /**
   * The offset.
   */
  @Min(0)
  private Integer offset;

  /**
   * The limit.
   */
  @Min(1)
  private Integer limit;

  /**
   * The sort by.
   */
  private String sortBy;

  /**
   * The sort direction.
   */
  private String sortDirection;
}
