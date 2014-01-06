package de.bachelor.smartSchedules.server.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bachelor.smartSchedules.client.service.ScenarioManager;
import de.bachelor.smartSchedules.server.model.algorithm.improvement.changer.AllocationSwitcher;
import de.bachelor.smartSchedules.server.model.algorithm.improvement.changer.Changer;
import de.bachelor.smartSchedules.server.model.algorithm.improvement.changer.PlannedVariantReScheduler;
import de.bachelor.smartSchedules.server.model.algorithm.improvement.wrapper.Greedy;
import de.bachelor.smartSchedules.server.model.algorithm.improvement.wrapper.HillClimbing;
import de.bachelor.smartSchedules.server.model.algorithm.improvement.wrapper.SimpleWrapper;
import de.bachelor.smartSchedules.server.model.algorithm.improvement.wrapper.ThresholdAccepting;
import de.bachelor.smartSchedules.server.model.algorithm.improvement.wrapper.Wrapper;
import de.bachelor.smartSchedules.server.model.algorithm.predictive.EDDAlgorithmOrderOriented;
import de.bachelor.smartSchedules.server.model.algorithm.predictive.ESTAlgorithmOrderOriented;
import de.bachelor.smartSchedules.server.model.algorithm.predictive.PredictiveAlgorithm;
import de.bachelor.smartSchedules.server.model.algorithm.predictive.SPTAlgorithmOrderOriented;
import de.bachelor.smartSchedules.server.model.algorithm.reactive.EDDReactiveAlgorithmOrderOriented;
import de.bachelor.smartSchedules.server.model.algorithm.reactive.ESTReactiveAlgorithmOrderOriented;
import de.bachelor.smartSchedules.server.model.algorithm.reactive.ReactiveAlgorithm;
import de.bachelor.smartSchedules.server.model.algorithm.reactive.SPTReactiveAlgorithmOrderOriented;
import de.bachelor.smartSchedules.server.model.db.DBMethods;
import de.bachelor.smartSchedules.shared.model.exceptions.EMailAlreadyInUseException;
import de.bachelor.smartSchedules.shared.model.exceptions.NicknameAlreadyInUseException;
import de.bachelor.smartSchedules.shared.model.exceptions.NotPlannedException;
import de.bachelor.smartSchedules.shared.model.exceptions.WrongPasswordException;
import de.bachelor.smartSchedules.shared.model.schedule.Evaluation;
import de.bachelor.smartSchedules.shared.model.schedule.Operation;
import de.bachelor.smartSchedules.shared.model.schedule.Product;
import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.ScenarioInformations;
import de.bachelor.smartSchedules.shared.model.schedule.ScenarioWrapper;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleInformation;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleOrder;
import de.bachelor.smartSchedules.shared.model.schedule.Variant;
import de.bachelor.smartSchedules.shared.model.schedule.event.ChangeOperationResourcesEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ChangeOrderDueTimeEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ChangeOrderPriorityEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.MachineBreakDownEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.MachineRepairedEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewOrdersEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewProductsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewResourceEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveOrdersEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveOrdersProductsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveProductsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveVariantsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleChangeByUserEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleEvent;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.ChangedOperations;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.Lateness;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.MeanLateness;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.MissedDeadLines;
import de.bachelor.smartSchedules.shared.model.util.User;
import de.bachelor.smartSchedules.shared.model.util.event.ChangeKeyFiguresEvent;
import de.bachelor.smartSchedules.shared.model.util.event.ChangeScheduleChangeDeadlineEvent;
import de.bachelor.smartSchedules.shared.model.util.event.IncreaseScenarioIDsEvent;
import de.bachelor.smartSchedules.shared.model.util.event.ScenarioInformationGridEvent;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.service.RemoteEventServiceServlet;

/**
 * Der ScenarioManager
 * 
 * @author timo
 * 
 */
