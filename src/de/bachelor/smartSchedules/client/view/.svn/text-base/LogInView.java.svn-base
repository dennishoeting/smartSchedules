package de.bachelor.smartSchedules.client.view;

import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;

import de.bachelor.smartSchedules.client.view.util.Button;
import de.bachelor.smartSchedules.client.view.util.Formular;
import de.bachelor.smartSchedules.client.view.util.HLine;
import de.bachelor.smartSchedules.client.view.util.HorizontalStack;
import de.bachelor.smartSchedules.client.view.util.PassFormular;
import de.bachelor.smartSchedules.client.view.util.VerticalStack;

/**
 * View für den Login
 * @author Dennis
 */
public class LogInView extends Canvas {
	/**
	 * ListGrid mit den Scenarios
	 */
	private final ScenarioGrid scenarioGrid;
	
	/**
	 * Button
	 */
	private final Button logInButton,
						 registrationButton;
	
	/**
	 * Formular für Name
	 */
	private final Formular name; 
	
	/**
	 * Formular für Passwort
	 */
	private final PassFormular password;
	
	/**
	 * DynamicForm für Checkbox
	 */
	private final DynamicForm checkboxForm;
	
	/**
	 * Checkbox
	 */
	private final CheckboxItem checkCreateNewScenario;
	
	private final DynamicForm dynamicForm1;
	
	/**
	 * Größe
	 */
	public static final int LEFT_STACK_CONTENT_WIDTH = 400,
							RIGHT_STACK_CONTENT_WIDTH = 235,
							CONTENT_HEIGHT = 310,
							WIDTH = 750,
							HEIGHT = 400;
	
	/**
	 * Konstruktor
	 */
	public LogInView() {
		this.setTitle("Log In");
		this.setTooltip("");
		
		final HorizontalStack mainStack = new HorizontalStack(HorizontalStack.STACK_TYPE_MAIN_STACK);
		final VerticalStack rightStack = new VerticalStack(VerticalStack.STACK_TYPE_SECOUND_ORDER);
		final VerticalStack leftStack = new VerticalStack(VerticalStack.STACK_TYPE_SECOUND_ORDER);
		
		this.dynamicForm1 = new DynamicForm();
		name = new Formular("LogIn", RIGHT_STACK_CONTENT_WIDTH-40, Formular.getNormalValidator());
		password = new PassFormular("Pass", RIGHT_STACK_CONTENT_WIDTH-40);
		dynamicForm1.setFields(name, password);
		scenarioGrid = new ScenarioGrid(LEFT_STACK_CONTENT_WIDTH, CONTENT_HEIGHT);
		logInButton = new Button("Log In", RIGHT_STACK_CONTENT_WIDTH);
		final HLine hLine = new HLine(RIGHT_STACK_CONTENT_WIDTH);
		registrationButton = new Button("Register", RIGHT_STACK_CONTENT_WIDTH);
		checkboxForm = new DynamicForm();
		checkCreateNewScenario = new CheckboxItem();
		checkCreateNewScenario.setTitle("Create new scenario");
		checkboxForm.setFields(checkCreateNewScenario);
		
		leftStack.addMembers(scenarioGrid);
		rightStack.addMembers(dynamicForm1, logInButton, hLine, registrationButton, checkboxForm);
		mainStack.addMembers(leftStack, rightStack);
		this.addChild(mainStack);
	}
	
	public Button getLogInButton() {
		return this.logInButton;
	}
	
	public Button getRegistrationButton() {
		return this.registrationButton;
	}
	
	public DynamicForm getCheckboxForm() {
		return this.checkboxForm;
	}
	
	public CheckboxItem getCheckCreateNewScenario() {
		return this.checkCreateNewScenario;
	}
	
	public ScenarioGrid getScenarioGrid() {
		return this.scenarioGrid;
	}
	
	public Formular getName() {
		return name;
	}

	public PassFormular getPassword() {
		return password;
	}

	public DynamicForm getDynamicForm1() {
		return dynamicForm1;
	}

	/**
	 * ListGrid für Scenario
	 * @author Dennis
	 */
	public class ScenarioGrid extends ListGrid {
		public ScenarioGrid(int width, int height) {
			super();
			
			this.setWidth(width);
			this.setHeight(height);
			
			this.setCanSort(false);
			this.setCanMultiSort(false);
			this.setShowRowNumbers(true);

			final ListGridField rowNumberField = new ListGridField();
			rowNumberField.setWidth(20);
			this.setRowNumberFieldProperties(rowNumberField);
			
			final ListGridField nameField = new ListGridField("name", "Name");
			nameField.setWidth(200);
			final ListGridField authorField = new ListGridField("author", "Author");
			authorField.setWidth(150);
			final ListGridField isFull = new ListGridField("isFull", "Full");
			isFull.setType(ListGridFieldType.BOOLEAN);
			isFull.setWidth(25);setFields(nameField, authorField, isFull);
		}
	}
}
