package de.bachelor.smartSchedules.server.model.xmlparser.exception;
/**
 * Falls eine Operation keine Duration besitzt.
 * @author timo
 *
 */
public class DurationNotDefinedForOperationException extends Exception{

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -3517637395423681078L;
	
	/**
	 * Default
	 */
	public DurationNotDefinedForOperationException() {
		
	}

	/**
	 * 
	 * @param operationname
	 */
	public DurationNotDefinedForOperationException(String operationname) {
		super("Duration not defined for operation " +operationname);
	}
	
	/**
	 * 
	 * @param operationID
	 */
	public DurationNotDefinedForOperationException(int operationID) {
		super("Duration not defined for operationID " +operationID);
	}
}
