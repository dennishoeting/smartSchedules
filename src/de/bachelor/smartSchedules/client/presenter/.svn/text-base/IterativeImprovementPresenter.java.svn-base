package de.bachelor.smartSchedules.client.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.AnimationCallback;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.events.RightMouseDownEvent;
import com.smartgwt.client.widgets.events.RightMouseDownHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import de.bachelor.smartSchedules.client.view.IterativeImprovementView;
import de.bachelor.smartSchedules.client.view.KeyFigureTrendChart;
import de.bachelor.smartSchedules.client.view.util.KeyFigureCanvas;
import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleChangeByUserEvent;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;

/**
 * Presenter für die iterative Verbesserung
 * @author Dennis
 */
public class IterativeImprovementPresenter {
	/**
	 * zugehöriger View
	 */
	private final IterativeImprovementView view;
	
	/**
	 * Hilfsliste mit KeyFigureCanvasses
	 */
	private final List<KeyFigureCanvas> keyFigureCanvasses;
	
	/**
	 * Hilfsmaps
	 */
	private final LinkedHashMap<String, String> wrapperMap, changerMap, keyFigureMap;

	private Schedule improvedSchedule;
	
	/**
	 * Hilfsvariablen
	 */
	private boolean threadAlive, isFinished;
//	private boolean dragging;
	
//	private int xPosForDrag, yPosForDrag;
//	private int xStartPosForDrag, yStartPosForDrag;
	
	/**
	 * temporär
	 */
	public static final int DEFAULT_ALGORITHM_ID = 0;
	
	/**
	 * Befinden sich auch noch in @look Wrapper.java
	 */
	public static final int SELECTED = 1,
							LOCKED = -1,
							NOTHING = 0;
	
