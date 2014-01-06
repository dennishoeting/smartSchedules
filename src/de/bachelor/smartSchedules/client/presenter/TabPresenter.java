package de.bachelor.smartSchedules.client.presenter;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.AnimationCallback;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

import de.bachelor.smartSchedules.client.presenter.LogInPresenter.ScenarioRecord;
import de.bachelor.smartSchedules.client.view.CreateNewScenarioView;
import de.bachelor.smartSchedules.client.view.LogInView;
import de.bachelor.smartSchedules.client.view.RegistrationView;
import de.bachelor.smartSchedules.client.view.StartingWindow;
import de.bachelor.smartSchedules.client.view.TabView;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.ScenarioWrapper;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.util.User;
import de.novanic.eventservice.client.event.RemoteEventServiceFactory;

/**
 * Hauptpresenter für das TabManagement (Singleton)
 * 
 * @author Dennis
 */
public class TabPresenter {
	/**
	 * zugehöriger View
	 */
	private TabView view;

	/**
	 * Anfangsfenster (EntryPoint)
	 */
	private StartingWindow startingWindow;

	/**
	 * Definition der Presenter Zugehöriger Presenter (LogIn)
	 */
	private LogInPresenter logInPresenter;

	/**
	 * Zugehöriger Presenter (Registration)
	 */
	private RegistrationPresenter registrationPresenter;

	/**
	 * Zugehöriger Presenter (CreateNewScenario)
	 */
	private CreateNewScenarioPresenter createNewScenarioPresenter;

	/**
	 * Zugehöriger Presenter (About)
	 */
	private AboutPresenter aboutPresenter;

	/**
	 * Zugehöriger TabPresenter (Schedule)
	 */
	private ScheduleTabPresenter scheduleTabPresenter;

	/**
	 * Zugehöriger TabPresenter (Event)
	 */
	private EventTabPresenter eventTabPresenter;

	/**
	 * Zugehöriger TabPresenter (Settings)
	 */
	private SettingsTabPresenter settingsTabPresenter;

	/**
	 * Hilfstabs
	 */
	private Tab scheduleTab, eventTab, settingsTab, logOutTab;

	/**
	 * Hilfsvariable zur Zwishcenspeicherung des Scenarios
	 */
	private Scenario tempScenarioResult;

	private boolean loggedIn;

	/**
	 * Singleton-Instanz
	 */
	private static final TabPresenter instance = new TabPresenter();

	/**
	 * Konstruktor
	 */
	private TabPresenter() {
		this.view = new TabView();

		this.startingWindow = new StartingWindow();

		this.logInPresenter = new LogInPresenter();
		this.registrationPresenter = new RegistrationPresenter();
		this.aboutPresenter = new AboutPresenter();

		this.startingWindow.setWidth(LogInView.WIDTH);
		this.startingWindow.setHeight(LogInView.HEIGHT);
		this.startingWindow.addChilds(logInPresenter.getView(),
				registrationPresenter.getView(), aboutPresenter.getView());
		this.startingWindow.visibleCanvas(logInPresenter.getView());
		this.startingWindow.show();

		this.addListeners();
	}

