package de.bachelor.smartSchedules.server.model.xmlparser.exception;
/**
 * Falls eine Operation keine Ressourcen besitzt.
 * @author timo
 *
 */
public class ResourcesNotDefinedForOperationException extends Exception{

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -6688832207887763893L;

	/**
	 * Default
	 */
	public ResourcesNotDefinedForOperationException() {
		
	}
	
	/**
	 * 
	 * @param operationname
	 */
	public ResourcesNotDefinedForOperationException(String operationname) {
		super("No resources defined for operation: " +operationname);
	}
	
	/**
	 * 
	 * @param operatioID
	 */
	public ResourcesNotDefinedForOperationException(int operatioID) {
		super("No resources defined for operationid: " +operatioID);
	}
}
