package br.com.silva.exceptions;

public class CAEPINotFoundException extends NullPointerException {

	private static final long serialVersionUID = -6820916043756394635L;

	public CAEPINotFoundException() {
		super();
	}

	public CAEPINotFoundException(String s) {
		super(s);
	}
}
