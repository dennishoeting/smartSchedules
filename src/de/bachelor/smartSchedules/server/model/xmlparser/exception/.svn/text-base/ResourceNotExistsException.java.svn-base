package de.bachelor.smartSchedules.server.model.xmlparser.exception;
/**
 * Falls eine Resource nicht existiert.
 * @author timo
 *
 */
public class ResourceNotExistsException extends Exception{
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -1218532442774197362L;
	
	public ResourceNotExistsException() {
		
	}
	
	/**
	 * Gibt die Fehlermeldung mit der passenden ResourceID aus.
	 * @param resourceID
	 */
	public ResourceNotExistsException(int resourceID) {
		super("Resource: " +resourceID +" not exists!");
	}
	
	/**
	 * Gibt die Fehlermeldung mit dem passenden ResourceName aus.
	 * @param resourceName
	 */
	public ResourceNotExistsException(String resourceName) {
		super("Resource: " +resourceName +" not exists!");
	}
}
