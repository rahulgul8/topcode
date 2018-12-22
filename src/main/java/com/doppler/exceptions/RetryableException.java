package com.doppler.exceptions;

public class RetryableException extends RuntimeException {

	private static final long serialVersionUID = -7208184752792647459L;

	public RetryableException(Exception e) {
		super(e);
	}
}
