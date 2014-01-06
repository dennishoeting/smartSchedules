package de.bachelor.smartSchedules.client.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import com.gargoylesoftware.htmlunit.javascript.host.Selection;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.tab.events.TabDeselectedEvent;
import com.smartgwt.client.widgets.tab.events.TabDeselectedHandler;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

import de.bachelor.smartSchedules.client.presenter.EventHistoryPresenter.OldEventRecord;
import de.bachelor.smartSchedules.client.view.KeyFigureTrendChart;
import de.bachelor.smartSchedules.client.view.ScheduleHistoryView;
import de.bachelor.smartSchedules.client.view.ScheduleHistoryView.OldSchedulesGrid;
import de.bachelor.smartSchedules.shared.model.exceptions.NotPlannedException;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleEvent;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;

/**
 * Presenter für alte Ablaufpläne
 * @author Dennis
 */
public class ScheduleHistoryPresenter {
	/**
	 * zugehöriger View
	 */
	private final ScheduleHistoryView view;
	
	private final List<Schedule> oldSchedules;
	
	/**
	 * Konstruktor
	 */
	public ScheduleHistoryPresenter() {
		this.view = new ScheduleHistoryView();
		this.oldSchedules = new ArrayList<Schedule>();

		/*
		 * Hilfsmap füllen
		 */
		final LinkedHashMap<String, String> keyFigureMap = new LinkedHashMap<String, String>();
		keyFigureMap.put(KeyFigureTrendChart.ALL+"", "All");
		for(KeyFigure kf : ScenarioPresenter.getInstance().getScenario().getKeyFigureList()) {
			keyFigureMap.put(kf.getId()+"", kf.getName());
		}
		this.view.getKeyFigureChooser().setValueMap(keyFigureMap);
		this.view.getKeyFigureChooser().setValue(KeyFigureTrendChart.ALL);
		
		this.addListeners();
	}
	
	public void insertFromScenario(List<Schedule> scheduleList) {
		for(Schedule schedule : scheduleList) {
			oldSchedules.add(schedule);
		}
		
		update();
	}

	public void insertFromDB(List<Schedule> scheduleList) {		
		for(Schedule schedule : scheduleList) {
			oldSchedules.add(schedule);
		}
		
		update();
	}
	
	/**
	 * Das neue Event oben auf den Stack packen
	 */
	public void insertIntoListGrid(Schedule schedule) {
		oldSchedules.add(schedule);
		
		update();
	}
	
	/**
	 * Hinzufügen alter Ablaufpläne aus Scenario in das ListGrid
	 * @param oldSchedules
	 */
	public void update() {
		/*
		 * Liste leeren
		 */
		for(ListGridRecord lgr : view.getOldSchedulesGrid().getRecords()) {
			view.getOldSchedulesGrid().removeData(lgr);
		}

		/*
		 * Chart leeren
		 */
		view.getHistoryChart().clearCurves();
		
		/*
		 * Sortieren
		 */
		Collections.sort(oldSchedules, new Comparator<Schedule>() {
			@Override
			public int compare(Schedule o1, Schedule o2) {
				if(o1.getAlgorithmInformations().getDueTime().before(o2.getAlgorithmInformations().getDueTime())) return 1;
				if(o1.getAlgorithmInformations().getDueTime().after(o2.getAlgorithmInformations().getDueTime())) return -1;
				return 0;
			}
		});
	
		/*
		 * ChosenSchedule ganz oben in die Liste
		 */
		this.view.getOldSchedulesGrid().addData(new ScheduleHistoryRecord(ScenarioPresenter.getInstance().getChosenSchedule(), true));
		
		/*
		 * Rest der Liste updaten
		 */
		for(Schedule s : oldSchedules) {
			this.view.getOldSchedulesGrid().addData(
				new ScheduleHistoryRecord(s, false)
			);
		}
		
		/*
		 * Chart updaten
		 */
		this.view.getHistoryChart().initialize(this.view.getOldSchedulesGrid().getDataAsRecordList().getLength(), ScenarioPresenter.getInstance().getKeyFigureList());	
		int i=1;
		try {
			/*
			 * Alte Schedules
			 */
			for(int j=oldSchedules.size()-1; j>=0; j--) {
				for(KeyFigure keyFigure : ScenarioPresenter.getInstance().getKeyFigureList()) {
					this.view.getHistoryChart().push(i, oldSchedules.get(j).getKeyFigureValueMap().get(keyFigure.getId()), keyFigure);
				}
				i++;
			}
			
			/*
			 * Aktuellen Schedule
			 */
			for(KeyFigure keyFigure : ScenarioPresenter.getInstance().getKeyFigureList()) {
				this.view.getHistoryChart().push(i, keyFigure.calculate(ScenarioPresenter.getInstance().getScenario(), ScenarioPresenter.getInstance().getChosenSchedule()), keyFigure);
			}
		} catch (NotPlannedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Hinzufügen der Listener
	 */
	private void addListeners() {

		this.view.getZoomInButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.getHistoryChart().zoomInAction();
			}
		});
		
