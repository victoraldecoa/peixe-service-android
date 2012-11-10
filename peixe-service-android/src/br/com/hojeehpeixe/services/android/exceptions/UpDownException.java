package br.com.hojeehpeixe.services.android.exceptions;

public class UpDownException extends Exception {

	private static final long serialVersionUID = 1L;
	String message;
	
	public UpDownException(String message) {
		this.message = message;
	}
	
	public UpDownException()
	{
		
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
