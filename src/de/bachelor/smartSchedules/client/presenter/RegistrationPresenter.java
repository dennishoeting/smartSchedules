package de.bachelor.smartSchedules.client.presenter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

import de.bachelor.smartSchedules.client.view.RegistrationView;

/**
 * Presenter für Registrierung
 * @author Dennis
 */
public class RegistrationPresenter {
	/**
	 * zugehöriger View
	 */
	private final RegistrationView view;

	
	/**
	 * Konstruktor
	 */
	public RegistrationPresenter() {
		view = new RegistrationView();
		
		this.addListeners();
	}
	
	/**
	 * Hinzufügen der Listener
	 */
	private void addListeners() {
		/**
		 * Bei Abbruch der Registrierung
		 */
		this.view.getAbortButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				TabPresenter.getInstance().showLogIn();
			}
		});
		
		this.view.getRegisterButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(validateAll()) {
					MessageDigest md;
					//FIXME: Verschlüsseln!
//					try {
//						md = MessageDigest.getInstance("SHA-1");
//						String cryptPass = new String(md.digest(view.getPass2().getValueAsString().getBytes()));
						
						ServerCommunicationsManager.getInstance().registerUser(
								view.getName().getValueAsString(), 
								view.getPass2().getValueAsString(), 
								view.geteMail().getValueAsString(), 
								view.getForename().getValueAsString(), 
								view.getSurname().getValueAsString(), 
								new AsyncCallback<Boolean>() {
									@Override
									public void onSuccess(Boolean result) {
										SC.say("Success.");
										TabPresenter.getInstance().showLogIn();
									}
									
									@Override
									public void onFailure(Throwable caught) {
										SC.say("Timo failed not too much. BUT HE DID!");
									}
								});
//					} catch (NoSuchAlgorithmException e) {
//						e.printStackTrace();
//					}
				}
			}
		});
	}
	
	private boolean validateAll() {
		return view.getForm1().validate() && view.getForm2().validate() && view.getForm3().validate();
	}
	
	public RegistrationView getView() {
		return view;
	}
}