		this.view.getZoomOutButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.getHistoryChart().zoomOutAction();
			}
		});

		this.view.getHistoryChart().addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				view.getHistoryChart().mouseDownAction(event.getClientX(), event.getClientY());
			}
		});
		
		this.view.getHistoryChart().addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				view.getHistoryChart().mouseMoveAction(event.getClientX(), event.getClientY());
			}
		});
		
		this.view.getHistoryChart().addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				event.preventDefault();
				view.getHistoryChart().mouseUpAction(event.getClientX(), event.getClientY());
			}
		});
		
		/*
		 * Bei Wechsel der zu visualisierenden Kennzahl in Chart
		 */
		this.view.getKeyFigureChooser().addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				view.getHistoryChart().changeVisualizedKeyFigures(Integer.valueOf((String) event.getValue()));
			}
		});
		
		this.view.getViewKeyFiguresButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Schedule schedule = ((ScheduleHistoryRecord)(view.getOldSchedulesGrid().getSelectedRecord())).getSchedule();
				
				String keyFigureString = "";
				for(KeyFigure kf : ScenarioPresenter.getInstance().getKeyFigureList()) {
					keyFigureString += kf.getName() + ": " + schedule.getKeyFigureValueMap().get(kf.getId()) + "</br>";
				}
				SC.say("Key figures for " + schedule.getAlgorithmInformations().getAlgorithmName() + " ("+schedule.getAlgorithmInformations().getDueTime()+")", 
						keyFigureString);
			}
		});
		
		this.view.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				view.getHistoryChart().setVisible(true);
			}
		});
		
		this.view.addTabDeselectedHandler(new TabDeselectedHandler() {
			@Override
			public void onTabDeselected(TabDeselectedEvent event) {
				view.getHistoryChart().setVisible(false);
			}
		});
		
		/**
		 * Bei Selektion eines Schedules entsprechende Details anzeigen
		 */
		this.view.getOldSchedulesGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				view.getViewKeyFiguresButton().setDisabled(false);
				view.getViewScheduleButton().setDisabled(false);
			}
		});
		
		/**
		 * Betrachtung des ausgewählten Schedules
		 */
		this.view.getViewScheduleButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				/*
				 * Nur wenn ein Record selektiert
				 */
				if(ScheduleHistoryPresenter.this.getView().getOldSchedulesGrid().getSelectedRecord()==null) return;
				
				/*
				 * Scenario klonen
				 */
				Scenario scenario = ScenarioPresenter.getInstance().getScenario().clone();
				
				/*
				 * Solange nicht der selektierte Schedule erreicht
				 */
				for(ListGridRecord lgr : ScheduleHistoryPresenter.this.getView().getOldSchedulesGrid().getRecords()) {
					if(ScheduleHistoryPresenter.this.getView().getOldSchedulesGrid().getSelectedRecord().equals(lgr)) break;
					
					/*
					 * Backwards:
					 */
					((ScheduleHistoryRecord)lgr).getSchedule().getScheduleEvent().changeScenarioBackwards(scenario);
					GWT.log("Das war ein rückschritt...");
				}
				
				Schedule selectedSchedule = ((ScheduleHistoryRecord)(view.getOldSchedulesGrid().getSelectedRecord())).getSchedule();
				new SchedulePopupPresenter(scenario, selectedSchedule, SchedulePopupPresenter.TYPE_OLD);
			
			}
		});
	}
	
	public ScheduleHistoryView getView() {
		return this.view;
	}
	
	/**
	 * Record für alte Ablaufpläne
	 * @author Dennis
	 */
	private class ScheduleHistoryRecord extends ListGridRecord {
		/**
		 * Konstruktor
		 * @param schedule alter Ablaufplan
		 */
		public ScheduleHistoryRecord(Schedule schedule, boolean isChosenSchedule) {
			this.setAttribute("schedule", schedule);
			
			String bigStart="", bigEnd="";
			if(isChosenSchedule) {
				bigStart="<b>";
				bigEnd="</b>";
			}
			
			this.setAttribute("creationDate", bigStart+schedule.getAlgorithmInformations().getDueTime()+bigEnd);
			this.setAttribute("creator", bigStart+schedule.getAlgorithmInformations().getAlgorithmName()+bigEnd);
			this.setAttribute("reason", bigStart+((schedule.getScheduleEvent()!=null)?schedule.getScheduleEvent().getName():"Initial")+bigEnd);
			this.setAttribute("event", schedule.getScheduleEvent());
		}
		
		public Schedule getSchedule() {
			return (Schedule) this.getAttributeAsObject("schedule");
		}
		
		public ScheduleEvent getEvent() {
			return (ScheduleEvent)this.getAttributeAsObject("event");
		}
	}
}