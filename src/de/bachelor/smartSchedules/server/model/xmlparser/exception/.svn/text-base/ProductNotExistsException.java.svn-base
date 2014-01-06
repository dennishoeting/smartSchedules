package de.bachelor.smartSchedules.server.model.xmlparser.exception;
/**
 * Falls ein Product nicht existiert.
 * @author timo
 *
 */
public class ProductNotExistsException extends Exception{

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -5659411744688175403L;
	
	/**
	 * Default
	 */
	public ProductNotExistsException() {
		
	}
	
	/**
	 * Konstruktor
	 * @param productID
	 */
	public ProductNotExistsException(int productID) {
		super("Product: " +productID +" not exists!");
	}
	
	public ProductNotExistsException(String message) {
		super(message);
	}
	
}
