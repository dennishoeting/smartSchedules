package de.bachelor.smartSchedules.server.model.xmlparser.exception;
/**
 * Falls ein Name schon vergeben ist.
 * @author timo
 *
 */
public class NameInUseException extends Exception{

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 3300290527387814679L;

	/**
	 * Default
	 */
	public NameInUseException() {
		
	}
	
	/**
	 * Konstruktor
	 * @param name
	 */
	public NameInUseException(String name) {
		super(name +" already in use!");
	}
}
