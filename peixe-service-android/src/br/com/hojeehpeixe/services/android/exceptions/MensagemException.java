package br.com.hojeehpeixe.services.android.exceptions;

public class MensagemException extends Exception{

	private static final long serialVersionUID = 1L;
	String message;
	
	public MensagemException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