public class ScenarioManagerImpl extends RemoteEventServiceServlet implements
		ScenarioManager {

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 2987450584498156755L;
	private static Map<Integer, Scenario> scenarios = new HashMap<Integer, Scenario>();
	private static List<ScheduleEvent> upComingEventStack = new ArrayList<ScheduleEvent>();
	private static Thread eventThread = null;
	private static Object eventThreadWait = new Object();
	private static Domain logInPresenterDomain = DomainFactory.getDomain("LogIn-Events");
	
	/**
	 * Gibt an, ob sich ein User in einem Scenario befindet.
	 * Map<Integer = ScenarioID, Integer = UserID>
	 */
	private static Map<Integer, Integer> scenarioUserIDMap = new HashMap<Integer, Integer>();
	
	private static Map<Integer, Wrapper> wrapperMap = new HashMap<Integer, Wrapper>();

	/**
	 * Nutzer LogIn Daten Checken
	 * 
	 * @param nickname
	 * @param password
	 * @return
	 * @throws WrongPasswordException
	 * @throws SQLException 
	 */
	@Override
	public User logIn(String nickname, String password)
			throws WrongPasswordException {

		try {
			return DBMethods.getInstance().getUser(nickname, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		throw new WrongPasswordException();
	}

	/**
	 * Nutzer Registrieren
	 * @param nickname
	 * @param password
	 * @param givenname
	 * @param surname
	 * @return
	 * @throws NicknameAlreadyInUseException
	 * @throws EMailAlreadyInUseException 
	 */
	@Override
	public boolean registerUser(String nickname, String password, String eMail,
			String givenname, String surname)
			throws NicknameAlreadyInUseException, EMailAlreadyInUseException {

		// In die Datenbank eintragen:
		try {
			DBMethods.getInstance().insertUser(nickname, password, eMail, givenname, surname);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	@Override
	public ScenarioWrapper getScenario(int scenarioID, int userID) {

		if (scenarios.get(scenarioID) != null) {
			
			// Noch kein User im Scenario:
			
			if(scenarioUserIDMap.get(scenarioID) == null) {
				
				// Scenario ist jetzt beleget:
				scenarioUserIDMap.put(scenarioID, userID);
				
				// Auf allen LogIn Views als nicht mehr frei markieren:
				Scenario tmpScenario = scenarios.get(scenarioID);
				try {
					this.addEvent(logInPresenterDomain, new ScenarioInformationGridEvent(new ScenarioInformations(scenarioID, tmpScenario.getName(), 
							DBMethods.getInstance().getNicknameByID(tmpScenario.getAuthorID()), true)));
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				return new ScenarioWrapper(scenarios.get(scenarioID), true);
				
			// Schon ein User im Scenario:
			} else {
				return new ScenarioWrapper(scenarios.get(scenarioID), false);
			} 
		}
		
		// TODO falls irgendwas kaputt ist..
		// Szenario erstellen:
		Scenario scenario = new Scenario(scenarioID, 0, "Autoland");

		// Ressource erstellen:
		Resource resource = new Resource(scenarioID,
				scenario.getNewIncreasedResourceIDCount(),
				"Autoreifen Maschine", 100);
		Resource resource2 = new Resource(scenarioID,
				scenario.getNewIncreasedResourceIDCount(),
				"Fensterscheiben Maschine", 100);
		Resource resource3 = new Resource(scenarioID,
				scenario.getNewIncreasedResourceIDCount(), "Auspuff Maschine",
				100);
		Resource resource4 = new Resource(scenarioID,
				scenario.getNewIncreasedResourceIDCount(), "Lackier-Maschine",
				100);
		Resource resource5 = new Resource(scenarioID,
				scenario.getNewIncreasedResourceIDCount(),
				"Auto-zusammenbauen Maschine", 100);
		Resource resource6 = new Resource(scenarioID,
				scenario.getNewIncreasedResourceIDCount(), "Lackier-Maschine2",
				100);
		Resource resource7 = new Resource(scenarioID,
				scenario.getNewIncreasedResourceIDCount(),
				"Auto-zusammenbauen Maschine2", 100);

		// Produkt erstellen:
		Product product = new Product(0,
				scenario.getNewIncreasedProductIDCount(), "Auto");

		// Variante erstellen:
		product.getVariants().add(
				new Variant(scenario.getNewIncreasedVariantIDCount(),
						new ArrayList<Operation>(), product));

		// Operation erstellen:
		ArrayList<Integer> lackierResourcen = new ArrayList<Integer>();
		lackierResourcen.add(resource4.getResourceID());
		lackierResourcen.add(resource6.getResourceID());
		ArrayList<Integer> zusammenbauResourcen = new ArrayList<Integer>();
		zusammenbauResourcen.add(resource5.getResourceID());
		zusammenbauResourcen.add(resource7.getResourceID());
		Operation operation1 = new Operation(scenarioID,
				scenario.getNewIncreasedOperationIDCount(), 0, 1, product.getVariant(0).getVariantID(),
				"Reifen herstellen");
		Operation operation2 = new Operation(scenarioID,
				scenario.getNewIncreasedOperationIDCount(), 1, 60 * 48, product.getVariant(0).getVariantID(),
				"Fensterscheiben herstellen", operation1);
		Operation operation3 = new Operation(scenarioID,
				scenario.getNewIncreasedOperationIDCount(), 2, 60 * 15, product.getVariant(0).getVariantID(),
				"Auspuff herstellen", operation2);
		Operation operation4 = new Operation(scenarioID,
				scenario.getNewIncreasedOperationIDCount(), lackierResourcen,
				60 * 96, product.getVariant(0).getVariantID(), "Auto lackieren", operation3);
		Operation operation5 = new Operation(scenarioID,
				scenario.getNewIncreasedOperationIDCount(),
				zusammenbauResourcen, 60 * 128, product.getVariant(0).getVariantID(), "Auto zusammenbauen",
				operation4);
		
		product.getVariant(0).addOperation(operation1);
		product.getVariant(0).addOperation(operation2);
		product.getVariant(0).addOperation(operation3);
		product.getVariant(0).addOperation(operation4);
		product.getVariant(0).addOperation(operation5);

		// Aufträge:
		ScheduleOrder order1 = new ScheduleOrder(
				scenario.getNewIncreasedOrderIDCount(), scenarioID,
				new ArrayList<Product>(), "Auftrag: 1", new Date(), new Date(
						System.currentTimeMillis() + 4000000), 1);
		order1.addProduct(product);
		ScheduleOrder order2 = new ScheduleOrder(
				scenario.getNewIncreasedOrderIDCount(), scenarioID,
				new ArrayList<Product>(), "Auftrag: 2", new Date(), new Date(
						System.currentTimeMillis() + 2000000), 1);
		order2.addProduct(product);
		ScheduleOrder order3 = new ScheduleOrder(
				scenario.getNewIncreasedOrderIDCount(), scenarioID,
				new ArrayList<Product>(), "Auftrag: 3", new Date(), new Date(
						System.currentTimeMillis() + 3000000), 1);
		order3.addProduct(product);
		ScheduleOrder order4 = new ScheduleOrder(
				scenario.getNewIncreasedOrderIDCount(), scenarioID,
				new ArrayList<Product>(), "Auftrag: 4", new Date(), new Date(
						System.currentTimeMillis() + 9000000), 1);
		order4.addProduct(product);
		ScheduleOrder order5 = new ScheduleOrder(
				scenario.getNewIncreasedOrderIDCount(), scenarioID,
				new ArrayList<Product>(), "Auftrag: 5", new Date(), new Date(
						System.currentTimeMillis() + 1000000), 1);
		order5.addProduct(product);
		ScheduleOrder order6 = new ScheduleOrder(
				scenario.getNewIncreasedOrderIDCount(), scenarioID,
				new ArrayList<Product>(), "Auftrag: 6", new Date(), new Date(
						System.currentTimeMillis() + 30000000), 1);
		order6.addProduct(product);

		scenario.addResource(resource);
		scenario.addResource(resource2);
		scenario.addResource(resource3);
		scenario.addResource(resource4);
		scenario.addResource(resource5);
		scenario.addResource(resource6);
		scenario.addResource(resource7);

		scenario.addProduct(product);

		scenario.addOrder(order1);
		scenario.addOrder(order2);
		scenario.addOrder(order3);
		scenario.addOrder(order4);
		scenario.addOrder(order5);
		scenario.addOrder(order6);

		scenario.setKeyFigureList(getKeyFigures());

		/*
		 * Schedule erzeugen 
		 */
		Schedule schedule = new EDDAlgorithmOrderOriented().produceSchedule(scenario, 
									null, 
									scenario.getFirstPossibleDateAfterScheduleChangeDeadline());

		List<Schedule> tmpCurrentScheduleList = new ArrayList<Schedule>();
		
		// Event und KeyFigure bei den Schedules setzen:
		schedule.setKeyFigureValueMap(getScheduleKeyFigures(scenario, schedule));
		

		tmpCurrentScheduleList.add(schedule);
		scenario.setCurrentSchedules(tmpCurrentScheduleList);
		scenario.setChosenSchedule(schedule);

		 try {
			 DBMethods.getInstance().insertNewScenario(scenario);
		 } catch (SQLException e) {
			 e.printStackTrace();
		 }
		 
		scenarios.put(scenario.getScenarioID(), scenario);
		scenarioUserIDMap.put(scenario.getScenarioID(), userID);
		
		return new ScenarioWrapper(scenario, true);
	}


	/**
	 * Hier sind allePredictiven Algorithmen gespeichert.
	 * 
	 * @return
	 */
	private static List<PredictiveAlgorithm> getPredictiveAlgorithms() {
		List<PredictiveAlgorithm> predictiveAlgorithmList = new ArrayList<PredictiveAlgorithm>();

		// Hier neue Algorithmen einfuegen:
		predictiveAlgorithmList.add(new ESTAlgorithmOrderOriented());
		predictiveAlgorithmList.add(new SPTAlgorithmOrderOriented());
		predictiveAlgorithmList.add(new EDDAlgorithmOrderOriented());

		return predictiveAlgorithmList;
	}

	/**
	 * Hier sind alle reaktive Algorithmen gespeichert.
	 * 
	 * @return
	 */
	private static List<ReactiveAlgorithm> getReactiveAlgorithms() {
		List<ReactiveAlgorithm> reactiveAlgorithmList = new ArrayList<ReactiveAlgorithm>();

		// Hier neue Algorithmen einfuegen:
		reactiveAlgorithmList.add(new ESTReactiveAlgorithmOrderOriented());
		reactiveAlgorithmList.add(new SPTReactiveAlgorithmOrderOriented());
		reactiveAlgorithmList.add(new EDDReactiveAlgorithmOrderOriented());
		
		return reactiveAlgorithmList;
	}

	@Override
	public List<KeyFigure> getKeyFigures() {
		List<KeyFigure> keyFigureList = new ArrayList<KeyFigure>();

		// Hier neue KeyFigures eintragen:
		keyFigureList.add(new Lateness());
		keyFigureList.add(new MeanLateness());
		keyFigureList.add(new MissedDeadLines());
		keyFigureList.add(new ChangedOperations());

		return keyFigureList;
	}

	/**
	 * Neue Events in den UpComingEventStack hinzufügen. Statisch damit es
	 * server und client seitig erfolgen kann.
	 * 
	 * @param scheduleEvent
	 */
	public static synchronized void addEvent(ScheduleEvent scheduleEvent) {
//		if (scheduleEvent.getThrowTime().after(new Date())) {
			upComingEventStack.add(scheduleEvent);

			// Liste austeigend nach throwTime sortieren:
			Collections.sort(upComingEventStack,
					new Comparator<ScheduleEvent>() {

						@Override
						public int compare(ScheduleEvent o1, ScheduleEvent o2) {
							if (o1.getThrowTime().before(o2.getThrowTime())) {
								return -1;
							}

							if (o1.getThrowTime().equals(o2.getThrowTime())) {
								return 0;
							}
							return 1;
						}

					});

			// EventThread starten:
			startEventThread();

			// Mitteilen, dass er ausgeführt werden muss:
			synchronized (eventThreadWait) {
				eventThreadWait.notifyAll();
			}
//		}
	}

	public static synchronized void addEvents(List<ScheduleEvent> eventList) {

		upComingEventStack.addAll(eventList);

		// Liste austeigend nach throwTime sortieren:
		Collections.sort(upComingEventStack, new Comparator<ScheduleEvent>() {

			@Override
			public int compare(ScheduleEvent o1, ScheduleEvent o2) {
				if (o1.getThrowTime().before(o2.getThrowTime())) {
					return -1;
				}

				if (o1.getThrowTime().equals(o2.getThrowTime())) {
					return 0;
				}
				return 1;
			}

		});

		// EventThread starten:
		startEventThread();

		// Mitteilen, dass er ausgeführt werden muss:
		synchronized (eventThreadWait) {
			eventThreadWait.notifyAll();
		}
	}

	/**
	 * Startet den Thread, der die Events zur gegebenen Zeit wirft.
	 */
	private static void startEventThread() {
		if (eventThread == null) {
			eventThread = new Thread("EventThread") {
				@Override
				public void run() {

					while (true) {
						synchronized (eventThreadWait) {
							try {
								// Wenn es keine Events gibt, warten:
								if (upComingEventStack.isEmpty()) {
									eventThreadWait.wait();
								} else { // Ansonsten invokeEvent ausführen:
									if (upComingEventStack.get(0)
											.getThrowTime().getTime() <= new Date()
											.getTime()) {
										invokeEvent(upComingEventStack.get(0));
										upComingEventStack.remove(0);
									} else { // Wenn es zulange dauert:
												// schlafen:
										eventThreadWait.wait(upComingEventStack
												.get(0).getThrowTime()
												.getTime()
												- System.currentTimeMillis());
									}
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				};

			};

			eventThread.start();
		}
	}

	/**
	 * Führt ein Event aus.
	 * pattern -> Abbrechen bei Event.
	 * 
	 * @param scheduleEvent
	 */
	private static void invokeEvent(ScheduleEvent scheduleEvent) {

		synchronized (scenarios.get(scheduleEvent.getScenarioID())) {

			Scenario scenario = scenarios.get(scheduleEvent.getScenarioID());
			Date scheduleChangeDeadline = scenario
					.getFirstPossibleDateAfterScheduleChangeDeadline();
			List<Schedule> tmpScheduleList = new ArrayList<Schedule>();
			
			/*
			 * Falls das Event keine Veränderungen erzeugt -> nicht ausführen:
			 */
			if(scheduleEvent instanceof NewResourceEvent) {
				NewResourceEvent tmpEvent = ((NewResourceEvent)scheduleEvent);
				
				// Schauen, ob der Name schon belegt ist:
				for(Integer key : scenario.getResources().keySet()) {
					for(int i = 0; i < tmpEvent.getNewResources().size(); i++) {
						if(tmpEvent.getNewResources().get(i).getName().equals(scenario.getResources().get(key).getName())) {
							tmpEvent.getNewResources().remove(i);
							i--;
						}
					}
				}
				
			} else if(scheduleEvent instanceof NewProductsEvent) {
				NewProductsEvent tmpEvent = ((NewProductsEvent)scheduleEvent);
				
				// Schauen, ob der Name schon belegt ist:
				for(Integer key : scenario.getProducts().keySet()) {
					for(int i = 0; i < tmpEvent.getNewProducts().size(); i++) {
						if(tmpEvent.getNewProducts().get(i).getName().equals(scenario.getProducts().get(key).getName())) {
							tmpEvent.getNewProducts().remove(i);
							i--;
						}
					}
				}
				
			} else if(scheduleEvent instanceof MachineBreakDownEvent) {
				MachineBreakDownEvent tmpEvent = ((MachineBreakDownEvent)scheduleEvent);

				for(Resource r : new ArrayList<Resource>(tmpEvent.getResourceBreakDowns())) {
					
					if(scenario.getResources().get(r.getResourceID()).isInBreakDown()) {
						tmpEvent.getResourceBreakDowns().remove(r);
					}
				}
				
				// Nicht ausführen:
				if(tmpEvent.getResourceBreakDowns().isEmpty()) {
					return;
				}
				
			} else if(scheduleEvent instanceof MachineRepairedEvent) {
				MachineRepairedEvent tmpEvent = ((MachineRepairedEvent)scheduleEvent);

				for(Resource r : new ArrayList<Resource>(tmpEvent.getResourceRepairs())) {
					if(!scenario.getResources().get(r.getResourceID()).isInBreakDown()) {
						tmpEvent.getResourceRepairs().remove(r);
					}
				}
				
				// Nicht ausführen:
				if(tmpEvent.getResourceRepairs().isEmpty()) {
					return;
				}
				
			} else if(scheduleEvent instanceof ChangeOperationResourcesEvent) {
				
				if(scenario.getVariantByOperationID(((ChangeOperationResourcesEvent)scheduleEvent).getOperationID()) == null) {
					return;
				}
				
			} else if(scheduleEvent instanceof ChangeOrderDueTimeEvent) {
				ChangeOrderDueTimeEvent tmpEvent = ((ChangeOrderDueTimeEvent)scheduleEvent);
				
				for(ScheduleOrder so : new ArrayList<ScheduleOrder>(tmpEvent.getOrderList())) {
					if(scenario.getOrder(so.getOrderID()) == null) {
						tmpEvent.getOrderList().remove(so);
						tmpEvent.getOldDueTimes().remove(so.getOrderID());
					}
				}
				
				// Nicht ausführen:
				if(tmpEvent.getOrderList().isEmpty()) {
					return;
				}
				
				
			} else if(scheduleEvent instanceof ChangeOrderPriorityEvent) {
				ChangeOrderPriorityEvent tmpEvent = ((ChangeOrderPriorityEvent)scheduleEvent);
				
				for(ScheduleOrder so : new ArrayList<ScheduleOrder>(tmpEvent.getOrderList())) {
					if(scenario.getOrder(so.getOrderID()) == null) {
						tmpEvent.getOrderList().remove(so);
						tmpEvent.getOldPriorities().remove(so.getOrderID());
					}
				}
				
				// Nicht ausführen:
				if(tmpEvent.getOrderList().isEmpty()) {
					return;
				}
				
				
			} else if(scheduleEvent instanceof NewOrdersEvent) {
				NewOrdersEvent tmpEvent = ((NewOrdersEvent)scheduleEvent);
				
				// Keine Duplikate der Namen erlauben!
				for(ScheduleOrder so : scenario.getOrders()) {
					for(int i = 0; i < tmpEvent.getNewOrdersList().size(); i++) {
						if(tmpEvent.getNewOrdersList().get(i).getName().equals(so.getName())) {
							tmpEvent.getNewOrdersList().remove(i);
							i--;
						}
					}
				}
				for(ScheduleOrder so : scenario.getDoneOrders()) {
					for(int i = 0; i < tmpEvent.getNewOrdersList().size(); i++) {
						if(tmpEvent.getNewOrdersList().get(i).getName().equals(so.getName())) {
							tmpEvent.getNewOrdersList().remove(i);
							i--;
						}
					}
				}
				
				for(ScheduleOrder so : new ArrayList<ScheduleOrder>(tmpEvent.getNewOrdersList())) {
					for(Integer productID : new ArrayList<Integer>(so.getProducts())) {
						if(scenario.getProduct(productID) == null) {
							so.removeProduct(productID);
						}
					}
					// Leere Orders löschen:
					if(so.getProducts().isEmpty()) {
						tmpEvent.getNewOrdersList().remove(so);
					}
				}
				
				// Nicht ausführen:
				if(tmpEvent.getNewOrdersList().isEmpty()) {
					return;
				}
				
				
			} else if(scheduleEvent instanceof RemoveOrdersEvent) {
				RemoveOrdersEvent tmpEvent = ((RemoveOrdersEvent)scheduleEvent);
				
				for(ScheduleOrder so : new ArrayList<ScheduleOrder>(tmpEvent.getRemoveOrdersList())) {
					if(scenario.getOrder(so.getOrderID()) == null) {
						tmpEvent.getRemoveOrdersList().remove(so);
					}
				}
				
				// Nicht ausführen:
				if(tmpEvent.getRemoveOrdersList().isEmpty()) {
					return;
				}
				
			} else if(scheduleEvent instanceof RemoveOrdersProductsEvent) {
				RemoveOrdersProductsEvent tmpEvent = ((RemoveOrdersProductsEvent)scheduleEvent);
				
				for(ScheduleOrder so : new ArrayList<ScheduleOrder>(tmpEvent.getRemoveMap().keySet())) {
					
					// Order schon gelöscht:
					if(scenario.getOrder(so.getOrderID()) == null) {
						continue;
					}
					
					// Products durchsehen:
					for(Product p : new ArrayList<Product>(tmpEvent.getRemoveMap().get(so))) {
						if(scenario.getProduct(p.getProductID()) == null) {
							so.removeProduct(p);
						}
					}
					
					if(so.getProducts().isEmpty()) {
						tmpEvent.getRemoveMap().remove(so);
					}
				}
				
				// Nicht ausführen:
				if(tmpEvent.getRemoveMap().isEmpty()) {
					return;
				}
				
				
			} else if(scheduleEvent instanceof RemoveProductsEvent) {
				RemoveProductsEvent tmpEvent = ((RemoveProductsEvent)scheduleEvent);
				
				for(Product p : new ArrayList<Product>(tmpEvent.getRemovedProducts())) {
					if(scenario.getProduct(p.getProductID()) == null) {
						tmpEvent.getRemovedProducts().remove(p);
					}
				}
				
				// Nicht ausführen:
				if(tmpEvent.getRemovedProducts().isEmpty()) {
					return;
				}
				
				
			} else if(scheduleEvent instanceof RemoveVariantsEvent) {
				RemoveVariantsEvent tmpEvent = ((RemoveVariantsEvent)scheduleEvent);
				
				for(Variant v : new ArrayList<Variant>(tmpEvent.getRemovedVariants())) {
					if(scenario.getVariant(v.getVariantID()) == null) {
						tmpEvent.getRemovedVariants().remove(v);
					}
				}
				
				// Nicht ausführen:
				if(tmpEvent.getRemovedVariants().isEmpty()) {
					return;
				}
			}
			
			// Für ScheduleChangeByUserEvent keinen neuen Schedule berechnen!
			if(scheduleEvent instanceof ScheduleChangeByUserEvent) {
				
				// Schedules Klonen und chosenSchedule wählen:
				for(Schedule s : scenario.getCurrentSchedules()) {
					
					Schedule tmpScheduleClone = s.clone(scenario);

					// Ist der ChosenSchedule
					if(s.getAlgorithmInformations().getAlgorithmName().equals(((ScheduleChangeByUserEvent)scheduleEvent).getNewSchedule()
							.getAlgorithmInformations().getAlgorithmName())) {

						scheduleEvent.setChosenSchedule(tmpScheduleClone);
						scenario.setChosenSchedule(tmpScheduleClone);
					} 
					
					tmpScheduleList.add(tmpScheduleClone);
					tmpScheduleClone.getAlgorithmInformations().
					setInitialScheduleID(((ScheduleChangeByUserEvent)scheduleEvent).getOldSchedule().getScheduleID());
					
					// Event setzen:
					tmpScheduleClone.setScheduleEvent(scheduleEvent);
				}
				
				// CurrentSchedules setzen:
				scenario.setCurrentSchedules(tmpScheduleList);
				scheduleEvent.setCurrentSchedules(tmpScheduleList);
				
				
			// Alle anderen Events werden ganz normal berechnet.	
			} else {
				
				// FIXME
				System.out.println("EventName: " +scheduleEvent.getName());
				
				// Änderung des Events am Scenario bewirken:
				scheduleEvent.changeScenario(scenario);
				
				// Predictive Pläne erzeugen:
				for (PredictiveAlgorithm pa : getPredictiveAlgorithms()) {
					tmpScheduleList.add(pa.produceSchedule(scenario, scheduleEvent,
							scheduleChangeDeadline));
				}

				// Reaktive Pläne erzeugen:
				List<ScheduleEvent> eventList = new ArrayList<ScheduleEvent>();
				eventList.add(scheduleEvent);
				for (ReactiveAlgorithm ra : getReactiveAlgorithms()) {
					// TODO
					System.out.println(ra.getAlgorithmName());
					tmpScheduleList.add(ra.produceSchedule(scenario, eventList,
							scheduleChangeDeadline));
				}

				// Schedules löschen, die nicht die Hard Constraints erfüllen:
				for (int i = 0; i < tmpScheduleList.size(); i++) {
					if (!tmpScheduleList.get(i).isConsistent(scenario,
							scheduleChangeDeadline)) {
						tmpScheduleList.remove(i);
						i--;
					}
				}

				// Alte Schedules löschen und neue Eintragen:
				scenario.setCurrentSchedules(tmpScheduleList);
				scheduleEvent.setCurrentSchedules(tmpScheduleList);

				// Evaluieren und chosenSchedule setzen:
				Schedule bestSchedule = null;
				try {
					bestSchedule = new Evaluation(scenario.getKeyFigureList())
							.getBestSchedule(scenario, tmpScheduleList).get(0);
				} catch (NotPlannedException e) {
					e.printStackTrace();
				}
				scenario.setChosenSchedule(bestSchedule);
				scheduleEvent.setChosenSchedule(bestSchedule);
				
				// Event und KeyFigure bei den Schedules setzen:
				for(Schedule tmpSchedule : tmpScheduleList) {					
					tmpSchedule.setScheduleEvent(scheduleEvent);
					tmpSchedule.setKeyFigureValueMap(getScheduleKeyFigures(scenario, tmpSchedule));
				}
			}
			
			// Neue Schedules in die DB: 
			try {
				DBMethods.getInstance().insertCurrentSchedules(scenario, scenario.getChosenSchedule(), tmpScheduleList);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			// Event auslösen:
			new ScenarioManagerImpl().addEvent(
					scenario.getEventServiceDomain(), scheduleEvent);
		}
	}

	@Override
	public void setKeyFigures(int scenarioID, List<KeyFigure> keyFigureList) {
		
		Scenario tmpScenario = scenarios.get(scenarioID);
		tmpScenario.setKeyFigureList(keyFigureList);
		
		// Event:
		this.addEvent(tmpScenario.getEventServiceDomain(),
				new ChangeKeyFiguresEvent(keyFigureList));
		
		// Datenbank:
		try {
			DBMethods.getInstance().insertNewKeyFigureList(scenarioID, keyFigureList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public HashMap<Integer, Integer> getKeyFigureValues(int scenarioID) {
		return wrapperMap.get(scenarioID).getKeyFigureValueMap();
	}
	
	@Override
	public Schedule stopIterativeImprovement(int scenarioID) {
		wrapperMap.get(scenarioID).cancel();

		Schedule newSchedule = wrapperMap.get(scenarioID).getSchedule();
		
		/*
		 * in die Datenbank speichern:
		 */
		try {
			DBMethods.getInstance().addSchedule(getScenarioStatic(scenarioID), newSchedule);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// als currentschedule setzen:
		getScenarioStatic(scenarioID).getCurrentSchedules().add(newSchedule);
		
		return newSchedule;
	}

	@Override
	public void addScheduleEvent(ScheduleEvent scheduleEvent) {
		ScenarioManagerImpl.addEvent(scheduleEvent);
	}

	@Override
	public void addScheduleEvents(List<ScheduleEvent> scheduleEventList) {
		ScenarioManagerImpl.addEvents(scheduleEventList);
	}

	/**
	 * Statischer Weg ein Scenario zu bekommen.
	 * z.B. @see FileUploadServlet
	 * @param scenarioID
	 * @return
	 */
	public static Scenario getScenarioStatic(int scenarioID) {
		return scenarios.get(scenarioID);
	}

	@Override
	public void changeScheduleChangeDeadline(int scenarioID,
			int newChangeScheduleDeadline) {
		
		Scenario tmpScenario = scenarios.get(scenarioID);
		tmpScenario.setScheduleChangeDeadline(newChangeScheduleDeadline);

		// Event:
		this.addEvent(tmpScenario.getEventServiceDomain(),
				new ChangeScheduleChangeDeadlineEvent(newChangeScheduleDeadline));

		try {
			DBMethods.getInstance().changeScheduleChangeDeadline(newChangeScheduleDeadline, newChangeScheduleDeadline);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Liefert Schedules mit offset und limit.
	 * CurrentSchedules (mit Ausnahme des ChosenSchedules) werden übersprungen.
	 */
	@Override
	public List<Schedule> getSchedules(int scenarioID, int offset, int limit) {
		List<Schedule> tmpScheduleList = new ArrayList<Schedule>();
		
		// Schedules laden:
		try {
			tmpScheduleList.addAll(DBMethods.getInstance().getSchedules(scenarios.get(scenarioID), offset, limit));
		} catch (SQLException e) {
			e.printStackTrace();
		}
				
		return tmpScheduleList;
	}

	@Override
	public List<ScenarioInformations> getScenarioInformationsList() {
	
		// TODO: falls mal wieder alles kaputt ist :D
		
//		try {
//			DBMethods.getInstance().truncateAllTablesWithoutUser();
//		} catch (SQLException e1) {
//			e1.printStackTrace();
//		}
//		
//		this.getScenario(1, 3);
//		this.getScenario(2, 3);
		
		
		/*
		 * Falls scenarios leer, dann aus der DB laden.
		 * Z.B. wenn der Server neugestartet wurde.
		 * Startet initial auch den EventThread.
		 */
		if(scenarios.isEmpty()) {
			try {
				
				scenarios.putAll(DBMethods.getInstance().getScenarios());
				
				startEventThread();
				
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		
		List<ScenarioInformations> tmpScenarioInformationsList = new ArrayList<ScenarioInformations>();
		
		// Liste füllen:
		for(Integer scenarioID : scenarios.keySet()) {
			Scenario tmpScenario = scenarios.get(scenarioID);
			// AuthorName holen:
			try {
				tmpScenarioInformationsList.add(new ScenarioInformations(
						tmpScenario.getScenarioID(), tmpScenario.getName(), DBMethods.getInstance().getNicknameByID(tmpScenario.getAuthorID()), 
						((scenarioUserIDMap.get(scenarioID) == null) ? true: false)));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return tmpScenarioInformationsList;
	}
	
	/**
	 * Berechnet alle KeyFigures zu einem Schedule.
	 * @param schedule
	 * @return Map<Integer = KeyFigure ID, Integer = Value>
	 */
	private static Map<Integer, Integer> getScheduleKeyFigures(Scenario scenario, Schedule schedule) {
		Map<Integer, Integer> tmpKeyFigureMap = new HashMap<Integer, Integer>();
		
		// KeyFigures durchlaufen:
		for(KeyFigure kf : new ScenarioManagerImpl().getKeyFigures()) {
			try {
				tmpKeyFigureMap.put(kf.getId(), kf.calculate(scenario, schedule));
			} catch (NotPlannedException e) {
				e.printStackTrace();
			}
		}
		
		return tmpKeyFigureMap;
	}

	/**
	 * Startet das IterativeImprovement.
	 * wrapperID sowie changerID stellen jeweiligen Index in deren Liste dar.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void startIterativeImprovement(Scenario scenario, Schedule chosenSchedule, int duration,	HashMap<Integer, KeyFigure> keyFiguresMap,
			HashMap<Integer, Integer> keyFigureFlexibilityMap,
			int wrapperId, int changerId) {
		Wrapper wrapper = null;
		Changer changer = null;
		
		
		/*
		 * Changer wählen:
		 */
		
		changer = ScenarioManagerImpl.getIterativeImprovementChangers().get(changerId);
		
		/*
		 * Wrapper wählen:
		 */
		try {
			Constructor<Wrapper> constructorWrapper = (Constructor<Wrapper>) ScenarioManagerImpl.getIterativeImprovementWrappers().get(wrapperId).getClass().getConstructor(Changer.class, Scenario.class, Schedule.class, int.class, HashMap.class, HashMap.class);
			wrapper = constructorWrapper.newInstance(changer, scenario, chosenSchedule, duration, keyFiguresMap, keyFigureFlexibilityMap);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
			
		wrapperMap.put(scenario.getScenarioID(), wrapper);
		wrapper.process();
	}

	/**
	 * Fügt ein neues Scenario hinzu.
	 * @param scenario
	 */
	public static void addScenarioStatic(Scenario scenario) {
		new ScenarioManagerImpl().addScenario(scenario);
	}
	
	@Override
	public void addScenario(Scenario scenario) {

		List<Schedule> tmpSchedules = new ArrayList<Schedule>();
		Date tmpScheduleChangeDeadline = new Date();

		for(PredictiveAlgorithm pa : getPredictiveAlgorithms()) {
			tmpSchedules.add(pa.produceSchedule(scenario, null, tmpScheduleChangeDeadline));
		}

		// CurrentSchedules setzen:
		scenario.setCurrentSchedules(tmpSchedules);

		// Besten Schedule finden:
		try {
			scenario.setChosenSchedule(new Evaluation(scenario.getKeyFigureList()).getBestSchedule(scenario, tmpSchedules).get(0));
		} catch (NotPlannedException e) {
			e.printStackTrace();
		}
		
		try {
			// Scenario in die DB:
			scenario.setScenarioID(DBMethods.getInstance().insertNewScenario(scenario));
			
			// Scenario in Scenarios:
			scenarios.put(scenario.getScenarioID(), scenario);
			
			// Scenario Event:
			this.addEvent(logInPresenterDomain, new ScenarioInformationGridEvent(new ScenarioInformations(scenario.getScenarioID(), scenario.getName(), 
					DBMethods.getInstance().getNicknameByID(scenario.getAuthorID()), true)));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void logOut(int scenarioID, int userID) {
		
		// Aus Map löschen:
		if(scenarioUserIDMap.get(scenarioID) != null && scenarioUserIDMap.get(scenarioID) == userID) {
			
			// Aus der Map löschen:
			scenarioUserIDMap.remove(scenarioID);
			
			// Auf allen LogIn Views als frei markieren:
			Scenario tmpScenario = scenarios.get(scenarioID);
			try {
				this.addEvent(logInPresenterDomain, new ScenarioInformationGridEvent(new ScenarioInformations(scenarioID, tmpScenario.getName(), 
						DBMethods.getInstance().getNicknameByID(tmpScenario.getAuthorID()), true)));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Erhöhrt die ScenarioIDs beim User.
	 * @param scenarioID
	 */
	public static void addGWTIncreaseScenarioIDsEvent(int scenarioID) {
		Scenario tmpScenario = scenarios.get(scenarioID);
		
		new ScenarioManagerImpl().addEvent(tmpScenario.getEventServiceDomain(), 
				new IncreaseScenarioIDsEvent(tmpScenario.getNewOrderIDCount(), tmpScenario.getNewProductIDCount(), 
						tmpScenario.getNewVariantIDCount(), tmpScenario.getNewOperationIDCount(), 
						tmpScenario.getNewResourceIDCount()));
	}

	/**
	 * Liefert eine Übersicht über alle Wrapper
	 * für das IterativeImprovement.
	 * @return <ID, String>
	 */
	@Override
	public Map<Integer, String> getIterativeImprovementWrapperNames() {
		Map<Integer, String> tmpMap = new HashMap<Integer, String>();

		for(Wrapper w : ScenarioManagerImpl.getIterativeImprovementWrappers()) {
			tmpMap.put(tmpMap.size(), w.getName());
		}
		
		return tmpMap;
	}

	/**
	 * Liefert eine Übersicht über alle Changer
	 * für das IterativeImprovement.
	 * @return <ID, String>
	 */
	@Override
	public Map<Integer, String> getIterativeImprovementChangerNames() {
		Map<Integer, String> tmpMap = new HashMap<Integer, String>();
		
		for(Changer c : ScenarioManagerImpl.getIterativeImprovementChangers()) {
			tmpMap.put(tmpMap.size(), c.getName());
		}
		
		return tmpMap;
	}
	
	/**
	 * Enthält alle Wrapper für das
	 * IterativeImprovement
	 * @return
	 */
	private static List<Wrapper> getIterativeImprovementWrappers() {
		List<Wrapper> wrapperList = new ArrayList<Wrapper>();
		
		// Hier neue Wrapper einfuegen:
		wrapperList.add(new Greedy());
		wrapperList.add(new HillClimbing());
		wrapperList.add(new SimpleWrapper());
		wrapperList.add(new ThresholdAccepting());
		
		return wrapperList;
	}
	
	/**
	 * Enthält alle Changer für das
	 * IterativeImprovement
	 * @return
	 */
	private static List<Changer> getIterativeImprovementChangers() {
		List<Changer> changerList = new ArrayList<Changer>();
		
		// Hier neue Changer einfuegen:
		changerList.add(new AllocationSwitcher());
		changerList.add(new PlannedVariantReScheduler());
		
		return changerList;
	}

	/**
	 * Setzt den Wert zwischen die Allocations.
	 * @param seconds
	 */
	@Override
	public void setSecondsBetweenAllocations(int scenarioID, int seconds) {
		Scenario scenario = scenarios.get(scenarioID);
		synchronized (scenario) {
			try {
				DBMethods.getInstance().setSecondsBetweenAllocations(scenarioID, seconds);
				scenario.setSecondsBetweenAllocations(seconds);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	}

	/**
	 * Liefert eine ältere Version eines Scenarios.
	 * Beispielsweise damit ältere Scheduls gezeichnet werden können.
	 * @param scenarioID
	 * @param scheduleID
	 * @return
	 */
	@Override
	public Scenario getScenarioForOldSchedule(int scenarioID, int scheduleID) {
		synchronized (scenarios.get(scenarioID)) {
			Scenario tmpScenario = scenarios.get(scenarioID).clone();
			// Alle Schedules hiervor laden:
			try {
				List<Schedule> tmpScheduleList = DBMethods.getInstance().getSchedulesAfterID(tmpScenario, scheduleID);
				
				// Muss überhaupt zurück gegangen werden?
				if(tmpScheduleList.size() > tmpScenario.getCurrentSchedules().size()) {
					// für den chosenschedule zurück setzen:
					tmpScenario.getChosenSchedule().getScheduleEvent().changeScenarioBackwards(tmpScenario);
					// Für jeden weiteren, außer den currentScheduls:
					for(Schedule s : tmpScheduleList) {
						if(!tmpScenario.getCurrentSchedules().contains(s)) {
							tmpScenario.getChosenSchedule().getScheduleEvent().changeScenarioBackwards(tmpScenario);
						}
					}
				}
					
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return tmpScenario;
		}
	}

	/**
	 * Liefert einen bestimmten Schedule.
	 * @param scenarioID
	 * @param scheduleID
	 * @return
	 */
	@Override
	public Schedule getSchedule(int scenarioID, int scheduleID) {
		try {
			return DBMethods.getInstance().getSchedule(scenarios.get(scenarioID), scheduleID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Liefert die ScheduleInformation zu einem bestimmten Schedule.
	 * @param scenarioID
	 * @param scheduleID
	 * @return
	 */
	@Override
	public ScheduleInformation getScheduleInformation(int scenarioID,
			int scheduleID) {
		return new ScheduleInformation(this.getSchedule(scenarioID, scheduleID));
	}

	/**
	 * Gibt ScheduleInformations aus dem Offset und Limit zurück.
	 * CurrentSchedules (mit Ausnahme des ChosenSchedules) werden übersprungen.
	 * @param scenarioID
	 * @param offset
	 * @param limit
	 * @return
	 */
	@Override
	public List<ScheduleInformation> getScheduleInformationList(int scenarioID,
			int offset, int limit) {
		List<ScheduleInformation> tmpScheduleInformationList = new ArrayList<ScheduleInformation>();
		for(Schedule s : this.getSchedules(scenarioID, offset, limit)) {
			tmpScheduleInformationList.add(new ScheduleInformation(s));
		}
		
		return tmpScheduleInformationList;
	}
}