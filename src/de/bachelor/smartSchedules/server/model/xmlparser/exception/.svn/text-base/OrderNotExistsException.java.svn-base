package de.bachelor.smartSchedules.server.model.xmlparser.exception;
/**
 * Falls eine Order nicht existiert.
 * @author timo
 *
 */
public class OrderNotExistsException extends Exception{
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 5988005536832421836L;
	
	public OrderNotExistsException() {
		
	}
	
	/**
	 * Gibt die Fehlermeldung mit der OrderID aus.
	 * @param orderID
	 */
	public OrderNotExistsException(int orderID) {
		super("Order: " +orderID +" not exists!");
	}
	
	public OrderNotExistsException(String message) {
		super(message);
	}
}
