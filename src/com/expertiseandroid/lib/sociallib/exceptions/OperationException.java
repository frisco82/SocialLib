package com.expertiseandroid.lib.sociallib.exceptions;

public class OperationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4732230771674125811L;
	protected String message; 
	
	public OperationException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