	/**
	 * Hinzufügen der Listener
	 */
	private void addListeners() {
		com.google.gwt.user.client.Window
				.addWindowClosingHandler(new ClosingHandler() {
					@Override
					public void onWindowClosing(ClosingEvent event) {
						if (loggedIn)
							performLogOut();
					}
				});

		/**
		 * Bei Click auf About
		 */
		this.view.getAboutButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				aboutPresenter.getView().animateFade(100,
						new AnimationCallback() {
							@Override
							public void execute(boolean earlyFinish) {
							}
						}, 500);
				aboutPresenter.getView().show();
			}
		});

		/**
		 * Bei Click auf Help
		 */
		this.startingWindow.getHelpHeaderControl().addClickHandler(
				new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						aboutPresenter.getView().animateFade(100,
								new AnimationCallback() {
									@Override
									public void execute(boolean earlyFinish) {
									}
								}, 500);
						aboutPresenter.getView().show();
					}
				});
	}

	/**
	 * Bei LogIn
	 */
	public void logInClicked() {
		final int selectedScenarioID;

		/*
		 * Falls Checkbox zum Erstellen eines neuen Scenarios NICHT
		 */
		if (!TabPresenter.getInstance().getLogInPresenter().getView()
				.getCheckCreateNewScenario().getValueAsBoolean()) {
			/*
			 * Prüfung, ob Scenario gewählt
			 */
			if (TabPresenter.getInstance().getLogInPresenter().getView()
					.getScenarioGrid().getSelectedRecord() != null) {
				selectedScenarioID = ((ScenarioRecord) (TabPresenter
						.getInstance().getLogInPresenter().getView()
						.getScenarioGrid().getSelectedRecord()))
						.getScenarioID();
			} else {
				SC.say("Please select a scenario");
				return;
			}
		} else {
			/*
			 * Neues Scenario: DummiWert für selectedScenario
			 */
			selectedScenarioID = -1;
		}

		/*
		 * Login-Daten validieren (ohne Server/DB-Abgleich)
		 */
		if (this.getLogInPresenter().getView().getDynamicForm1().validate()) {
			/*
			 * LogIn-Button disabled zur Fehlervermeidung
			 */
			logInPresenter.getView().getLogInButton().setDisabled(true);

			/*
			 * LogIn-Daten-Validierung mit DB/Server
			 */
			String name = logInPresenter.getView().getName().getValueAsString();
			String pass = logInPresenter.getView().getPassword()
					.getValueAsString();
			// FIXME: Verschlüsseln!
			// try {
			// MessageDigest md = MessageDigest.getInstance("SHA-1");
			// String cryptPass = new String(md.digest(pass.getBytes()));

			ServerCommunicationsManager.getInstance().logIn(name, pass,
					new AsyncCallback<User>() {
						@Override
						public void onFailure(Throwable caught) {
							SC.say("LogIn failed: " + caught);
							logInPresenter.getView().getLogInButton()
									.setDisabled(false);
						}

						@Override
						public void onSuccess(User user) {
							UserPresenter.getInstance().setUser(user);

							/*
							 * Cookie-Management
							 */
							Cookies.setCookie("name", logInPresenter.getView().getName().getValueAsString());
							Cookies.setCookie("pass", logInPresenter.getView().getPassword().getValueAsString());

							/*
							 * Fallunterscheidung IF: Neues Schenario ELSE: In
							 * selektiertes Scenario einloggen
							 */
							if (logInPresenter.getView()
									.getCheckCreateNewScenario()
									.getValueAsBoolean()) {
								showCreateNewScenario(user);
							} else {
								Cookies.setCookie("selectedScenario",
										selectedScenarioID + "");

								performLogin(user, selectedScenarioID);

								// LogIn Event Listener entfernen:
								RemoteEventServiceFactory
										.getInstance()
										.getRemoteEventService()
										.removeListeners(
												LogInPresenter.LOGIN_PRESENTER_DOMAIN);
							}
						}
					});
			// } catch (NoSuchAlgorithmException e) {
			// e.printStackTrace();
			// }
		}
	}

	/**
	 * Bei Erstellung eines neuen Scenarios
	 */
	public void showCreateNewScenario(User user) {
		this.createNewScenarioPresenter = new CreateNewScenarioPresenter(user);

		this.startingWindow.unvisibleAll();
		this.startingWindow.animateResize(CreateNewScenarioView.WIDTH,
				CreateNewScenarioView.HEIGHT, new AnimationCallback() {
					@Override
					public void execute(boolean earlyFinish) {
						startingWindow.visibleCanvas(createNewScenarioPresenter
								.getView());
					}
				}, 500);
	}

	/**
	 * Bei letztendlichem Login
	 */
	public void performLogin(User user, int selectedScenarioID) {
		/**
		 * Serveranfrage nach Scenariodaten
		 */
		ServerCommunicationsManager.getInstance().getScenario(
				selectedScenarioID, user.getUserID(),
				new AsyncCallback<ScenarioWrapper>() {
					@Override
					/**
					 * Bei Fehlschlag: Fehler als Hinweis
					 */
					public void onFailure(Throwable caught) {
						SC.say("Scenario could not be received");
						logInPresenter.getView().getLogInButton()
								.setDisabled(false);
					}

					@Override
					/**
					 * Bei Erfolg:
					 * 1. Animation
					 * 2. Laden der Komponenten
					 */
					public void onSuccess(ScenarioWrapper result) {
						loggedIn = true;

						/*
						 * Flag in ScnearioPresenter schreiben
						 */
						UserPresenter.getInstance().setAdvancedUser(
								result.isAdvancedUser());

						/*
						 * Login
						 */
						tempScenarioResult = result.getScenario();
						startingWindow.animateFade(0, new AnimationCallback() {
							@Override
							public void execute(boolean earlyFinish) {
								startingWindow.destroy();
								view.getLoadingCanvas().animateRect(350, 250, 200, 100, new AnimationCallback() {
									@Override
									public void execute(boolean earlyFinish) {
										/*
										 * Scenario in ScenarioPresenter
										 * speichern
										 */
										ScenarioPresenter.getInstance().putScenario(tempScenarioResult);

										/*
										 * Presenter erstellen
										 */
										scheduleTabPresenter = new ScheduleTabPresenter();
										eventTabPresenter = new EventTabPresenter();
										settingsTabPresenter = new SettingsTabPresenter();

										/*
										 * OldSchedules aus DB an
										 * ScheduleHistory und
										 * EventHistory verteilen
										 */
										int offset = 0;
										int limit = 10;
										ServerCommunicationsManager.getInstance().getSchedules(
											ScenarioPresenter.getInstance().getScenario().getScenarioID(),
											offset,
											limit,
											new AsyncCallback<List<Schedule>>() {
												@Override
												public void onSuccess(List<Schedule> result) {
													/*
													 * Senden der OldSchedules an die EventHostory
													 */
													eventTabPresenter.getEventHistoryPresenter().insertFromDB(result);
													GWT.log(result.size()+"");
														
													/*
													 * Senden der OldSchedules an ScheduleHistory
													 * Hier ist der aktuelle Ablaufplan enthalten.
													 * Dieser muss abgezogen werden, da er im
													 * ScheduleHistoryPresenter hinzugefügt wird (in fett)
													 */
													//Result leer
													if(result.size()>=2) {
														result.remove(0);
														scheduleTabPresenter.getScheduleHistoryPresenter().insertFromDB(result);
													} 
													//Nur initialer Plan
													else if(result.size()>0) {
														scheduleTabPresenter.getScheduleHistoryPresenter().update();
													}
																		
												}

												@Override
												public void onFailure(Throwable caught) {
													SC.say("Old schedules not received from database.");
												}
											}
										);

										/*
										 * Tabs für TabSet der Presenter
										 * erstellen
										 */
										scheduleTab = new Tab("Schedules");
										eventTab = new Tab("Events");
										settingsTab = new Tab("Settings");
										logOutTab = new Tab("Log out");
										logOutTab.addTabSelectedHandler(new TabSelectedHandler() {
											@Override
											public void onTabSelected(TabSelectedEvent event) {
												SC.ask("Are you sure?", new BooleanCallback() {
													@Override
													public void execute(Boolean value) {
														if (value) {
															Window.Location.reload();
														} else {
															TabPresenter.this.view.selectTab(scheduleTab);
															TabPresenter.getInstance().getScheduleTabPresenter().getView().selectTab(scheduleTabPresenter.getCurrentSchedulePresenter().getView());
														}
													}
												});
											}
										});

										/*
										 * TabSets der Presenter in die
										 * Tabs packen
										 */
										scheduleTab.setPane(scheduleTabPresenter.getView());
										eventTab.setPane(eventTabPresenter.getView());
										settingsTab.setPane(settingsTabPresenter.getView());

										if (!UserPresenter.getInstance().isAdvancedUser()) {
											settingsTab.setDisabled(true);
											SC.say("Another client is logged in to the chosen scenario. Advanced access denied.");
										}

										/*
										 * Tabs hinzufügen
										 */
										view.addTab(scheduleTab, 0);
										view.addTab(eventTab, 1);
										view.addTab(settingsTab, 2);
										view.addTab(logOutTab, 3);
												
										view.getAboutButton().setOpacity(100);
										view.getLoadingCanvas().hide();
									}
								}, 1000);
							}
						}, 1000);
					}
				});

		/*
		 * Auf jetzt scrollen
		 */
		if (ScenarioPresenter.getInstance().getChosenSchedule() != null) {
			this.scheduleTabPresenter
					.getCurrentSchedulePresenter()
					.getView()
					.getGantt()
					.scrollToItem(
							ScenarioPresenter
									.getInstance()
									.getChosenSchedule()
									.getAllocationNow(
											ScenarioPresenter.getInstance()
													.getScenario()));
		}
	}

	public void performLogOut() {
		ServerCommunicationsManager.getInstance().logOut(
				ScenarioPresenter.getInstance().getScenario().getScenarioID(),
				UserPresenter.getInstance().getUser().getUserID(),
				new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Logout failed");
					}

					@Override
					public void onSuccess(Void result) {
						GWT.log("Logout succeeded");
					}
				});
	}

	/**
	 * Zu Registrierung "morphen"
	 */
	public void showRegistration() {
		this.startingWindow.unvisibleAll();
		this.startingWindow.animateResize(RegistrationView.WIDTH,
				RegistrationView.HEIGHT, new AnimationCallback() {
					@Override
					public void execute(boolean earlyFinish) {
						startingWindow.visibleCanvas(registrationPresenter
								.getView());
					}
				}, 500);
	}

	/**
	 * Zu LogIn "morphen"
	 */
	public void showLogIn() {
		this.startingWindow.unvisibleAll();
		this.startingWindow.animateResize(LogInView.WIDTH, LogInView.HEIGHT,
				new AnimationCallback() {
					@Override
					public void execute(boolean earlyFinish) {
						logInPresenter.getView().getScenarioGrid()
								.setDisabled(false);
						logInPresenter.getView().getLogInButton()
								.setDisabled(false);

						startingWindow.visibleCanvas(logInPresenter.getView());
					}
				}, 500);
	}

	public static TabPresenter getInstance() {
		return TabPresenter.instance;
	}

	public ScheduleTabPresenter getScheduleTabPresenter() {
		return this.scheduleTabPresenter;
	}

	public EventTabPresenter getEventTabPresenter() {
		return this.eventTabPresenter;
	}

	public SettingsTabPresenter getEvaluationTabPresenter() {
		return this.settingsTabPresenter;
	}

	public LogInPresenter getLogInPresenter() {
		return logInPresenter;
	}

	public TabView getView() {
		return view;
	}
}
