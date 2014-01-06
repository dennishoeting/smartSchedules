package de.bachelor.smartSchedules.client.presenter;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;

import de.bachelor.smartSchedules.client.view.SchedulesAlternativesView;
import de.bachelor.smartSchedules.shared.model.exceptions.NotPlannedException;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleChangeByUserEvent;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;

/**
 * Presenter für das Popup für die Alternativpläne
 * @author Dennis
 */
public class ScheduleAlternativesPresenter {
	/**
	 * zugehöriger View
	 */
	private final SchedulesAlternativesView view;
	
	private List<Schedule> alternatives;
	
	private static final String ALL = "-1";
	
	/**
	 * Konstruktor mit Liste von Alternativplänen
	 * @param schedules Liste von Alternativplänen
	 */
	public ScheduleAlternativesPresenter() {
		this.view = new SchedulesAlternativesView();
		
		this.addListeners();
		
		final LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		valueMap.put("-1", "All key figures");
		int i=0;
		for(KeyFigure kf : ScenarioPresenter.getInstance().getKeyFigureList()) {
			valueMap.put((i++)+"", kf.getName());
		}
		this.view.getComparedKeyFigureChooser().setValueMap(valueMap);
		this.view.getComparedKeyFigureChooser().setValue(ALL);
	}
	
	/**
	 * Aktualisiert die Liste der Schedules
	 * @param alternatives
	 */
	public void update(List<Schedule> alternatives) {
		this.alternatives = alternatives;
		
		/*
		 * Liste leeren
		 */
		for(ListGridRecord r : this.view.getListGrid().getRecords()) {
			this.view.getListGrid().removeData(r);
		}
		
		/*
		 * Liste füllen
		 */
		for(Schedule schedule : this.alternatives) {
			boolean isTheSameAsTheChosen = schedule.getAlgorithmInformations().equals(ScenarioPresenter.getInstance().getChosenSchedule().getAlgorithmInformations());
			this.view.getListGrid().addData(
					new ScheduleAlternativesRecord(schedule, isTheSameAsTheChosen)
			);
		}
		
		String[] algorithmStrings = new String[alternatives.size()];
		int i=0;
		for(Schedule schedule : this.alternatives) {
			algorithmStrings[i++] = schedule.getAlgorithmInformations().getAlgorithmName();
		}
		
		List<KeyFigure> keyFigureList = ScenarioPresenter.getInstance().getKeyFigureList();
		String[] keyFigureStrings = new String[keyFigureList.size()];
		int j=0;
		for(KeyFigure keyFigure : keyFigureList) {
			keyFigureStrings[j++] = keyFigure.getName();
		}

		ComparisonChartRecord[] comparisonChartRecords = new ComparisonChartRecord[alternatives.size()];
		int[] values;
		int k=0, l=0;
		for(Schedule schedule : this.alternatives) {
			values = new int[keyFigureStrings.length];
			for(KeyFigure kf : keyFigureList) {
				try {
					values[l++] = kf.calculate(ScenarioPresenter.getInstance().getScenario(), schedule);
				} catch (NotPlannedException e) {
					e.printStackTrace();
				}
			}
			comparisonChartRecords[k++] = new ComparisonChartRecord(schedule.getAlgorithmInformations().getAlgorithmName(), values);
			l=0;
		}
		
		this.view.getChart().setContent(algorithmStrings, keyFigureStrings);
		this.view.getChart().setData(comparisonChartRecords);
		this.view.getChart().draw(Integer.valueOf(view.getComparedKeyFigureChooser().getValueAsString()));
		
	}
	
