package de.bachelor.smartSchedules.client.view.util;

import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.validator.RegExpValidator;
import com.smartgwt.client.widgets.form.validator.Validator;

/**
 * Erweiterung eines TextItems
 * @author Dennis
 */
public class Formular extends TextItem {
	/**
	 * Konstruktor
	 * @param name Titel
	 * @param width Größe
	 */
	public Formular(String name, int width) {
		super(name);
		this.setWidth(width);
	}
	
	public Formular(String name, int width, Validator validator) {
		this(name, width);
		this.setRequired(true);
		this.setRequiredMessage("Required.");
		this.setValidators(validator);
	}
	
	public static Validator getNormalValidator() {
		RegExpValidator nicknameValidator = new RegExpValidator();
		nicknameValidator.setErrorMessage("Value invalid.");
		nicknameValidator.setExpression("^([a-zA-Z0-9_-])+$");
		return nicknameValidator;
	}
	
	public static Validator getEMailValidator() {
		RegExpValidator emailValidator = new RegExpValidator();
		emailValidator.setErrorMessage("Value invalid.");
		emailValidator.setExpression("^([a-zA-Z0-9_.\\-+])+@(([a-zA-Z0-9\\-])+\\.)+[a-zA-Z0-9]{2,4}$");
		return emailValidator;
	}
}
