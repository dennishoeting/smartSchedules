package de.bachelor.smartSchedules.shared.model.util.event;

import com.smartgwt.client.util.SC;

import de.bachelor.smartSchedules.client.view.FileUploaderInterface;
import de.novanic.eventservice.client.event.Event;
/**
 * Event für die XML-Parser.
 * @author timo
 *
 */
public class XMLEvent implements Event{

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 461878220469604572L;
	private String message;
	private boolean successfull;
	
	/**
	 * Default
	 */
	public XMLEvent() {
		this.message = "";
		this.successfull = false;
	}
	
	/**
	 * Konsturktor
	 * @param successfull
	 * @param message
	 */
	public XMLEvent(boolean successfull, String message) {
		this.successfull = successfull;
		this.message = message;
	}
	
	/**
	 * Führt das Event Clientseitig aus.
	 */
	public void process(FileUploaderInterface fileUploader) {
		
		SC.say(message);
		
		// Anzeige setzen:
		if(successfull) {
			fileUploader.uploadSuccessfull();
		} else {
			fileUploader.uploadFailed();
		}
	}
}
