package de.bachelor.smartSchedules.server.model.xmlparser.exception;
/**
 * Für alle die sonst nirgendwo reinpassen.
 * @author timo
 *
 */
public class AnythingNotDefinedException extends Exception{

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -1158509155751187508L;

	/** 
	 * Default
	 */
	public AnythingNotDefinedException() {
		
	}
	
	/**
	 * ruft super(notDefined +" not defined for " +forThis +"!") auf
	 * @param notDefined
	 * @param forThis
	 */
	public AnythingNotDefinedException(String notDefined, String forThis) {
		super(notDefined +" not defined for " +forThis +"!");
	}
}
