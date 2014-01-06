package de.bachelor.smartSchedules.server.model.xmlparser.exception;
/**
 * Falls die Resource Availability falsch ist.
 * @author timo
 *
 */
public class WrongAvailabilityException extends Exception{

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -6013769805783577021L;

	/**
	 * Default
	 */
	public WrongAvailabilityException() {
		
	}
	
	public WrongAvailabilityException(int availability) {
		super("Availability: " +availability +" is " +((availability > 100) ? "too heigh. Max: 100": "too low. Min: 1."));
	}
}