	/**
	 * Konstruktor
	 */
	public IterativeImprovementPresenter() {
		this.view = new IterativeImprovementView();
		this.wrapperMap = new LinkedHashMap<String, String>();
		this.changerMap = new LinkedHashMap<String, String>();
		this.keyFigureMap = new LinkedHashMap<String, String>();
		
//		this.xPosForDrag = -1;
//		this.yPosForDrag = -1;

		keyFigureCanvasses = new ArrayList<KeyFigureCanvas>();
		
		ServerCommunicationsManager.getInstance().getKeyFigures(new AsyncCallback<List<KeyFigure>>() {
			@Override
			/**
			 * Bei Fehlschlag: Ausgabe in Konsole
			 */
			public void onFailure(Throwable caught) {
				GWT.log("Failure in DefineEvaluationPresenter");
				GWT.log("caught: " + caught.getMessage());
			}

			@Override
			/**
			 * Bei Erfolg
			 */
			public void onSuccess(List<KeyFigure> result) {
				/*
				 * KeyFigureCanvasses hinzufügen
				 */
				KeyFigureCanvas keyFigureCanvas;
				for(KeyFigure kf : result) {
					keyFigureCanvas = view.generateKeyFigureCanvas(kf);
					
					view.addKeyFigureCanvasses(keyFigureCanvas);
					keyFigureCanvasses.add(keyFigureCanvas);
				}
				
				/*
				 * Listener hinzufügen
				 */
				for(KeyFigureCanvas c : keyFigureCanvasses) {
					c.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							for(KeyFigureCanvas c : keyFigureCanvasses) {
								if(c.isSelected())
									c.changeSelectionState();
							}
							
							((KeyFigureCanvas)(event.getSource())).changeSelectionState();
						}
					});
					
					c.addRightMouseDownHandler(new RightMouseDownHandler() {
						@Override
						public void onRightMouseDown(RightMouseDownEvent event) {
							((KeyFigureCanvas)(event.getSource())).changeLockingState();
						}
					});
				}
				
				/*
				 * Standardeinstellung: erste Kennzahl ist Selektiert
				 */
				keyFigureCanvasses.get(0).setSelected(true);
			}
		});
		
		/*
		 * Hilfsmap füllen
		 */
		
		// Wrapper holen:
		ServerCommunicationsManager.getInstance().getIterativeImprovementWrapperNames(new AsyncCallback<Map<Integer,String>>() {
			@Override
			public void onFailure(Throwable caught) {
				SC.say("Wrapper-Algorithms not received from server");
			}

			@Override
			public void onSuccess(Map<Integer, String> result) {
				for(Integer key : result.keySet()) {
					wrapperMap.put("" +key, result.get(key));
					IterativeImprovementPresenter.this.view.getWrapperChooser().setValueMap(wrapperMap);
					IterativeImprovementPresenter.this.view.getWrapperChooser().setValue(0);
					IterativeImprovementPresenter.this.view.getWrapperChooser().setDisabled(wrapperMap.size()<=1);
				}
			}
		
		
		});
		
		// Changer holen:
		ServerCommunicationsManager.getInstance().getIterativeImprovementChangerNames(new AsyncCallback<Map<Integer,String>>() {

			@Override
			public void onFailure(Throwable caught) {
				SC.say("Changer-Algorithms not received from server");
			}

			@Override
			public void onSuccess(Map<Integer, String> result) {
				for(Integer key : result.keySet()) {
					changerMap.put("" +key, result.get(key));
					IterativeImprovementPresenter.this.view.getChangerChooser().setValueMap(changerMap);
					IterativeImprovementPresenter.this.view.getChangerChooser().setValue(0);
					IterativeImprovementPresenter.this.view.getChangerChooser().setDisabled(changerMap.size()<=1);
				}
				
			}
		});

		/*
		 * Hilfsmap füllen
		 */
		this.keyFigureMap.put(KeyFigureTrendChart.ALL+"", "All");
		for(KeyFigure kf : ScenarioPresenter.getInstance().getScenario().getKeyFigureList()) {
			this.keyFigureMap.put(kf.getId()+"", kf.getName());
		}
		this.view.getPopup().getKeyFigureChooser().setValueMap(keyFigureMap);
		this.view.getPopup().getKeyFigureChooser().setValue(KeyFigureTrendChart.ALL);
		
		this.addListeners();
		
		/*
		 * Gantt füllen
		 */
		this.fillGanttComponents();
	}
	
	/**
	 * Füllt Gantt-Komponenten mit aktuellen Daten aus Schedule
	 */
	public void fillGanttComponents() {
		fillGanttGrid(ScenarioPresenter.getInstance().getChosenSchedule());
		view.getGantt().redraw();
		fillResourceList(ScenarioPresenter.getInstance().getChosenSchedule());
	}
	
	/**
	 * Maschinenliste füllen
	 * @param schedule Ablaufplan
	 */
	public void fillResourceList(Schedule schedule) {
		/*
		 * Record-Feld für GanttGridRecords
		 */
		ResourceListRecord[] data = new ResourceListRecord[schedule.getResources(ScenarioPresenter.getInstance().getScenario()).size()];
		
		/*
		 * Resourcen holen und nach ID ordnen
		 */
		List<Resource> resourceList = new ArrayList<Resource>(schedule.getResources(ScenarioPresenter.getInstance().getScenario()).keySet());
		Collections.sort(resourceList, new Comparator<Resource>() {
			@Override
			public int compare(Resource o1, Resource o2) {
				if(o1.equals(o2))
					return 0;
				if(o1.getResourceID() < o2.getResourceID()) 
					return -1;
				return 1;
			}
		});
		
		/*
		 * Record-Feld füllen
		 */
		for(int i=0; i<data.length; i++) {
			data[i] = new ResourceListRecord(resourceList.get(i).getName());
		}
		
		this.view.getResourceListGrid().setData(data);
	}
	
	/**
	 * Gantt-Diagramm füllen
	 * @param schedule Ablaufplan
	 * @see GanttManager
	 */
	public void fillGanttGrid(Schedule schedule) {
		view.getDataProvider().setList(GanttManager.visualizeSchedule(ScenarioPresenter.getInstance().getScenario(), schedule));
	}
	
	/**
	 * Hinzufügen der Listener
	 */
	private void addListeners() {

		this.view.getPopup().getShowKeyFiguresButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String keyFigureString = "";
				for(KeyFigure kf : ScenarioPresenter.getInstance().getKeyFigureList()) {
					keyFigureString += kf.getName() + ": " + improvedSchedule.getKeyFigureValueMap().get(kf.getId()) + "</br>";
				}
				SC.say("Key figures for " + improvedSchedule.getAlgorithmInformations().getAlgorithmName(), 
						keyFigureString);
			}
		});
		
		/**
		 * Anzeige einer Alternative
		 */
		this.view.getPopup().getShowImprovedScheduleButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				new SchedulePopupPresenter(ScenarioPresenter.getInstance().getScenario(), improvedSchedule, SchedulePopupPresenter.TYPE_ALTERNATIVE);
			}
		});
		
		/**
		 * Wählen einer Alternative
		 */
		this.view.getPopup().getElectImprovedScheduleButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				long milliesToEndOfChangeDeadline = ScenarioPresenter.getInstance().getScenario().getScheduleChangeDeadline()*60*1000 - (new Date().getTime() - improvedSchedule.getStart().getTime());
				
				if(milliesToEndOfChangeDeadline<0) {
					SC.say("Schedule change not possible.<br />" +
						   "Schedule is too old. Change deadline elapsed. As a consequence, this may result in inconsistency.");
					return;
				}

				int timeLeftInMin = (int)(milliesToEndOfChangeDeadline/1000/60);
				int timeLeftInSec = (int)(milliesToEndOfChangeDeadline/1000%60);
				//TODO: Besser formulieren
				SC.ask(new Date() +" " +improvedSchedule.getStart() +" " +timeLeftInMin + " minutes and " + timeLeftInSec + " seconds left.<br />Are you sure to perform a schedule change?", new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value) {
							ScheduleChangeByUserEvent event = new ScheduleChangeByUserEvent(ScenarioPresenter.getInstance().getScenario().getScenarioID(), ScenarioPresenter.getInstance().getChosenSchedule(), improvedSchedule, new Date());
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
		
		this.view.getPopup().getUpdateSpinner().addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if(Integer.valueOf(event.getValue().toString()) > 0) {
					view.getPopup().getUpdateSpinner().setValue(event.getValue());
				} else {
					view.getPopup().getUpdateSpinner().setValue(1);
				}
			}
		});
		
		/*
		 * Bei Wechsel der zu visualisierenden Kennzahl in Chart
		 */
		this.view.getPopup().getKeyFigureChooser().addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				view.getPopup().getChart().changeVisualizedKeyFigures(Integer.valueOf((String) event.getValue()));
			}
		});
		
		/*
		 * Bei Start der iterativen Verbesserung
		 */
		this.view.getStartImprovementButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final int startTimeInSec = Integer.valueOf(view.getDurationChooser().getValueAsString())*60;
				
				/*
				 * Chart initialisieren
				 */
				view.getPopup().getChart().initialize(startTimeInSec, ScenarioPresenter.getInstance().getScenario().getKeyFigureList());
				
				/*
				 * Fenster anzeigen
				 */
				view.getPopup().show();
				view.getPopup().redraw();
				view.getPopup().animateResize(view.getPopup().WIDTH, view.getPopup().HEIGHT, new AnimationCallback() {
					@Override
					public void execute(boolean earlyFinish) {
						/*
						 * KeyFigureMap und KeyFigureFlexibilityMap füllen
						 * KeyFigureMap mit den Kennzahlen
						 * KeyFigureFlexibilityMap für die durch den Nutzer eingestellte Flexibilität (grün/rot)
						 */
						HashMap<Integer, Integer> keyFigureFlexibilityMap = new HashMap<Integer, Integer>();
						HashMap<Integer, KeyFigure> keyFiguresMap = new HashMap<Integer, KeyFigure>();
						for(KeyFigureCanvas kf : keyFigureCanvasses) {
							keyFigureFlexibilityMap.put(kf.getKeyFigure().getId(), (kf.isSelected())?SELECTED:(kf.isLocked()?LOCKED:NOTHING));
							keyFiguresMap.put(kf.getKeyFigure().getId(), kf.getKeyFigure());
						}
						
						/*
						 * Improvement starten
						 */
						ServerCommunicationsManager.getInstance().startIterativeImprovement(ScenarioPresenter.getInstance().getScenario(), ScenarioPresenter.getInstance().getChosenSchedule(), startTimeInSec, keyFiguresMap, keyFigureFlexibilityMap, Integer.valueOf(String.valueOf(view.getWrapperChooser().getValue())), Integer.valueOf(String.valueOf(view.getChangerChooser().getValue())), new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								SC.say("Fail (startIterativeImprovement()).");
							}

							/*
							 * Wenn erfolgreich:
							 */
							@Override
							public void onSuccess(Void result) {
								/*
								 * Flag setzen
								 */
								threadAlive = true;
								
								/*
								 * Startzeit setzen
								 */
								final long startTimeInMillies = new Date().getTime();
								
								/*
								 * Timer definieren
								 */
								Timer t = new Timer() {
									@Override
									public void run() {
										/*
										 * Aktuelle Zeit
										 */
										final long timeInMillis = Math.abs(startTimeInMillies - (new Date().getTime()));
										
										/*
										 * Restzeit
										 */
										long timeLeftInMillies = startTimeInSec*1000 - timeInMillis;
										
										/*
										 * Zwischenergebnis abholen
										 */
										ServerCommunicationsManager.getInstance().getKeyFigureValues(ScenarioPresenter.getInstance().getScenario().getScenarioID(), new AsyncCallback<HashMap<Integer, Integer>>() {
											@Override
											public void onFailure(Throwable caught) {
												SC.say("Fail (getKeyFigureValues()).");
											}

											/*
											 * Wenn Ergebnisse erhalten
											 */
											@Override
											public void onSuccess(HashMap<Integer, Integer> result) {
												/*
												 * Konsistenz überprüfen
												 */
												if(result.keySet().size() == keyFigureCanvasses.size()) {
													/*
													 * Einzelne Ergebnisse an chart pushen
													 */
													for(int i=0; i<ScenarioPresenter.getInstance().getScenario().getKeyFigureList().size(); i++) {
														view.getPopup().getChart().push(timeInMillis/1000.0, result.get(i), ScenarioPresenter.getInstance().getScenario().getKeyFigureList().get(i));
													}
												}
											}
										});
										
										/*
										 * Restzeit in String
										 * an Titel und DetailViewer
										 */
										String timeLeftString = (int)(timeLeftInMillies/1000/60)+"m " + ((int)(timeLeftInMillies/1000)%60) + "s";
										Record r = new Record();
										r.setAttribute("timeLeft", timeLeftString);
										view.getPopup().getDetailViewer().setData(new Record[]{r});
										view.getPopup().getDetailViewer().selectRecord(r);
										view.getPopup().setTitle("Iterative Improvement: "+timeLeftString + " left.");
										
										/*
										 * Wenn Zeit übrig und nicht vom Nutzer beendet
										 */
										if(threadAlive && timeLeftInMillies>0) {
											this.schedule(Integer.valueOf(view.getPopup().getUpdateSpinner().getValueAsString())*1000);
										} else {
											threadAlive = false;
											isFinished = true;
											/*
											 * Improvement stoppen
											 */
											ServerCommunicationsManager.getInstance().stopIterativeImprovement(ScenarioPresenter.getInstance().getScenario().getScenarioID(), new AsyncCallback<Schedule>() {
												@Override
												public void onFailure(Throwable caught) {
													SC.say("Iterative improvement not stoppable. OH MY GOD!");
												}

												@Override
												public void onSuccess(Schedule result) {
													IterativeImprovementPresenter.this.improvedSchedule = result;
													
													IterativeImprovementPresenter.this.getView().getPopup().getShowKeyFiguresButton().setDisabled(false);
													IterativeImprovementPresenter.this.getView().getPopup().getShowImprovedScheduleButton().setDisabled(false);
													IterativeImprovementPresenter.this.getView().getPopup().getElectImprovedScheduleButton().setDisabled(false);
												
													IterativeImprovementPresenter.this.view.getStartImprovementButton().setDisabled(false);
												}
											});
										}
									}
								};
								
								t.schedule(1000);
							}
						});
					}
				}, 500);
			}
		});
		
		/**
		 * Schließen des Fensters
		 */
		this.view.getPopup().addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClientEvent event) {
				/*
				 * Eventuelle Abfrage, Thread sicher beenden
				 */
				if(isFinished) {
					IterativeImprovementPresenter.this.view.getStartImprovementButton().setDisabled(true);
					view.getPopup().animateResize(view.getPopup().SMALL_WIDTH, view.getPopup().SMALL_HEIGHT, new AnimationCallback() {
						@Override
						public void execute(boolean earlyFinish) {
							view.getPopup().hide();
							ServerCommunicationsManager.getInstance().stopIterativeImprovement(ScenarioPresenter.getInstance().getScenario().getScenarioID(), new AsyncCallback<Schedule>() {
								@Override
								public void onFailure(Throwable caught) {
									SC.say("Iterative Improvement may not be stopped. Contact server administrator to provice not serious damage.");
								}

								@Override
								public void onSuccess(Schedule result) {
								}
							});
							threadAlive = false;
						}
					}, 500);
				} else {
					SC.ask("Are you sure?", new BooleanCallback() {
						@Override
						public void execute(Boolean value) {
							if(value) {
								IterativeImprovementPresenter.this.view.getStartImprovementButton().setDisabled(true);
								isFinished = true;
								view.getPopup().animateResize(view.getPopup().SMALL_WIDTH, view.getPopup().SMALL_HEIGHT, new AnimationCallback() {
									@Override
									public void execute(boolean earlyFinish) {
										view.getPopup().hide();
										ServerCommunicationsManager.getInstance().stopIterativeImprovement(ScenarioPresenter.getInstance().getScenario().getScenarioID(), new AsyncCallback<Schedule>() {
											@Override
											public void onFailure(Throwable caught) {
												SC.say("Fail");
											}

											@Override
											public void onSuccess(Schedule result) {
											}
										});
										threadAlive = false;
									}
								}, 500);
							}
						}
					});
				}
			}
		});
		
		this.view.getPopup().getZoomInButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.getPopup().getChart().zoomInAction();
			}
		});
		
		this.view.getPopup().getZoomOutButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.getPopup().getChart().zoomOutAction();
			}
		});
		
		this.view.getPopup().getChart().addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				view.getPopup().getChart().mouseDownAction(event.getClientX(), event.getClientY());
			}
		});
		
		this.view.getPopup().getChart().addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				view.getPopup().getChart().mouseMoveAction(event.getClientX(), event.getClientY());
			}
		});
		
		this.view.getPopup().getChart().addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				event.preventDefault();
				view.getPopup().getChart().mouseUpAction(event.getClientX(), event.getClientY());
			}
		});
	}
	
	public IterativeImprovementView getView() {
		return this.view;
	}
	
	/**
	 * Record für die Resourcenliste
	 * @author Dennis
	 */
	private class ResourceListRecord extends ListGridRecord {
		private ResourceListRecord(String name) {
			this.setName(name);
		}
		
		public void setName(String name) {
			this.setAttribute("name", name);
		}

		@SuppressWarnings("unused")
		public String getName() {
			return this.getAttribute("name");
		}
	}
}