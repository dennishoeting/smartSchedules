package de.bachelor.smartSchedules.client.view.util;

import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.validator.MatchesFieldValidator;
import com.smartgwt.client.widgets.form.validator.Validator;

/**
 * Erweiterung des PasswordItem
 * @author Dennis
 *
 */
public class PassFormular extends PasswordItem {
	/**
	 * Konstruktor
	 * @param name Titel
	 * @param width Größe
	 */
	public PassFormular(String name, int width) {
		super(name);
		this.setWidth(width);
		this.setRequired(true);
		this.setRequiredMessage("Required.");
	}
	
	public PassFormular(String name, int width, Validator validator) {
		this(name, width);
		this.setValidators(validator);
	}
	
	public static Validator getPasswordValidator(String form2) {
		MatchesFieldValidator matchesValidator = new MatchesFieldValidator();
		matchesValidator.setOtherField(form2);
		matchesValidator.setErrorMessage("no match.");
		return matchesValidator;
	}
}
