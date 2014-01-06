package de.bachelor.smartSchedules.client.presenter;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import de.bachelor.smartSchedules.client.view.LogInView;
import de.bachelor.smartSchedules.shared.model.schedule.ScenarioInformations;
import de.bachelor.smartSchedules.shared.model.util.event.ScenarioInformationGridEvent;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.RemoteEventServiceFactory;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;

/**
 * Presenter für den LogIn
 * @author Dennis
 */
public class LogInPresenter {
	/**
	 * zugehöriger View
	 */
	private final LogInView view;
	
	public static Domain LOGIN_PRESENTER_DOMAIN = DomainFactory.getDomain("LogIn-Events");
	
	/**
	 * Konstruktor
	 */
	public LogInPresenter() {
		view = new LogInView();
		
		/*
		 * Event-Service Listener:
		 */
		RemoteEventServiceFactory.getInstance().getRemoteEventService().addListener(LOGIN_PRESENTER_DOMAIN, new RemoteEventListener() {
			@Override
			public void apply(Event anEvent) {

				if(anEvent instanceof ScenarioInformationGridEvent) {
					/*
					 * Enthält entweder neue ScenarioInformations oder geänderte.
					 */
					ScenarioInformations tmpScenarioInformations = ((ScenarioInformationGridEvent)anEvent).getScenarioInformations();
					
					/*
					 * Schleife zur Ermittlung, ob neues Scenario
					 */
					boolean isNewScenario = true;
					for(ListGridRecord lgr : view.getScenarioGrid().getRecords()) {
						/*
						 * Wenn bereits bestehendes Scenario (ID-Vergleich), ändere
						 */
						if(((ScenarioRecord)lgr).getScenarioID()==((ScenarioInformationGridEvent)anEvent).getScenarioInformations().getScenarioID()) {
							isNewScenario = false;
							((ScenarioRecord)lgr).setName(((ScenarioInformationGridEvent)anEvent).getScenarioInformations().getScenarioName());
							((ScenarioRecord)lgr).setScenarioID(((ScenarioInformationGridEvent)anEvent).getScenarioInformations().getScenarioID());
							((ScenarioRecord)lgr).setAuthor(((ScenarioInformationGridEvent)anEvent).getScenarioInformations().getAuthorName());
							((ScenarioRecord)lgr).setIsFull(!((ScenarioInformationGridEvent)anEvent).getScenarioInformations().isAdvancedUserPossible());
						}
					}
					
					/*
					 * Wenn neuer Schedule, füge hinzu
					 */
					if(isNewScenario) {
						ScenarioRecord data = new ScenarioRecord(tmpScenarioInformations.getScenarioID(), tmpScenarioInformations.getScenarioName(), tmpScenarioInformations.getAuthorName(), !tmpScenarioInformations.isAdvancedUserPossible());
						view.getScenarioGrid().addData(data);
						GWT.log("New Scenario: " + tmpScenarioInformations.getScenarioName());
					}
					
					/*
					 * Aktualisieren
					 */
					view.getScenarioGrid().redraw();
				}
				
			}
		}); 
		
		/*
		 * Scenarios anfordern
		 */
		ServerCommunicationsManager.getInstance().getScenarioInformationsList(new AsyncCallback<List<ScenarioInformations>>() {
			@Override
			public void onSuccess(List<ScenarioInformations> result) {
				ScenarioRecord data;
				for(ScenarioInformations info : result) {
					data = new ScenarioRecord(info.getScenarioID(), info.getScenarioName(), info.getAuthorName(), !info.isAdvancedUserPossible());
					view.getScenarioGrid().addData(data);
				}

				/*
				 * Cookie-Management
				 */
				if(Cookies.isCookieEnabled()
				&& Cookies.getCookie("name")!=null
				&& Cookies.getCookie("pass")!=null
				&& Cookies.getCookie("selectedScenario")!=null) {
					view.getName().setValue(Cookies.getCookie("name"));
					view.getPassword().setValue(Cookies.getCookie("pass"));
					for(ListGridRecord r : view.getScenarioGrid().getRecords()) {
						if(((ScenarioRecord)r).getScenarioID() == Integer.valueOf(Cookies.getCookie("selectedScenario"))) {
							view.getScenarioGrid().selectRecord(r);
						}
					}
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				SC.say("Scenarios not received.");
			}
		});
		
		this.addListeners();	
	}
	
	/**
	 * Hinzufügen der Listener
	 */
	private void addListeners() {
		/**
		 * Bei Login
		 */
		this.view.getLogInButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				TabPresenter.getInstance().logInClicked();
				
				// LogIn Event Listener entfernen:
				RemoteEventServiceFactory.getInstance().getRemoteEventService().removeListeners(LOGIN_PRESENTER_DOMAIN);
			}
		});
		
		/**
		 * Bei Login
		 */
		this.view.getPassword().addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				
				if(event.getKeyName().equals("Enter")) {
					TabPresenter.getInstance().logInClicked();
				}
			}
		});
		
		/**
		 * Bei Registration
		 */
		this.view.getRegistrationButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				TabPresenter.getInstance().showRegistration();
			}
		});
		
		/**
		 * Bei Wechsel der Checkbox für "CreateNewScenario"
		 */
		this.view.getCheckboxForm().addItemChangedHandler(new ItemChangedHandler() {
			@Override
			public void onItemChanged(ItemChangedEvent event) {
				LogInPresenter.this.getView().getScenarioGrid()
					.setDisabled(
							(Boolean)LogInPresenter.this.view.getCheckCreateNewScenario().getValue()
					);
			}
		});
	}
	
	public LogInView getView() {
		return view;
	}
	
	/**
	 * Record für Scenarios
	 * @author Dennis
	 */
	public class ScenarioRecord extends ListGridRecord {
		/**
		 * Konstruktor
		 * @param name Name des Scenarios
		 * @param author Author des Scenarios
		 * @param date Datum der Erstellung des Scenarios
		 */
		private ScenarioRecord(int scenarioID, String scenarioName, String authorName, boolean isFull) {
			this.setName(scenarioName);
			this.setAuthor(authorName);
			this.setScenarioID(scenarioID);
			this.setIsFull(isFull);
		}
		
		private void setName(String name) {
			this.setAttribute("name", name);
		}
		
		private void setAuthor(String author) {
			this.setAttribute("author", author);
		}
		
		private void setScenarioID(int scenario) {
			this.setAttribute("scenarioID", scenario);
		}
		
		private void setIsFull(boolean isFull) {
			this.setAttribute("isFull", isFull);
		}
		
		public String getName() {
			return this.getAttribute("name");
		}
		
		public String getAuthor() {
			return this.getAttribute("author");
		}
		
		public int getScenarioID() {
			return this.getAttributeAsInt("scenarioID");
		}
		
		public boolean getIsFull() {
			return this.getAttributeAsBoolean("isFull");
		}
	}
}
