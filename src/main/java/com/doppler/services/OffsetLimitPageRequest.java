package com.doppler.services;

import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * The offset limit based page request.
 */
public class OffsetLimitPageRequest extends AbstractPageRequest {

  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 4146362751484532549L;

  /**
   * The offset.
   */
  private final int offset;

  /**
   * The sort option.
   */
  private final Sort sort;

  /**
   * Create a new instance.
   * 
   * @param offset the offset
   * @param limit the limit
   * @param sort the sort option
   */
  public OffsetLimitPageRequest(int offset, int limit, Sort sort) {
    super(offset / limit, limit);

    this.offset = offset;
    this.sort = sort;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.data.domain.Pageable#getSort()
   */
  @Override
  public Sort getSort() {
    return this.sort;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.data.domain.Pageable#getOffset()
   */
  public long getOffset() {
    return this.offset;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.data.domain.Pageable#next()
   */
  @Override
  public Pageable next() {
    // Always return null in this implementation
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.data.domain.Pageable#previous()
   */
  @Override
  public Pageable previous() {
    // Always return null in this implementation
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.data.domain.Pageable#first()
   */
  @Override
  public Pageable first() {
    // Always return null in this implementation
    return null;
  }
}
