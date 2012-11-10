package br.com.hojeehpeixe.services.android.exceptions;

public class CardapioException extends Exception {

	private static final long serialVersionUID = 1L;
	String message;
	public CardapioException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
