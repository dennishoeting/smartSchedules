package de.bachelor.smartSchedules.client.presenter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import de.bachelor.smartSchedules.client.view.InvokeEventView;
import de.bachelor.smartSchedules.shared.model.schedule.Product;
import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleOrder;
import de.bachelor.smartSchedules.shared.model.schedule.event.MachineBreakDownEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.MachineRepairedEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewOrdersEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewProductsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewResourceEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewVariantsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleChangeByUserEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleEvent;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;
import de.bachelor.smartSchedules.shared.model.util.event.UtilEvent;
import de.bachelor.smartSchedules.shared.model.util.event.XMLEvent;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.RemoteEventServiceFactory;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;

/**
 *  Manager für Scenario (Singleton)
 */
public class ScenarioPresenter {
	/**
	 * statische Instanz
	 */
	private static ScenarioPresenter instance = new ScenarioPresenter();
	
	/**
	 * aktuelles Scenario
	 */
	private Scenario scenario;
	
	/**
	 * Liste mit Kennzahlen
	 */
	private List<KeyFigure> keyFigureList;
	
	/**
	 * Liste mit Events
	 */
	private List<ScheduleEvent> scheduleEvents;
	
	private List<ScheduleEvent> uninvokedScheduleEvents;
	

	/**
	 * Konstruktor
	 */
	private ScenarioPresenter() {
		this.keyFigureList = new ArrayList<KeyFigure>();
		this.scheduleEvents = new ArrayList<ScheduleEvent>();
		this.uninvokedScheduleEvents = new ArrayList<ScheduleEvent>();
		
	}
	
	/**
	 * Scenario setzen (Bei Login)
	 * @param scenario
	 * @see TabPresenter
	 */
	public void putScenario(Scenario scenario) {
		this.scenario = scenario;
		
		/*
		 *  EventserviceListener hinzufügen
		 */
		ScenarioPresenter.this.addGWTEventServiceListener(this.scenario.getEventServiceDomain());
		this.keyFigureList = scenario.getKeyFigureList();
	}
	
	/**
	 * Liefert das aktuelle Scenario oder null
	 */
	public Schedule getChosenSchedule() {
		if(scenario == null) {
			return null;
		}
		
		GWT.log(scenario.getChosenSchedule().getStart()+"");
		
		return this.scenario.getChosenSchedule();
	}
	
	/**
	 * Setzt die KeyFigureList neu
	 * @param newKeyFigureList
	 * @see DefineEvaluationPresenter
	 */
	public void setKeyFigureList(List<KeyFigure> newKeyFigureList) {
		/*
		 * Ausnahme: Keine Änderungen
		 */
		if(this.keyFigureList.equals(newKeyFigureList)) {
			SC.say("No changes to the old key figure order");
			return;
		}
		
		this.keyFigureList = newKeyFigureList;

		/*
		 * Kommunikation mit Server
		 * Schicken der geänderten Kennzahlenreihenfolge
		 */
		ServerCommunicationsManager.getInstance().setKeyFigures(this.scenario.getScenarioID(), keyFigureList, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				SC.say("Key figure update failed");
			}

			@Override
			public void onSuccess(Void result) {
				SC.say("Key figures updated");
			}
		});
	}
	
	public List<KeyFigure> getKeyFigureList() {
		return this.keyFigureList;
	}
	
	public Scenario getScenario() {
		return scenario;
	}
	
	/**
	 * Fügt den GWTEventServiceListener hinzu.
	 */
	private void addGWTEventServiceListener(Domain eventServiceDomain) {
		RemoteEventServiceFactory.getInstance().getRemoteEventService().addListener(eventServiceDomain, new RemoteEventListener() {
			@Override
			public void apply(Event anEvent) {
				/*
				 * Schedule Event
				 */
				if(anEvent instanceof ScheduleEvent) {
					GWT.log("Jau. Ist angekommen");
					
					/*
					 * In Liste einfügen und an EventHistory senden
					 */
					scheduleEvents.add((ScheduleEvent)anEvent);
					TabPresenter.getInstance().getEventTabPresenter().getEventHistoryPresenter().insertIntoListGrid((ScheduleEvent)anEvent);

					/*
					 * In die liste der uninvoked ScheduleEvents einfügen.
					 * Diese werden invoked, sobald Update geklickt
					 */
					uninvokedScheduleEvents.add((ScheduleEvent)anEvent);
					
					/*
					 * Falls ScheduleChangeByUserEvent,
					 * direkt ausführen und Views updaten
					 */
					if(anEvent instanceof ScheduleChangeByUserEvent && UserPresenter.getInstance().isAdvancedUser()) {
						TabPresenter.getInstance().getScheduleTabPresenter().getCurrentSchedulePresenter().updateClickAction();
						SC.clearPrompt();
						return;
					}
					
					/*
					 * Counter in CurrentSchedule erhöhen
					 */
					TabPresenter.getInstance().getScheduleTabPresenter().getCurrentSchedulePresenter().increaseNewEventsCounter();
			
				/*
				 * Kein ScheduleEvent
				 */
				} else if(anEvent instanceof UtilEvent) {
						((UtilEvent)anEvent).process(ScenarioPresenter.this);
						
				/*
				 * XMLEvent: Falls eine XMLFile mit Events eingelesen wurde.	
				 */
				} else if(anEvent instanceof XMLEvent) {
					((XMLEvent)anEvent).process(TabPresenter.getInstance().getEventTabPresenter().getInvokeEventPresenter().getView());
				}
				
				/*
				 * InvokeEvent-View resetten und invoke-Button wieder enablen
				 */
				TabPresenter.getInstance().getEventTabPresenter().getInvokeEventPresenter().getView().getInvokeButton().setDisabled(false);
			}
		});
	}


	public List<ScheduleEvent> getScheduleEvents() {
		return scheduleEvents;
	}

	public void setScheduleEvents(List<ScheduleEvent> scheduleEvents) {
		this.scheduleEvents = scheduleEvents;
	}

	public List<ScheduleEvent> getUninvokedScheduleEvents() {
		return uninvokedScheduleEvents;
	}
	
	public void clearUninvokedScheduleEvents() {
		this.uninvokedScheduleEvents.clear();
	}	
	
	/**
	 * Singleton-Getter
	 * @return Instanz
	 */
	public static ScenarioPresenter getInstance() {
		return ScenarioPresenter.instance;
	}
}
