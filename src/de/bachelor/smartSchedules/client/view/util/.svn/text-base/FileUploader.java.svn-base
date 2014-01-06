package de.bachelor.smartSchedules.client.view.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.smartgwt.client.widgets.Canvas;

import de.bachelor.smartSchedules.client.view.FileUploaderInterface;

/**
 * Widget für einen DateiUpload.
 * @author timo
 *
 */
public class FileUploader extends Canvas{
	private static final String UPLOAD_ACTION_URL = GWT.getModuleBaseURL() + "upload";
	private final FormPanel form;
	private final HorizontalPanel horizontalPanel;
	private final FileUpload upload;
	private int scenarioID, userID;
	private final TextBox scenarioBox, userIDBox;
	private final FileUploaderInterface view;
	
	public FileUploader(FileUploaderInterface view) {
		super();
		this.view = view;
		// FileUpload:
		this.upload = new FileUpload();
		this.upload.setName("uploadFormElement");
		this.upload.setHeight("25px");
		
		// Textbox mit der ScenarioID:
		this.scenarioID = -1;
		this.scenarioBox = new TextBox();
		this.scenarioBox.setName("scenarioID");
		this.scenarioBox.setValue("" +this.scenarioID);
		this.scenarioBox.setVisible(false);
		this.scenarioBox.setWidth("0px");
		this.scenarioBox.setHeight("0px");
		
		// Textbox mit der UserID:
		this.userID = -1;
		this.userIDBox = new TextBox();
		this.userIDBox.setName("userID");
		this.userIDBox.setValue("" +this.userID);
		this.userIDBox.setVisible(false);
		this.userIDBox.setWidth("0px");
		this.userIDBox.setHeight("0px");
		
		// Alles auf ein VerticalPanel adden:
		this.horizontalPanel = new HorizontalPanel();
		this.horizontalPanel.add(scenarioBox);
		this.horizontalPanel.add(userIDBox);
		this.horizontalPanel.add(upload);
		this.horizontalPanel.setHeight("30px");
		this.horizontalPanel.setWidth("200px");
	
		// Form einstellungen und VerticalPanel adden:
		this.form = new FormPanel();
		this.form.setAction(UPLOAD_ACTION_URL);
		this.form.setEncoding(FormPanel.ENCODING_MULTIPART);
		this.form.setMethod(FormPanel.METHOD_POST);
		this.form.add(horizontalPanel);
		this.form.setHeight("30px");
		this.form.setWidth("200px");
		
		this.setPadding(0);
		this.setMargin(0);
		this.addChild(this.form);
		
		// Add an event handler to the form.
		this.form.addSubmitHandler(new FormPanel.SubmitHandler() {
			public void onSubmit(SubmitEvent event) {
				FileUploader.this.view.testingUpload();
			}
		});

		this.form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {
				/*
				 * Nichts
				 */
			}
		});
	}

	public FormPanel getForm() {
		return form;
	}

	public FileUpload getUpload() {
		return upload;
	}
	
	public TextBox getScenarioBox() {
		return this.scenarioBox;
	}
	
	public TextBox getUserIDBox() {
		return this.userIDBox;
	}
}
