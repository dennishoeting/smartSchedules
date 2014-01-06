package de.bachelor.smartSchedules.client.view;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.validator.RegExpValidator;

import de.bachelor.smartSchedules.client.view.util.Button;
import de.bachelor.smartSchedules.client.view.util.Formular;
import de.bachelor.smartSchedules.client.view.util.HLine;
import de.bachelor.smartSchedules.client.view.util.HorizontalStack;
import de.bachelor.smartSchedules.client.view.util.PassFormular;
import de.bachelor.smartSchedules.client.view.util.VerticalStack;

/**
 * View für die Registrierung
 * @author Dennis
 */
public class RegistrationView extends Canvas {
	/**
	 * Button
	 */
	private final Button registerButton,
				   abortButton;
	
	/**
	 * Formular
	 */
	private final Formular name, 
						   eMail,
						   forename, 
						   surname;
	
	/**
	 * Passwort-Formular
	 */
	private final PassFormular pass1, pass2;
	
	private final CheckboxItem gtcAccepted;
	
	/**
	 * Window für AGBs
	 */
	private final Window gtcWindow;
	
	private final DynamicForm form1, form2, form3;
	
	/**
	 * Größe
	 */
	public static final int LEFT_STACK_CONTENT_WIDTH = 250,
							RIGHT_STACK_CONTENT_WIDTH = 440,
							CONTENT_HEIGHT = 410,
							WIDTH = 800,
							HEIGHT = 500;
	
	/**
	 * Konstruktor
	 */
	public RegistrationView() {
		this.setTitle("Registration");
		this.setTooltip("");
		
		final HorizontalStack mainStack = new HorizontalStack(HorizontalStack.STACK_TYPE_MAIN_STACK);
		final VerticalStack rightStack = new VerticalStack(VerticalStack.STACK_TYPE_SECOUND_ORDER);
		final VerticalStack leftStack = new VerticalStack(VerticalStack.STACK_TYPE_SECOUND_ORDER);

		final Label requiredLabel = new Label("<b>Note:</b> All fields are required");
		requiredLabel.setWrap(false);
		requiredLabel.setHeight(25);
		
		form1 = new DynamicForm();
		name = new Formular("Name", LEFT_STACK_CONTENT_WIDTH-70, Formular.getNormalValidator());
		eMail = new Formular("eMail", LEFT_STACK_CONTENT_WIDTH-70, Formular.getEMailValidator());
		pass1 = new PassFormular("Password", LEFT_STACK_CONTENT_WIDTH-70);
		pass2 = new PassFormular("Confirmation", LEFT_STACK_CONTENT_WIDTH-70, PassFormular.getPasswordValidator("Password"));
		form1.setFields(name, eMail, pass1, pass2);
		
		form2 = new DynamicForm();
		forename = new Formular("Forename", LEFT_STACK_CONTENT_WIDTH-65, Formular.getNormalValidator());
		surname = new Formular("Surname", LEFT_STACK_CONTENT_WIDTH-65, Formular.getNormalValidator());
		form2.setFields(forename, surname);
		
		form3 = new DynamicForm();
		gtcAccepted = new CheckboxItem("gtcAccepted", "Accepting GTC");
		gtcAccepted.setRequired(true);
		gtcAccepted.setRequiredMessage("GTC must be accepted");
		form3.setFields(gtcAccepted);
		
		registerButton = new Button("Register", LEFT_STACK_CONTENT_WIDTH);
		abortButton = new Button("Abort", LEFT_STACK_CONTENT_WIDTH);
		
		
		/*
		 * Validators
		 */
		
		gtcWindow = new Window();
		gtcWindow.setTitle("General terms and conditions (GTC)");
		gtcWindow.setWidth(RIGHT_STACK_CONTENT_WIDTH);
		gtcWindow.setHeight(CONTENT_HEIGHT);
		gtcWindow.setCanDragReposition(false);
		gtcWindow.setShowCloseButton(false);
		gtcWindow.setShowMinimizeButton(false);
		
		leftStack.addMembers(requiredLabel, form1, new HLine(LEFT_STACK_CONTENT_WIDTH), form2, new HLine(LEFT_STACK_CONTENT_WIDTH), form3, new HLine(LEFT_STACK_CONTENT_WIDTH), registerButton, abortButton);
		rightStack.addMember(gtcWindow);
		mainStack.addMembers(leftStack, rightStack);
		this.addChild(mainStack);
	}
	
	public Button getAbortButton() {
		return abortButton;
	}

	public Button getRegisterButton() {
		return registerButton;
	}

	public Formular getName() {
		return name;
	}

	public Formular geteMail() {
		return eMail;
	}

	public Formular getForename() {
		return forename;
	}

	public Formular getSurname() {
		return surname;
	}

	public PassFormular getPass1() {
		return pass1;
	}

	public PassFormular getPass2() {
		return pass2;
	}

	public Window getAgbWindow() {
		return gtcWindow;
	}

	public DynamicForm getForm1() {
		return form1;
	}

	public DynamicForm getForm2() {
		return form2;
	}

	public DynamicForm getForm3() {
		return form3;
	}
}
