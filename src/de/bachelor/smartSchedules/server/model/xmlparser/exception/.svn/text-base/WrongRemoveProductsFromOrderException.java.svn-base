package de.bachelor.smartSchedules.server.model.xmlparser.exception;
/**
 * Falls mehr Produkte oder falsche Produkte gelöscht werden sollen.
 * @author timo
 *
 */
public class WrongRemoveProductsFromOrderException extends Exception{

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 6817790619141945736L;
	
	/**
	 * Default
	 */
	public WrongRemoveProductsFromOrderException() {
		
	}
	
	public WrongRemoveProductsFromOrderException(int orderID, String productName, int productAmount, int removeAmount) {
		super("Can't remove " +removeAmount +" of " +productAmount +" of "+productName +" from Order " +orderID);
	}

}
