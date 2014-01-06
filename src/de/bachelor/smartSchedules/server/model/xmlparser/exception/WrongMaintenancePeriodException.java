package de.bachelor.smartSchedules.server.model.xmlparser.exception;

import java.util.Date;

/**
 * Falls die Zeit per einer Maintenance Period falsch ist
 * @author timo
 *
 */
public class WrongMaintenancePeriodException extends Exception{

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 2350917321978349483L;
	
	/**
	 * Default
	 */
	public WrongMaintenancePeriodException() {
		
	}
	
	/**
	 * Konstruktor
	 * @param startDate
	 * @param dueDate
	 */
	public WrongMaintenancePeriodException(Date startDate, Date dueDate) {
		super(dueDate +" is before " +startDate);
	}
}
