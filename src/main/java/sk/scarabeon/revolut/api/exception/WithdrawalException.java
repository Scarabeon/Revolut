package sk.scarabeon.revolut.api.exception;

public class WithdrawalException extends Exception {
	private static final long serialVersionUID = -8620284452013527726L;

	public WithdrawalException(String message) {
		super(message);
	}
}