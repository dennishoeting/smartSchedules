package de.bachelor.smartSchedules.shared.model.exceptions;
/**
 * Wird geworfen, wenn der Nickname schon benutzt wird.
 * @author timo
 *
 */
public class NicknameAlreadyInUseException extends Exception {

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -4369380542472476939L;
	
	/**
	 * Default
	 */
	public NicknameAlreadyInUseException() {
		
	}
	
	/**
	 * Konsturktor
	 * @param nickname
	 */
	public NicknameAlreadyInUseException(String nickname) {
		super(nickname +" already in use!");
	}
}