	/**
	 * Hinzufügen der Listener
	 */
	private void addListeners() {
		this.view.getComparedKeyFigureChooser().addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				view.getChart().draw(Integer.valueOf(event.getValue().toString()));
			}
		});
		
		this.view.getShowKeyFiguresButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Schedule schedule = ((ScheduleAlternativesRecord)(view.getListGrid().getSelectedRecord())).getSchedule();
				
				String keyFigureString = "";
				List<KeyFigure> keyFigureList = ScenarioPresenter.getInstance().getKeyFigureList();
				for(int i=0; i<keyFigureList.size(); i++) {
					try {
						//FIXME: Nicht errechnen sondern gespeicherten Wert nehmen
						keyFigureString += keyFigureList.get(i).getName() + ": " + keyFigureList.get(i).calculate(ScenarioPresenter.getInstance().getScenario(), schedule) + "</br>";
					} catch (NotPlannedException e) {
						e.printStackTrace();
					}
				}
				
				SC.say("Key figures for " + schedule.getAlgorithmInformations().getAlgorithmName(), 
						keyFigureString);
			}
		});
		
		/**
		 * Anzeige einer Alternative
		 */
		this.view.getExamineButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				new SchedulePopupPresenter(ScenarioPresenter.getInstance().getScenario(), ((ScheduleAlternativesRecord)(view.getListGrid().getSelectedRecord())).getSchedule(), SchedulePopupPresenter.TYPE_ALTERNATIVE);
			}
		});
		
		/**
		 * Wählen einer Alternative
		 */
		this.view.getElectButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final Schedule selectedAlternative = ((ScheduleAlternativesRecord)(view.getListGrid().getSelectedRecord())).getSchedule();
				long milliesToEndOfChangeDeadline = ScenarioPresenter.getInstance().getScenario().getScheduleChangeDeadline()*60*1000 - (new Date().getTime() - selectedAlternative.getStart().getTime());
				
				if(view.getListGrid().getSelectedRecord().getAttributeAsBoolean("isMarked")) {
					SC.say("This schedule is selected already.");
					return;
				}
				
				if(milliesToEndOfChangeDeadline<0) {
					SC.say("Schedule change not possible.<br />" +
						   "Schedule is too old. Change deadline elapsed. As a consequence, this may result in inconsistency.");
					return;
				}

				int timeLeftInMin = (int)(milliesToEndOfChangeDeadline/1000/60);
				int timeLeftInSec = (int)(milliesToEndOfChangeDeadline/1000%60);
				//TODO: Besser formulieren
				SC.ask(new Date() +" " +selectedAlternative.getStart() +" " +timeLeftInMin + " minutes and " + timeLeftInSec + " seconds left.<br />Are you sure to perform a schedule change?", new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value) {
							ScheduleChangeByUserEvent event = new ScheduleChangeByUserEvent(ScenarioPresenter.getInstance().getScenario().getScenarioID(), ScenarioPresenter.getInstance().getChosenSchedule(), selectedAlternative, new Date());
							ServerCommunicationsManager.getInstance().addScheduleEvent(
									event, 
									new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {
											SC.say("Schedule change failed: "+caught);
										}
			
										@Override
										public void onSuccess(Void result) {
											SC.showPrompt("Processing schedule change...<br />" +
													"This might take a few seconds. Please wait.");
										}
									}
							);
						}
					}
				});
			}
		});
		
		/**
		 * Selektion einer Alternative (Aktivierung der Buttons)
		 */
		this.view.getListGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				view.getExamineButton().setDisabled(false);
				view.getElectButton().setDisabled(false);
				view.getShowKeyFiguresButton().setDisabled(false);
			}
		});
		
		this.view.addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClientEvent event) {
				view.hide();
				view.getChart().setVisible(false);
			}
		});
	}

	public SchedulesAlternativesView getView() {
		return view;
	}
	
	/**
	 * Record für einen Alternativplan
	 * @author Dennis
	 */
	private class ScheduleAlternativesRecord extends ListGridRecord {
		/**
		 * Konstruktor
		 * @param schedule Schedule
		 * @param marked true, wenn gewählte Alternative
		 */
		public ScheduleAlternativesRecord(Schedule schedule, boolean marked) {
			String bigStart="", bigEnd="";
			if(marked) {
				bigStart = "<b>";
				bigEnd = "</b>";
			}
			
			this.setAttribute("schedule", schedule);
			this.setAttribute("isMarked", marked);
			
			this.setAttribute("creationDate", bigStart+schedule.getAlgorithmInformations().getDueTime()+bigEnd);
			this.setAttribute("creator", bigStart+schedule.getAlgorithmInformations().getAlgorithmName()+bigEnd);
			this.setAttribute("creationDuration", bigStart+schedule.getAlgorithmInformations().getDurationInMS()+"ms"+bigEnd);
		}
		
		public Schedule getSchedule() {
			return (Schedule) this.getAttributeAsObject("schedule");
		}
	}
	
	public class ComparisonChartRecord {
		private final int[] keyFigureValues;
		
		public ComparisonChartRecord(String algorithm, int[] keyFigureValues) {
			this.keyFigureValues = keyFigureValues;
		}
		
		
		public int getValue(int keyFigure) {
			return keyFigureValues[keyFigure];
		}
		
		public int getKeyFigureAmount() {
			return keyFigureValues.length;
		}
		
		public int[] getKeyFigureValues() {
			return keyFigureValues;
		}
	}
}
