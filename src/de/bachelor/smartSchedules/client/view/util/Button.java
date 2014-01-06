package de.bachelor.smartSchedules.client.view.util;

import com.smartgwt.client.widgets.IButton;

/**
 * Erweiterung des IButton
 * @author Dennis
 * @see IButton
 */
public class Button extends IButton {
	/**
	 * Konstruktor mit Standardgröße
	 * @param title Titel
	 */
	public Button(String title) {
		super(title);
	}
	
	/**
	 * Konstruktor
	 * @param title Titel
	 * @param width Größe
	 */
	public Button(String title, int width) {
		this(title);
		
		this.setWidth(width);
	}
}
