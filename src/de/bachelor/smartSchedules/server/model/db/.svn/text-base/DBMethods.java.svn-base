package de.bachelor.smartSchedules.server.model.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bachelor.smartSchedules.shared.model.exceptions.EMailAlreadyInUseException;
import de.bachelor.smartSchedules.shared.model.exceptions.NicknameAlreadyInUseException;
import de.bachelor.smartSchedules.shared.model.exceptions.WrongPasswordException;
import de.bachelor.smartSchedules.shared.model.schedule.Allocation;
import de.bachelor.smartSchedules.shared.model.schedule.Operation;
import de.bachelor.smartSchedules.shared.model.schedule.PlannedVariant;
import de.bachelor.smartSchedules.shared.model.schedule.Product;
import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.ResourceRestriction;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleOrder;
import de.bachelor.smartSchedules.shared.model.schedule.Variant;
import de.bachelor.smartSchedules.shared.model.schedule.event.ChangeOperationResourcesEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ChangeOrderDueTimeEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ChangeOrderPriorityEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ChangeResourceAvailabilityEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.MachineBreakDownEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.MachineRepairedEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.MaintenancePeriodsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewOrdersEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewProductsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewResourceEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewVariantsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveOrdersEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveOrdersProductsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveProductsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveVariantsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleChangeByUserEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleEvent;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;
import de.bachelor.smartSchedules.shared.model.util.AlgorithmInformation;
import de.bachelor.smartSchedules.shared.model.util.User;

/**
 * Methoden für die Datenbank.
 * Singleton Pattern.
 * @author timo
 *
 */
public class DBMethods {
	private static DBMethods instance;
	
	/**
	 * Sperrobjekte
	 */
	private Map<Integer, Object> scenarioMap;
	
	/**
	 * Sperrobjekte
	 */
	private Object changeOperationResourceEventLock;
	
	private DBMethods() {
		this.scenarioMap = new HashMap<Integer, Object>();
		this.changeOperationResourceEventLock = new Object();
	}
	
	/**
	 * Liefert alle Schedules, die nach einer bestimmten ID kommen.
	 * @param scenario
	 * @param scheduleID
	 * @return
	 * @throws SQLException
	 */
	public List<Schedule> getSchedulesAfterID(Scenario scenario, int scheduleID) throws SQLException {
		PreparedStatement stmtSchedulesAfterID = DBConnection.getPstmt(DBStatements.SELECT_SCHEDULES_AFTER_SCHEDULE_ID.getStmt());
		List<Schedule> tmpScheduleList = new ArrayList<Schedule>();
		
		stmtSchedulesAfterID.setInt(1, scenario.getScenarioID());
		stmtSchedulesAfterID.setInt(2, scheduleID);
		
		ResultSet rsSchedulesAfterID = stmtSchedulesAfterID.executeQuery();
		while(rsSchedulesAfterID.next()) {
			tmpScheduleList.add(this.getSchedule(scenario, scheduleID));
		}

		return tmpScheduleList;
	}
	
	/**
	 * Ändert die secondsBetweenAllocations.
	 * @param scenarioID
	 * @param seconds
	 * @throws SQLException
	 */
	public void setSecondsBetweenAllocations(int scenarioID, int seconds) throws SQLException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.UPDATE_SECONDS_BETWEEN_ALLOCATIONS.getStmt());
		
		stmt.setInt(1, seconds);
		stmt.setInt(2, scenarioID);
		stmt.execute();
	}
	
	/**
	 * Leert alle Tabellen außer USER.
	 * @throws SQLException
	 */
	public void truncateAllTablesWithoutUser() throws SQLException {
		PreparedStatement stmtAllTables = DBConnection.getPstmt(DBStatements.SHOW_TABLES.getStmt());
		
		// Alle Tabellen durchlaufen:
		ResultSet rsAllTables = stmtAllTables.executeQuery();
		while(rsAllTables.next()) {
			if(!rsAllTables.getString("tables_in_smartschedule").equals("USER")){
				truncateTable(rsAllTables.getString("tables_in_smartschedule"));
			}
		}
	}
	
	/**
	 * Löscht eine Tabelle.
	 * @param tableName
	 * @throws SQLException
	 */
	public void truncateTable(String tableName) throws SQLException {
		PreparedStatement stmtTruncateTable = DBConnection.getPstmt("TRUNCATE TABLE " +tableName);
		
		stmtTruncateTable.execute();
	}
	
	/**
	 * Leert alle Tabellen.
	 * @throws SQLException
	 */
	public void truncateAllTables() throws SQLException {
		PreparedStatement stmtAllTables = DBConnection.getPstmt(DBStatements.SHOW_TABLES.getStmt());
		
		// Alle Tabellen durchlaufen:
		ResultSet rsAllTables = stmtAllTables.executeQuery();
		while(rsAllTables.next()) {
			truncateTable(rsAllTables.getString("tables_in_smartschedule"));
		}
	}
	
	/**
	 * Gibt einen Nicknamen aus.
	 * @param userID
	 * @return
	 * @throws SQLException
	 */
	public String getNicknameByID(int userID) throws SQLException {
		String tmpNickname = "";
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_USER_BY_ID.getStmt());
		
		stmt.setInt(1, userID);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			tmpNickname = rs.getString("NICKNAME");
		}
		
		return tmpNickname;
	}
	
	/**
	 * Liefert ein UserObjekt, falls alles richtige ist.
	 * @param nickname
	 * @param password
	 * @return
	 * @throws SQLException
	 * @throws WrongPasswordException
	 */
	public User getUser(String nickname, String password) throws SQLException, WrongPasswordException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_USER_BY_NICKNAME_AND_PASSWORD.getStmt());
		User tmpUser = null;
		
		// Variablen binden:
		stmt.setString(1, nickname);
		stmt.setString(2, password);
		
		ResultSet rs = stmt.executeQuery();
		// User existiert:
		if(rs.next()) {
			tmpUser = new User(rs.getInt("USER_ID"), rs.getString("NICKNAME"), rs.getString("FORENAME"), rs.getString("SURNAME"), rs.getString("E_MAIL"));
		// User existiert nicht oder falsches passwort:	
		} else {
			throw new WrongPasswordException();
		}
		
		return tmpUser;
	}
	
	/**
	 * Fügt einen neuen Nutzer hinzu.
	 * @param nickname
	 * @param password
	 * @param eMail
	 * @param givenname
	 * @param surname
	 * @throws SQLException 
	 * @throws NicknameAlreadyInUseException 
	 * @throws EMailAlreadyInUseException 
	 */
	public void insertUser(String nickname, String password, String eMail, String givenname, String surname) throws SQLException, NicknameAlreadyInUseException, EMailAlreadyInUseException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_USER.getStmt());
		PreparedStatement stmtNickname = DBConnection.getPstmt(DBStatements.SELECT_USER_BY_NICKNAME.getStmt());
		PreparedStatement stmtEMail = DBConnection.getPstmt(DBStatements.SELECT_USER_BY_E_MAIL.getStmt());
		
		// Prüfen, ob der Nickname schon besetzt ist:
		stmtNickname.setString(1, nickname);
		ResultSet rsNickname = stmtNickname.executeQuery();
		if(rsNickname.next()) {
			throw new NicknameAlreadyInUseException(nickname);
		}
		
		// Prüfen, ob die E-Mail Addresse schon besetzt ist:
		stmtEMail.setString(1, eMail);
		ResultSet rsEMail = stmtEMail.executeQuery();
		if(rsEMail.next()) {
			throw new EMailAlreadyInUseException(eMail);
		}
		
		// Variablen binden:
		stmt.setString(1, nickname);
		stmt.setString(2, password);
		stmt.setString(3, eMail);
		stmt.setString(4, givenname);
		stmt.setString(5, surname);
		
		stmt.execute();
	}
	
	/**
	 * Läd Schedules mit LIMIT und OFFSET.
	 * Dabei werden die CURRENT_SCHEDULES mit ausnahme des CHOSEN_SCHEDULES übersprungen!
	 * @param scenario
	 * @param offset
	 * @param limit
	 * @return
	 * @throws SQLException
	 */
	public List<Schedule> getSchedules(Scenario scenario, int offset, int limit) throws SQLException {
		
		if(this.scenarioMap.get(scenario.getScenarioID()) == null) {
			this.scenarioMap.put(scenario.getScenarioID(), new Object());
		}
		
		synchronized(this.scenarioMap.get(scenario.getScenarioID())) {
			List<Schedule> tmpScheduleList = new ArrayList<Schedule>();
			
			PreparedStatement stmtSchedules = DBConnection.getPstmt(DBStatements.SELECT_SCHEDULES.getStmt());
			PreparedStatement stmtCountCurrentSchedules = DBConnection.getPstmt(DBStatements.COUNT_CURRENT_SCHEDULES.getStmt());
			PreparedStatement stmtScheduleBefore = DBConnection.getPstmt(DBStatements.SELECT_SCHEDULE_ID_BEFORE.getStmt());
			
			// ChosenSchedule soll auch geladen werden:
			if(offset == 0) {
				tmpScheduleList.add(scenario.getChosenSchedule());
			} 
			
			/*
			 *  Ab hier alle CurrentSchedules überspringen:
			 */
			stmtCountCurrentSchedules.setInt(1, scenario.getScenarioID());
			ResultSet rsCountCurrentSchedules = stmtCountCurrentSchedules.executeQuery();
			if(rsCountCurrentSchedules.next()) {
				offset += rsCountCurrentSchedules.getInt("COUNTER");
			}
			
			stmtSchedules.setInt(1, scenario.getScenarioID());
			stmtSchedules.setInt(2, limit);
			stmtSchedules.setInt(3, offset);
			
			// Schedules laden:
			ResultSet rs = stmtSchedules.executeQuery();
			while(rs.next()) {
				tmpScheduleList.add(this.getSchedule(scenario, rs.getInt("SCHEDULE_ID")));
				
			}
			
			// Bei allen ScheduleChangeByUserEvents den OldSchedule setzen:
			for(int i = 0; i < tmpScheduleList.size(); i++) {
				Schedule tmpSchedule = tmpScheduleList.get(i);
				if(tmpSchedule.getScheduleEvent() != null && 
				tmpSchedule.getScheduleEvent().getType() == ScheduleEvent.TYPE_SCHEDULE_CHANGE_BY_USER_EVENT) {
					
					((ScheduleChangeByUserEvent)tmpSchedule.getScheduleEvent()).setNewSchedule(tmpSchedule);
					
					// oldSchedule ist schon in der List:
					if(i > 0) {
						
						((ScheduleChangeByUserEvent)tmpSchedule.getScheduleEvent()).setOldSchedule(tmpScheduleList.get(i - 1));
						
					// oldSchedule muss noch geladen werden:
					} else {
						
						stmtScheduleBefore.setInt(1, scenario.getScenarioID());
						stmtScheduleBefore.setInt(2, tmpSchedule.getScheduleID());
						ResultSet rsOldSchedule = stmtScheduleBefore.executeQuery();
						
						if(rsOldSchedule.next()) {
							((ScheduleChangeByUserEvent)tmpSchedule.getScheduleEvent()).setOldSchedule(this.getSchedule(scenario, rsOldSchedule.getInt("SCHEDULE_ID")));
						}
						
					}
				}
			}
			
			return tmpScheduleList;
		}
	}
	
	/**
	 * Ändert die ScheduleChangeDeadline zu einem Scenario.
	 * @param scenarioID
	 * @param newScheduleChangeDeadline
	 * @throws SQLException
	 */
	public void changeScheduleChangeDeadline(int scenarioID, int newScheduleChangeDeadline) throws SQLException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.UPDATE_SCHEDULE_CHANGE_DEADLINE.getStmt());
		
		stmt.setInt(1, newScheduleChangeDeadline);
		stmt.setInt(2, scenarioID);
		
		stmt.execute();
	}
	
	/**
	 * Läd eine Map mit allen Scenarien.
	 * @return Map<Integer = Scenario ID, Schedule>
	 * @throws SQLException
	 */
	public Map<Integer, Scenario> getScenarios() throws SQLException {
		Map<Integer, Scenario> tmpScenarioMap = new HashMap<Integer, Scenario>();
		
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_SCENARIO_IDS.getStmt());
		
		// Map füllen:
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			tmpScenarioMap.put(rs.getInt("SCENARIO_ID"), this.getScenario(rs.getInt("SCENARIO_ID")));
		}
		
		return tmpScenarioMap;
	}
	
	public Scenario getScenario(int scenarioID) throws SQLException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_SCENARIO.getStmt());
		PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_CURRENT_SCHEDULES.getStmt());
		
		Scenario scenario = null;
		
		stmt.setInt(1, scenarioID);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			/*
			 *  Scenario erstellen:
			 */
			scenario = new Scenario(scenarioID, rs.getInt("AUTHER_ID"), rs.getString("NAME"));
			
			/*
			 * Verschiedene Werte setzen:
			 */
			scenario.setNewProductIDCount(rs.getInt("NEW_PRODUCT_ID_COUNT"));
			scenario.setNewOrderIDCount(rs.getInt("NEW_ORDER_ID_COUNT"));
			scenario.setNewVariantIDCount(rs.getInt("NEW_VARIANT_ID_COUNT"));
			scenario.setNewOperationIDCount(rs.getInt("NEW_OPERATION_ID_COUNT"));
			scenario.setNewResourceIDCount(rs.getInt("NEW_RESOURCE_ID_COUNT"));
			scenario.setScheduleChangeDeadline(rs.getInt("SCHEDULE_CHANGE_DEADLINE"));
			scenario.setSecondsBetweenAllocations(rs.getInt("SECONDS_BETWEEN_ALLOCATIONS"));
			
			/*
			 * Resources laden:
			 */
			scenario.addResources(this.getResources(scenario));
			
			/*
			 * Products/Variants/Operations laden.
			 * Alles über this.getProducts()
			 */
			scenario.addProducts(this.getProducts(scenario));
			
			/*
			 * CurrentSchedules laden und chosenSchedule setzen: 
			 */
			List<Schedule> tmpCurrentScheduleList = new ArrayList<Schedule>();
			int tmpChosenScheduleID = rs.getInt("CHOSEN_SCHEDULE_ID");
			
			stmt2.setInt(1, scenarioID);
			ResultSet rsCurrentSchedules = stmt2.executeQuery();
			while(rsCurrentSchedules.next()) {
				
				// Zu den Current Schedules:
				Schedule tmpSchedule = this.getSchedule(scenario, rsCurrentSchedules.getInt("SCHEDULE_ID"));
				tmpCurrentScheduleList.add(tmpSchedule);
				
				// Als ChosenSchedule:
				if(tmpSchedule.getScheduleID() == tmpChosenScheduleID) {
					scenario.setChosenSchedule(tmpSchedule);
				}
				
			}
			
			scenario.addCurrentSchedules(tmpCurrentScheduleList);
			
			
			/*
			 * ScheduleOrders laden. Hierbei werden nur ScheduleOrders geladen,
			 * die sich im aktuellen Schedule befinden!
			 */
			scenario.addOrders(this.getScheduleOrders(scenario));
		}
		
		return scenario;
	}
	
	/**
	 * Gibt ein ScheduleChangeByUserEvent aus.
	 * @param scenario
	 * @param eventDBID
	 * @param newEventSchedule
	 * @return
	 * @throws SQLException
	 */
	private ScheduleChangeByUserEvent getScheduleChangeByUserEvent(Scenario scenario, int eventDBID) throws SQLException {
		ScheduleChangeByUserEvent tmpScheduleChangeByUserEvent = null;
		
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_SCHEDULE_CHANGE_BY_USER_EVENT.getStmt());
		
		// Variablen binden:
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, eventDBID);
		
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {			
			tmpScheduleChangeByUserEvent = new ScheduleChangeByUserEvent(scenario.getScenarioID(), 
					new Date(rs.getLong("THROWTIME")));
		}
		
		return tmpScheduleChangeByUserEvent;
	}
	
	/**
	 * Läd ein ChangeOperationResourceEvent.
	 * @param scenario
	 * @param eventDBID
	 * @return
	 * @throws SQLException
	 */
	private ChangeOperationResourcesEvent getChangeOperationResourcesEvent(Scenario scenario, int eventDBID) throws SQLException {
		ChangeOperationResourcesEvent tmpChangeOperationResourcesEvent = null;
		
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_CHANGE_OPERATION_RESOURCE_EVENT.getStmt());
		PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_CHANGE_OPERATION_RESOURCE_LIST.getStmt());
		PreparedStatement stmt3 = DBConnection.getPstmt(DBStatements.SELECT_CHANGE_OPERATION_OLD_RESOURCE_LIST.getStmt());
		
		// Variablen binden:
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, eventDBID);
		stmt2.setInt(1, eventDBID);
		stmt3.setInt(1, eventDBID);
		
		ResultSet rsEvent = stmt.executeQuery();
		if(rsEvent.next()) {
			
			/*
			 * Neue Resource List erstellen:
			 */
			List<Integer> tmpNewResourceList = new ArrayList<Integer>();
			ResultSet rsNewResourceList = stmt2.executeQuery();
			while(rsNewResourceList.next()) {
				tmpNewResourceList.add(rsNewResourceList.getInt("RESOURCE_ID"));
			}
			
			/*
			 * Alte Resource List erstellen:
			 */
			List<Integer> tmpOldResourceList = new ArrayList<Integer>();
			ResultSet rsOldResourceList = stmt3.executeQuery();
			while(rsOldResourceList.next()) {
				tmpNewResourceList.add(rsOldResourceList.getInt("RESOURCE_ID"));
			}
			
			tmpChangeOperationResourcesEvent = new ChangeOperationResourcesEvent(scenario.getScenarioID(), 
					scenario.getProductByOperationID(rsEvent.getInt("OPERATION_ID")).getProductID(), scenario.getVariantByOperationID(rsEvent.getInt("OPERATION_ID")).getVariantID(), 
					rsEvent.getInt("OPERATION_ID"), rsEvent.getInt("NEW_DURATION"), tmpNewResourceList, new Date(rsEvent.getLong("THROWTIME")));
			
			/*
			 * Weitere Einstellungen
			 */
			tmpChangeOperationResourcesEvent.setOldResourceList(tmpOldResourceList);
			tmpChangeOperationResourcesEvent.setOldDuration(rsEvent.getInt("OLD_DURATION"));
		}
		
		return tmpChangeOperationResourcesEvent;
	}
	
	/**
	 * Läd ein ChangeOrdersDueTimesEvent.
	 * @param scenario
	 * @param eventDBID
	 * @return
	 * @throws SQLException
	 */
	private ChangeOrderDueTimeEvent getChangeOrdersDueTimesEvent(Scenario scenario, int eventDBID) throws SQLException {
		ChangeOrderDueTimeEvent tmpChangeOrdersDueTimesEvent = null;
		
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_CHANGE_ORDERS_DUE_TIMES_EVENT.getStmt());
		
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, eventDBID);
		
		ResultSet rs = stmt.executeQuery();
		
		// Neue orderList erstelen:
		List<ScheduleOrder> tmpOrderList = new ArrayList<ScheduleOrder>();
		Map<Integer, Date> tmpOldDueTimes = new HashMap<Integer, Date>();
		while(rs.next()) {
			// Event erstellen:
			if(tmpChangeOrdersDueTimesEvent == null) {
				tmpChangeOrdersDueTimesEvent = new ChangeOrderDueTimeEvent(scenario.getScenarioID(), tmpOrderList, new Date(rs.getLong("THROWTIME")));
				tmpChangeOrdersDueTimesEvent.setOldDueTimes(tmpOldDueTimes);
			}
			
			// Neue OrderList (duetimes):
			tmpOrderList.add(new ScheduleOrder(rs.getInt("SCHEDULE_ORDER_ID"), 
					scenario.getScenarioID(), new ArrayList<Product>(), "", new Date(), new Date(rs.getLong("NEW_DUE_TIME")), -1));
			
			// Alte dueTimesMap erstellen:
			tmpOldDueTimes.put(rs.getInt("SCHEDULE_ORDER_ID"), new Date(rs.getLong("OLD_DUE_TIME")));
			
		}
		
		return tmpChangeOrdersDueTimesEvent;
	}
	
	/**
	 * Läd ein ChangeOrdersPriorityEvent.
	 * @param scenario
	 * @param eventDBID
	 * @return
	 * @throws SQLException
	 */
	private ChangeOrderPriorityEvent getChangeOrdersPriorityEvent(Scenario scenario, int eventDBID) throws SQLException {
		ChangeOrderPriorityEvent tmpChangeOrdersPriorityEvent = null;
		List<ScheduleOrder> tmpScheduleOrderList = new ArrayList<ScheduleOrder>();
		Map<Integer, Integer> tmpOldPriorityMap = new HashMap<Integer, Integer>();
		
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_CHANGE_ORDERS_PRIORITY_EVENT.getStmt());
		
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, eventDBID);
		
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			// Event erstellen:
			if(tmpChangeOrdersPriorityEvent == null) {
				tmpChangeOrdersPriorityEvent = new ChangeOrderPriorityEvent(scenario.getScenarioID(), 
						tmpScheduleOrderList, new Date(rs.getLong("THROWTIME")));
				tmpChangeOrdersPriorityEvent.setOldPriorities(tmpOldPriorityMap);
			}
			
			// Order mit neuer Priority erstellen:
			tmpScheduleOrderList.add(new ScheduleOrder(rs.getInt("SCHEDULE_ORDER_ID"), scenario.getScenarioID(), 
					new ArrayList<Product>(), "", new Date(), new Date(), rs.getInt("NEW_PRIORITY")));
			
			// OldPriorityMap füllen:
			tmpOldPriorityMap.put(rs.getInt("SCHEDULE_ORDER_ID"), rs.getInt("OLD_PRIORITY"));
			
		}
		
		return tmpChangeOrdersPriorityEvent;
	}
	
	/**
	 * Läd ein ChangeResourceAvailabilityEvent.
	 * @param scenario
	 * @param eventDBID
	 * @return
	 * @throws SQLException
	 */
	private ChangeResourceAvailabilityEvent getChangeResourceAvailabilityEvent(Scenario scenario, int eventDBID) throws SQLException {
		ChangeResourceAvailabilityEvent tmpResourceAvailabilityEvent = null;
		Map<Integer, Integer> tmpNewResourceAvailabilityMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> tmpOldResourceAvailabilityMap = new HashMap<Integer, Integer>();
		
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_CHANGE_RESOURCE_AVAILABILITY_EVENT.getStmt());
		
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, eventDBID);
		
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			// Event erstellen:
			if(tmpResourceAvailabilityEvent == null) {
				tmpResourceAvailabilityEvent = new ChangeResourceAvailabilityEvent(scenario.getScenarioID(), new HashMap<Resource, Integer>(), new Date(rs.getLong("THROWTIME")));
				tmpResourceAvailabilityEvent.setResourceAvailabilityMap(tmpNewResourceAvailabilityMap);
				tmpResourceAvailabilityEvent.setOldResourceAvailabilityMap(tmpOldResourceAvailabilityMap);
			}
			
			// New Resource Availability:
			tmpNewResourceAvailabilityMap.put(rs.getInt("RESOURCE_ID"), rs.getInt("NEW_AVAILABILITY"));
			
			// Old Resource Availability:
			tmpOldResourceAvailabilityMap.put(rs.getInt("RESOURCE_ID"), rs.getInt("OLD_AVAILABILITY"));
		}
		
		return tmpResourceAvailabilityEvent;
	}
	
	/**
	 * Läd ein MachineBreakDownsEvent.
	 * @param scenario
	 * @param eventDBID
	 * @return
	 * @throws SQLException
	 */
	private MachineBreakDownEvent getMachineBreakDownEvent(Scenario scenario, int eventDBID) throws SQLException {
		MachineBreakDownEvent tmpMachineBreakDownsEvent = null;
		List<Resource> tmpMachineBreakDownList = new ArrayList<Resource>();
		
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_MACHINE_BREAK_DOWN_EVENT.getStmt());
		
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, eventDBID);
		
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			// Event Erzeugen:
			if(tmpMachineBreakDownsEvent == null) {
				tmpMachineBreakDownsEvent = new MachineBreakDownEvent(scenario.getScenarioID(), tmpMachineBreakDownList, new Date(rs.getLong("THROWTIME")));
			}
			
			// MachineBreakDown hinzufügen:
			tmpMachineBreakDownList.add(scenario.getResources().get(rs.getInt("RESOURCE_ID")));
		}
		
		return tmpMachineBreakDownsEvent;
	}
	
	/**
	 * Läd ein MachineRepairedEvent.
	 * @param scenario
	 * @param eventDBID
	 * @return
	 * @throws SQLException
	 */
	private MachineRepairedEvent getMachineRepairedEvent(Scenario scenario, int eventDBID) throws SQLException {
		MachineRepairedEvent tmpMachineRepairedEvent = null;
		List<Resource> tmpMachineRepairedList = new ArrayList<Resource>();
		
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_MACHINE_REPAIRED_EVENT.getStmt());
		
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, eventDBID);
		
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			// Event Erzeugen:
			if(tmpMachineRepairedEvent == null) {
				tmpMachineRepairedEvent = new MachineRepairedEvent(scenario.getScenarioID(), tmpMachineRepairedList, new Date(rs.getLong("THROWTIME")));
			}
			
			// MachineBreakDown hinzufügen:
			tmpMachineRepairedList.add(scenario.getResources().get(rs.getInt("RESOURCE_ID")));
		}
		
		return tmpMachineRepairedEvent;
	}
	
	/**
	 * Läd ein MaintenancePeriodsEvent.
	 * @param scenario
	 * @param eventDBID
	 * @return
	 * @throws SQLException
	 */
	private MaintenancePeriodsEvent getMaintenancePeriodsEvent(Scenario scenario, int eventDBID) throws SQLException {
		MaintenancePeriodsEvent tmpMaintenancePeriodsEvent = null;
		List<ResourceRestriction> tmpResourceRestrictionList = new ArrayList<ResourceRestriction>();
		
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_MAINTENANCE_PERIODS_EVENT.getStmt());
		
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, eventDBID);
		
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			// Event erstellen:
			if(tmpMaintenancePeriodsEvent == null) {
				tmpMaintenancePeriodsEvent = new MaintenancePeriodsEvent(scenario.getScenarioID(), tmpResourceRestrictionList, new Date(rs.getLong("THROWTIME")));
			}
			
			// ResourceRestriction erstellen:
			tmpResourceRestrictionList.add(new ResourceRestriction(scenario.getScenarioID(), new Date(rs.getLong("START_TIME")), 
					new Date(rs.getLong("DUE_TIME")), rs.getInt("RESOURCE_ID")));
		}
		
		return tmpMaintenancePeriodsEvent;
	}
	
	/**
	 * Läd ein NewOrdersEvent.
	 * @param scenario
	 * @param eventDBID
	 * @return
	 * @throws SQLException
	 */
	private NewOrdersEvent getNewOrdersEvent(Scenario scenario, int eventDBID) throws SQLException {
		NewOrdersEvent tmpNewOrdersEvent = null;
		List<ScheduleOrder> tmpNewOrdersList = new ArrayList<ScheduleOrder>();
		
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_NEW_ORDERS_EVENT.getStmt());
		
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, eventDBID);
		
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			// Event erstellen:
			if(tmpNewOrdersEvent == null) {
				tmpNewOrdersEvent = new NewOrdersEvent(scenario.getScenarioID(), tmpNewOrdersList, new Date(rs.getLong("THROWTIME")));
			}
			
			// OrderList erstellen:
			ScheduleOrder tmpScheduleOrder = scenario.getOrder(rs.getInt("SCHEDULE_ORDER_ID"));
			if(tmpScheduleOrder == null) {
				tmpScheduleOrder = this.getScheduleOrder(scenario, rs.getInt("SCHEDULE_ORDER_ID"));
			}
			tmpNewOrdersList.add(tmpScheduleOrder);
		}
		
		return tmpNewOrdersEvent;
	}
	
	/**
	 * Läd ein NewProductsEvent.
	 * @param scenario
	 * @param eventDBID
	 * @return
	 * @throws SQLException
	 */
	private NewProductsEvent getNewProductsEvent(Scenario scenario, int eventDBID) throws SQLException {
		NewProductsEvent tmpNewProductsEvent = null;
		List<Product> tmpNewProductList = new ArrayList<Product>();
		
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_NEW_PRODUCTS_EVENT.getStmt());
		
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, eventDBID);
		
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			// Event erstellen:
			if(tmpNewProductsEvent == null) {
				tmpNewProductsEvent = new NewProductsEvent(scenario.getScenarioID(), tmpNewProductList, new Date(rs.getLong("THROWTIME")));
			}
			
			// Neues Product erstellen:
			Product tmpProduct = scenario.getProduct(rs.getInt("PRODUCT_ID"));
			if(tmpProduct == null) {
				this.getProduct(scenario, rs.getInt("PRODUCT_ID"));
			}
			tmpNewProductList.add(tmpProduct);
		}
		
		return tmpNewProductsEvent;
	}
	
	/**
	 * Läd ein NewResourceEvent.
	 * @param scenario
	 * @param eventDBID
	 * @return
	 * @throws SQLException
	 */
	private NewResourceEvent getNewResourceEvent(Scenario scenario, int eventDBID) throws SQLException {
		NewResourceEvent tmpNewResourceEvent = null;
		List<Resource> tmpResourceList = new ArrayList<Resource>();
		
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_NEW_RESOURCES_EVENT.getStmt());
		
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, eventDBID);
		
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			
			// Event erstellen:
			if(tmpNewResourceEvent == null) {
				tmpNewResourceEvent = new NewResourceEvent(scenario.getScenarioID(), tmpResourceList, new Date(rs.getLong("THROWTIME")));
			}
			
			// Resource hinzufügen:
			tmpResourceList.add(scenario.getResources().get(rs.getInt("RESOURCE_ID")));
			
		}
		
		
		return tmpNewResourceEvent;
	}
	
	private NewVariantsEvent getNewVariantsEvent(Scenario scenario, int eventDBID) throws SQLException {
		NewVariantsEvent tmpNewVariantsEvent = null;
		List<Variant> tmpNewVariantList = new ArrayList<Variant>();
		
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_NEW_VARIANTS_EVENT.getStmt());
		PreparedStatement stmtProductID = DBConnection.getPstmt(DBStatements.SELECT_PRODUCT_ID_BY_VARIANT_ID.getStmt());
		
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, eventDBID);
		
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			// Event erzeugen:
			if(tmpNewVariantsEvent == null) {
				tmpNewVariantsEvent = new NewVariantsEvent(scenario.getScenarioID(), tmpNewVariantList, new Date(rs.getLong("THROWTIME")));
			}
			// VariantList erzeugen:
			Variant tmpVariant = scenario.getVariant(rs.getInt("VARIANT_ID"));
			if(tmpVariant == null) {
				stmtProductID.setInt(1, scenario.getScenarioID());
				stmtProductID.setInt(2, rs.getInt("VARIANT_ID"));
				ResultSet rsProductID = stmtProductID.executeQuery();
				if(rs.next()) {
					tmpVariant = this.getVariant(scenario, rs.getInt("VARIANT_ID"), this.getProduct(scenario, rsProductID.getInt("PRODUCT_ID")));
				}

			}
			
			tmpNewVariantList.add(tmpVariant);
		}
		
		return tmpNewVariantsEvent;
	}
	
	/**
	 * Läd ein RemoveOrdersEvent.
	 * @param scenario
	 * @param eventDBID
	 * @return
	 * @throws SQLException
	 */
	private RemoveOrdersEvent getRemoveOrdersEvent(Scenario scenario, int eventDBID) throws SQLException {
		RemoveOrdersEvent tmpRemoveOrdersEvent = null;
		List<ScheduleOrder> tmpRemoveOrdersList = new ArrayList<ScheduleOrder>();
		
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_REMOVE_ORDERS_EVENT.getStmt());
		
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, eventDBID);
		
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			// Event erstellen:
			if(tmpRemoveOrdersEvent == null) {
				tmpRemoveOrdersEvent = new RemoveOrdersEvent(scenario.getScenarioID(), tmpRemoveOrdersList, new Date(rs.getLong("THROWTIME")));
			}
			
			// RemoveOrderList füllen:
			ScheduleOrder tmpSO = scenario.getOrder(rs.getInt("SCHEDULE_ORDER_ID"));
			if(tmpSO == null) {
				tmpSO = this.getScheduleOrder(scenario, rs.getInt("SCHEDULE_ORDER_ID"));
			}
			
			tmpRemoveOrdersList.add(tmpSO);
		}
		
		return tmpRemoveOrdersEvent;
	}
	
	/**
	 * Läd ein RemoveOrdersProductsEvent:
	 * @param scenario
	 * @param eventDBID
	 * @return
	 * @throws SQLException
	 */
	private RemoveOrdersProductsEvent getRemoveOrdersProductsEvent(Scenario scenario, int eventDBID) throws SQLException {
		RemoveOrdersProductsEvent tmpRemoveOrdersProductsEvent = null;
		Map<ScheduleOrder, List<Product>> tmpRemoveOrdersProductsMap = new HashMap<ScheduleOrder, List<Product>>();
		
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_REMOVE_ORDERS_PRODUCTS_EVENT.getStmt());
		
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, eventDBID);
		
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			// Event erstellen:
			if(tmpRemoveOrdersProductsEvent == null) {
				tmpRemoveOrdersProductsEvent = new RemoveOrdersProductsEvent(scenario.getScenarioID(), tmpRemoveOrdersProductsMap, new Date(rs.getLong("THROWTIME")));
			}
			
			// RemoveOrdersProductsMap füllen:
			
			// ScheduleOrder holen:
			ScheduleOrder tmpScheduleOrder = scenario.getOrder(rs.getInt("SCHEDULE_ORDER_ID"));
			if(tmpScheduleOrder == null) {
				tmpScheduleOrder = this.getScheduleOrder(scenario, rs.getInt("SCHEDULE_ORDER_ID"));
			}
			
			// Product holen:
			Product tmpProduct = scenario.getProduct(rs.getInt("PRODUCT_ID"));
			if(tmpProduct == null) {
				tmpProduct = this.getProduct(scenario, rs.getInt("PRODUCT_ID"));
			}
			
			// AMOUNT * in die List:
			if(tmpRemoveOrdersProductsMap.get(tmpScheduleOrder) == null) {
				tmpRemoveOrdersProductsMap.put(tmpScheduleOrder, new ArrayList<Product>());
			}
			for(int i = 0; i < rs.getInt("AMOUNT"); i++) {
				tmpRemoveOrdersProductsMap.get(tmpScheduleOrder).add(tmpProduct);
			}
		}
		
		return tmpRemoveOrdersProductsEvent;
	}
	
	/**
	 * Läd ein RemoveProductsEvent:
	 * @param scenario
	 * @param eventDBID
	 * @return
	 * @throws SQLException
	 */
	private RemoveProductsEvent getRemoveProductsEvent(Scenario scenario, int eventDBID) throws SQLException {
		RemoveProductsEvent tmpRemoveProductsEvent = null;
		List<Product> tmpRemoveProductList = new ArrayList<Product>();
		
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_REMOVE_PRODUCTS_EVENT.getStmt());
		
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, eventDBID);
		
		ResultSet rs = stmt.executeQuery();
		while(rs.next()){
			// Event erstellen:
			if(tmpRemoveProductsEvent == null) {
				tmpRemoveProductsEvent = new RemoveProductsEvent(scenario.getScenarioID(), tmpRemoveProductList, new Date(rs.getLong("THROWTIME")));
			}
			
			// RemoveProductList erstellen:
			Product tmpProduct = scenario.getProduct(rs.getInt("PRODUCT_ID"));
			if(tmpProduct == null) {
				this.getProduct(scenario, rs.getInt("PRODUCT_ID"));
			}
			
			tmpRemoveProductList.add(tmpProduct);
		}
		
		return tmpRemoveProductsEvent;
	}
	
	/**
	 * Läd ein RemoveVariantsEvent.
	 * @param scenario
	 * @param eventDBID
	 * @return
	 * @throws SQLException
	 */
	private RemoveVariantsEvent getRemoveVariantsEvent(Scenario scenario, int eventDBID) throws SQLException {
		RemoveVariantsEvent tmpRemoveVariantsEvent = null;
		List<Variant> tmpRemoveVariantList = new ArrayList<Variant>();
		
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_REMOVE_VARIANTS_EVENT.getStmt());
		PreparedStatement stmtProductID = DBConnection.getPstmt(DBStatements.SELECT_PRODUCT_ID_BY_VARIANT_ID.getStmt());
		
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, eventDBID);
		
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			// Event erstellen:
			if(tmpRemoveVariantsEvent == null) {
				tmpRemoveVariantsEvent = new RemoveVariantsEvent(scenario.getScenarioID(), tmpRemoveVariantList, new Date(rs.getLong("THROWTIME")));
			}
			
			// RemoveVariantList erstellen:
			Variant tmpVariant = scenario.getVariant(rs.getInt("VARIANT_ID"));
			if(tmpVariant == null) {
				stmtProductID.setInt(1, scenario.getScenarioID());
				stmtProductID.setInt(2, rs.getInt("VARIANT_ID"));
				ResultSet rsProductID = stmtProductID.executeQuery();
				if(rsProductID.next()) {
					tmpVariant = this.getVariant(scenario, rs.getInt("VARIANT_ID"), this.getProduct(scenario, rsProductID.getInt("PRODUCT_ID")));
				}
			}
			
			tmpRemoveVariantList.add(tmpVariant);
		}
		
		return tmpRemoveVariantsEvent;
	}
	
	/**
	 * Läd ein ScheduleEvent aus der Datenbank.
	 * @param scenario
	 * @param eventTyp
	 * @param eventDBID
	 * @return
	 * @throws SQLException 
	 */
	private ScheduleEvent getScheduleEvent(Scenario scenario, int eventTyp, int eventDBID) throws SQLException {

		switch(eventTyp) {
		
		case ScheduleEvent.TYPE_CHANGE_OPERATION_RESOURCES_EVENT:
			
			return this.getChangeOperationResourcesEvent(scenario, eventDBID);
			
		case ScheduleEvent.TYPE_CHANGE_ORDERS_DUE_TIMES_EVENT:
			
			return this.getChangeOrdersDueTimesEvent(scenario, eventDBID);
			
		case ScheduleEvent.TYPE_CHANGE_ORDERS_PRIORITY_EVENT:
			
			return this.getChangeOrdersPriorityEvent(scenario, eventDBID);
			
		case ScheduleEvent.TYPE_CHANGE_RESOURCE_AVAILABILITY_EVENT:
			
			return this.getChangeResourceAvailabilityEvent(scenario, eventDBID);
			
		case ScheduleEvent.TYPE_MACHINE_BREAK_DOWNS_EVENT:
			
			return this.getMachineBreakDownEvent(scenario, eventDBID);
			
		case ScheduleEvent.TYPE_MACHINES_REPAIRED_EVENT:
			
			return this.getMachineRepairedEvent(scenario, eventDBID);
			
		case ScheduleEvent.TYPE_MAINTENACE_PERIODS_EVENT:	
		
			return this.getMaintenancePeriodsEvent(scenario, eventDBID);
			
		case ScheduleEvent.TYPE_NEW_ORDERS_EVENT:	
			
			return this.getNewOrdersEvent(scenario, eventDBID);
			
		case ScheduleEvent.TYPE_NEW_PRODUCTS_EVENT:
			
			return this.getNewProductsEvent(scenario, eventDBID);
			
		case ScheduleEvent.TYPE_NEW_RESOURCE_EVENT:	
			
			return this.getNewResourceEvent(scenario, eventDBID);
			
		case ScheduleEvent.TYPE_NEW_VARIANTS_EVENT:
			
			return this.getNewVariantsEvent(scenario, eventDBID);
			
		case ScheduleEvent.TYPE_REMOVE_ORDERS_EVENT:
			
			return this.getRemoveOrdersEvent(scenario, eventDBID);
			
		case ScheduleEvent.TYPE_REMOVE_ORDERS_PRODUCTS_EVENT:
			
			return this.getRemoveOrdersProductsEvent(scenario, eventDBID);
			
		case ScheduleEvent.TYPE_REMOVE_PRODUCTS_EVENT:
			
			return this.getRemoveProductsEvent(scenario, eventDBID);
			
		case ScheduleEvent.TYPE_REMOVE_VARIANTS_EVENT:
			
			return this.getRemoveVariantsEvent(scenario, eventDBID);
			
		case ScheduleEvent.TYPE_SCHEDULE_CHANGE_BY_USER_EVENT:
			
			return this.getScheduleChangeByUserEvent(scenario, eventDBID);
			
		default:
			
			return null;
			
		}
	}
	
	public Schedule getSchedule(Scenario scenario, int scheduleID) throws SQLException {
		
		if(this.scenarioMap.get(scenario.getScenarioID()) == null) {
			this.scenarioMap.put(scenario.getScenarioID(), new Object());
		}
		
		synchronized(this.scenarioMap.get(scenario.getScenarioID())) {
			Schedule tmpSchedule = null;
			
			PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_SCHEDULE.getStmt());
			PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_ALGORITHM_INFORMATIONS.getStmt());
			PreparedStatement stmt3 = DBConnection.getPstmt(DBStatements.SELECT_SCHEDULE_KEY_FIGURES.getStmt());
			
			// Schedule und Algorithm Information Parameter setzen:
			stmt.setInt(1, scenario.getScenarioID());
			stmt.setInt(2, scheduleID);
			stmt2.setInt(1, scenario.getScenarioID());
			stmt2.setInt(2, scheduleID);
			stmt3.setInt(1, scenario.getScenarioID());
			
			ResultSet rsSchedule = stmt.executeQuery();
			if(rsSchedule.next()) {
				ResultSet rsAlgorithmInformations = stmt2.executeQuery();
				if(rsAlgorithmInformations.next()) {
					// Initialer Schedule (Kein Event eingegangen):
					if(rsSchedule.getInt("EVENT_ID") == -1) {
						
						tmpSchedule = new Schedule(scenario.getScenarioID(), scheduleID, this.getPlannedVariants(scenario, scheduleID), null);
						tmpSchedule.setAlgorithmInformations(new AlgorithmInformation(rsAlgorithmInformations.getString("ALGORITHM_NAME"), 
								new Date(rsAlgorithmInformations.getLong("START_TIME")), new Date(rsAlgorithmInformations.getLong("DUE_TIME"))));
						
					// Durch Event erzeugter Schedule:	
					} else {
						
						tmpSchedule = new Schedule(scenario.getScenarioID(), scheduleID, this.getPlannedVariants(scenario, scheduleID), 
								this.getScheduleEvent(scenario, rsSchedule.getInt("EVENT_TYP"), rsSchedule.getInt("EVENT_ID")));
						tmpSchedule.setAlgorithmInformations(new AlgorithmInformation(rsAlgorithmInformations.getString("ALGORITHM_NAME"), 
								new Date(rsAlgorithmInformations.getLong("START_TIME")), new Date(rsAlgorithmInformations.getLong("DUE_TIME")), 
								rsAlgorithmInformations.getInt("INITIAL_SCHEDULE_ID")));
						
					}
				}
				// Schedule KeyFigures setzen:
				Map<Integer, Integer> tmpScheduleKeyFigures = new HashMap<Integer, Integer>();
				stmt3.setInt(2, scheduleID);
				
				ResultSet rsScheduleKeyFigures = stmt3.executeQuery();
				while(rsScheduleKeyFigures.next()) {
					tmpScheduleKeyFigures.put(rsScheduleKeyFigures.getInt("KEY_FIGURE_TYPE"), rsScheduleKeyFigures.getInt("VALUE"));
				}
				
				tmpSchedule.setKeyFigureValueMap(tmpScheduleKeyFigures);
			}
			
			return tmpSchedule;
		}
	}
	
	private ScheduleOrder getScheduleOrder(Scenario scenario, int scheduleOrderID) throws SQLException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_SCHEDULE_ORDER.getStmt());
		PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_ORDER_PRODUCT_LIST.getStmt());

		stmt.setInt(1, scenario.getScenarioID());
		stmt2.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, scheduleOrderID);
		stmt2.setInt(2, scheduleOrderID);

		// Order holen:
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {

			// Order Product List holen:
			List<Integer> tmpScheduleOrderProductList = new ArrayList<Integer>();
			ResultSet rsOrderProductList = stmt2.executeQuery();
			while(rsOrderProductList.next()) {
				// Jedes Produkt in der richtigen Anzahl hinzufüen:
				for(int i = 0; i < rsOrderProductList.getInt("AMOUNT"); i++) {
					tmpScheduleOrderProductList.add(rsOrderProductList.getInt("PRODUCT_ID"));
				}
			}

			return new ScheduleOrder(scheduleOrderID, scenario.getScenarioID(), 
					rs.getString("NAME"), tmpScheduleOrderProductList, new Date(0), new Date(rs.getLong("EARLIST_DUE_TIME")), rs.getInt("PRIORITY"));

		} 
		
		return null;
	}
	
	/**
	 * Läd alle aktuellen ScheduleOrder.
	 * @param scheduleOrderIDList
	 * @return
	 * @throws SQLException 
	 */
	private List<ScheduleOrder> getScheduleOrders(Scenario scenario) throws SQLException {
		List<ScheduleOrder> tmpScheduleOrderList = new ArrayList<ScheduleOrder>();

		if(scenario.getChosenSchedule() != null) {
			
			// Alle im aktuellen Schedule geplanten Orders laden:
			for(Integer scheduleOrderID : scenario.getChosenSchedule().getPlannedOrders()) {
				ScheduleOrder tmpScheduleOrder = this.getScheduleOrder(scenario, scheduleOrderID);
				if(tmpScheduleOrder != null) {
					tmpScheduleOrderList.add(tmpScheduleOrder);
				}
			}
		}
		
		return tmpScheduleOrderList;
	}
	
	/**
	 * Läd alle Resources zu einem Scenario.
	 * Dabei werden nur ResourceRestrictions geladen, die noch aktuell sind!
	 * @param scenario
	 * @return
	 * @throws SQLException
	 */
	private List<Resource> getResources(Scenario scenario) throws SQLException {
		List<Resource> tmpResourceList = new ArrayList<Resource>();
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_RESOURCES.getStmt());
		PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_RESOURCE_RESTRICTIONS_AFTER_DATE.getStmt());
		
		// Alle Resources holen:
		stmt.setInt(1, scenario.getScenarioID());
		stmt2.setInt(1, scenario.getScenarioID());
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			Resource r = new Resource(scenario.getScenarioID(), rs.getInt("RESOURCE_ID"), rs.getString("NAME"), rs.getInt("AVAILABILITY"));
			
			// Resource Restriction holen:
			stmt2.setInt(2, r.getResourceID());
			if(scenario.getChosenSchedule() == null) {
				stmt2.setLong(3, new Date(0).getTime());
			}

			ResultSet rsRR = stmt2.executeQuery();
			while(rsRR.next()) {
				r.addRestriction(new ResourceRestriction(scenario.getScenarioID(), 
						new Date(rsRR.getLong("START_TIME")), new Date(rsRR.getLong("DUE_TIME")), 
						r.getResourceID()));
			}
			
			tmpResourceList.add(r);
		}
		
		return tmpResourceList;
	}
	
	/**
	 * Gibt ein Product zu einer productID aus.
	 * @param scenario
	 * @param productID
	 * @return
	 * @throws SQLException
	 */
	private Product getProduct(Scenario scenario, int productID) throws SQLException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_PRODUCT.getStmt());
		
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, productID);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			Product p = new Product(scenario.getScenarioID(), productID, rs.getString("NAME"));
			p.setVariants(this.getVariants(scenario, p));
			
			return p;
		}
		
		return null;
	}
	
	/**
	 * Läd die Products zu einem Scenario.
	 * Mit den dazugehörigen Variants und Operations.
	 * @param scenario
	 * @return
	 * @throws SQLException
	 */
	private List<Product> getProducts(Scenario scenario) throws SQLException {
		List<Product> tmpProductList = new ArrayList<Product>();
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_PRODUCTS.getStmt());
	
		stmt.setInt(1, scenario.getScenarioID());
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			
			Product p = new Product(scenario.getScenarioID(), rs.getInt("PRODUCT_ID"), rs.getString("NAME"));
			p.addVariants(this.getVariants(scenario, p));
			tmpProductList.add(p);
		}
		
		return tmpProductList;
	}
	
	/**
	 * Läd eine Variant anhand der VariantID
	 * @param scenario
	 * @param variantID
	 * @return
	 * @throws SQLException
	 */
	private Variant getVariant(Scenario scenario, int variantID, Product product) throws SQLException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_VARIANT.getStmt());
		
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, variantID);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
				return new Variant(rs.getInt("VARIANT_ID"), this.getOperationList(scenario, rs.getInt("VARIANT_ID")), product);
		}
		
		return null;
	}
	
	
	
	/**
	 * Läd alle MÖGLICHEN Varianten zu einem Product.
	 * Die Products müssen sich bereits im Scenario befinden.
	 * @param scenario
	 * @return
	 * @throws SQLException 
	 */
	private List<Variant> getVariants(Scenario scenario, Product product) throws SQLException {
		List<Variant> tmpVariantList = new ArrayList<Variant>();
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_VARIANTS_BY_PRODUCT_ID.getStmt());
		
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, product.getProductID());
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {

			tmpVariantList.add(new Variant(rs.getInt("VARIANT_ID"), this.getOperationList(scenario, rs.getInt("VARIANT_ID")), product));
 		}
		
		return tmpVariantList;
	}

	/**
	 * Läd alle Operationen zu einer Variante.
	 * @param scenario
	 * @param variantID
	 * @return
	 * @throws SQLException
	 */
	private List<Operation> getOperationList(Scenario scenario, int variantID) throws SQLException {
		List<Operation> tmpOperationList = new ArrayList<Operation>();
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_OPERATION.getStmt());
		PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_OPERATION_PREDECESSORS.getStmt());
		PreparedStatement stmt3 = DBConnection.getPstmt(DBStatements.SELECT_OPERATION_RESOURCES.getStmt());
		
		// Operations laden:
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, variantID);
		ResultSet rs = stmt.executeQuery();
		
		while(rs.next()) {
			
			// Resource Alternativen laden:
			List<Integer> tmpResourceAlternatives = new ArrayList<Integer>();
			stmt3.setInt(1, scenario.getScenarioID());
			stmt3.setInt(2, rs.getInt("OPERATION_ID"));
			ResultSet rsResources = stmt3.executeQuery();
			while(rsResources.next()) {
				tmpResourceAlternatives.add(rsResources.getInt("RESOURCE_ID"));
			}
			
			// Operation hinzufügen:
			tmpOperationList.add(new Operation(scenario.getScenarioID(), rs.getInt("OPERATION_ID"), 
					tmpResourceAlternatives, rs.getInt("DURATION"), rs.getInt("VARIANT_ID"), rs.getString("NAME")));
		}
		
		// Predecessors beachten:
		stmt2.setInt(1, scenario.getScenarioID());
		for(Operation op : tmpOperationList) {
			List<Operation> tmpOperationPredecessorsList = new ArrayList<Operation>();
			
			stmt2.setInt(2, op.getOperationID());
			ResultSet rsPredecessors = stmt2.executeQuery();
			
			while(rsPredecessors.next()) {
				int tmpOpPreID = rsPredecessors.getInt("OPERATION_PRE_ID");
				for(Operation opPre : tmpOperationList) {
					if(opPre.getOperationID() == tmpOpPreID) {
						tmpOperationPredecessorsList.add(opPre);
						break;
					}
				}
			}
			
			op.setPredecessors(tmpOperationPredecessorsList);
		}
		
		return tmpOperationList;
	}
	
	/**
	 * Läd alle PlannedVariants zu einem Schedule.
	 * @param scenario
	 * @param scheduleID
	 * @return
	 * @throws SQLException
	 */
	private List<PlannedVariant> getPlannedVariants(Scenario scenario, int scheduleID) throws SQLException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.SELECT_PLANNED_VARIANTS.getStmt());
		PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_ALLOCATION.getStmt());
		PreparedStatement stmt3 = DBConnection.getPstmt(DBStatements.SELECT_ALLOCATION_PREDECESSOR.getStmt());
		PreparedStatement stmtSelectProductIdByVariantID = DBConnection.getPstmt(DBStatements.SELECT_PRODUCT_ID_BY_VARIANT_ID.getStmt());
		
		List<PlannedVariant> tmpPVList = new ArrayList<PlannedVariant>();
		
		stmt2.setInt(1, scenario.getScenarioID());
		stmt3.setInt(1, scenario.getScenarioID());
		
		// Alle PlannedVariants holen:
		stmt.setInt(1, scenario.getScenarioID());
		stmt.setInt(2, scheduleID);
		
		ResultSet rs = stmt.executeQuery();
		
		// Alle PV durchlaufen:
		while(rs.next()) {
			
			Variant tmpVariant = scenario.getVariant(rs.getInt("VARIANT_ID"));
			// Variante gelöscht?
			if(tmpVariant == null) {
				
				// ProductID holen:
				stmtSelectProductIdByVariantID.setInt(1, scenario.getScenarioID());
				stmtSelectProductIdByVariantID.setInt(2, rs.getInt("VARIANT_ID"));
				ResultSet rsProductID = stmtSelectProductIdByVariantID.executeQuery();
				if(rsProductID.next()) {
					// Product da oder gelöscht?
					Product tmpProduct = scenario.getProduct(rsProductID.getInt("PRODUCT_ID"));
					if(tmpProduct == null) {
						tmpVariant = this.getVariant(scenario, rs.getInt("VARIANT_ID"),tmpProduct);
					} else {
						tmpVariant = this.getVariant(scenario, rs.getInt("VARIANT_ID"), this.getProduct(scenario, rsProductID.getInt("PRODUCT_ID")));
					}

				}
			}
			
			// AllocationList erstellen:
			List<Allocation> tmpAllocationList = new ArrayList<Allocation>();
			
			stmt2.setInt(2, rs.getInt("PLANNED_VARIANT_ID"));
			ResultSet rsAllocations = stmt2.executeQuery();
			while(rsAllocations.next()) {
				
				// AllocationPredecessors beachten:
				List<Allocation> tmpAllocationPredecessors = new ArrayList<Allocation>();
				stmt3.setInt(2, rsAllocations.getInt("ALLOCATION_ID"));
				stmt3.setInt(3, scheduleID);
				ResultSet rsAllocationPre = stmt3.executeQuery();
				while(rsAllocationPre.next()) {
					for(Allocation tmpAllocationPredecessor : tmpAllocationList) {
						if(rsAllocationPre.getInt("ALLOCATION_PRE_ID") == tmpAllocationPredecessor.getUID()) {
							tmpAllocationPredecessors.add(tmpAllocationPredecessor);
						}
					}
				}

				// Allocation erzeugen:
				tmpAllocationList.add(new Allocation(rsAllocations.getInt("ALLOCATION_ID"), new Date(rsAllocations.getLong("START_TIME")), 
						tmpVariant.getOperation(rsAllocations.getInt("OPERATION_ID")), rsAllocations.getInt("SCHEDULE_ORDER_ID"), 
						tmpAllocationPredecessors, rsAllocations.getInt("RESOURCE_ID")));
			}
			
			
			// PlannedVariant erzeugen:
			PlannedVariant tmpPV = new PlannedVariant(tmpVariant, tmpAllocationList);
			tmpPV.setPlannedVariantID(rs.getInt("PLANNED_VARIANT_ID"));
			tmpPV.setScenarioID(scenario.getScenarioID());
			tmpPV.setScheduleID(scheduleID);
			
			tmpPVList.add(tmpPV);
		}
		
		return tmpPVList;
	}
	
	/**
	 * 
	 * @param scenario
	 * @return scenario_ID
	 * @throws SQLException
	 */
	public synchronized int insertNewScenario(Scenario scenario) throws SQLException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_SCENARIO.getStmt());
		PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_LAST_SCENARIO.getStmt());
		List<Resource> tmpResourceList = new ArrayList<Resource>();
		List<Product> tmpProductList = new ArrayList<Product>();
		int tmpScenarioID = -1;
		
		// Variablen binden
		stmt.setInt(1, scenario.getAuthorID());
		stmt.setInt(2, -1);
		stmt.setString(3, scenario.getName());
		stmt.setInt(4, scenario.getNewProductIDCount());
		stmt.setInt(5, scenario.getNewOrderIDCount());
		stmt.setInt(6, scenario.getNewVariantIDCount());
		stmt.setInt(7, scenario.getNewOperationIDCount());
		stmt.setInt(8, scenario.getNewResourceIDCount());
		stmt.setInt(9, scenario.getScheduleChangeDeadline());
		stmt.setInt(10, scenario.getSecondsBetweenAllocations());
		
		// Statement ausführen
		stmt.execute();
		
		// Überall die ScenarioID richtig setzen:
		ResultSet rs = stmt2.executeQuery();
		while(rs.next()) {
			if(tmpScenarioID == -1) {
				tmpScenarioID = rs.getInt("SCENARIO_ID");
			}
			
			scenario.setScenarioID(tmpScenarioID);
		}
		
		// ScenarioIDs setzen::
		for(Integer productID : scenario.getProducts().keySet()) {
			Product p = scenario.getProduct(productID);
			p.setScenarioID(scenario.getScenarioID());
			
			// Für Später das Product speichern:
			tmpProductList.add(p);
			
			// Varianten:
			for(Variant v : p.getVariants()) {
				v.setScenarioID(scenario.getScenarioID());
				
				// Operation:
				for(Operation op : v.getOperations()) {
					op.setScenarioID(scenario.getScenarioID());
				}
			}
		}
		
		// Resources:
		for(Integer resourceID : scenario.getResources().keySet()) {
			Resource r = scenario.getResources().get(resourceID);
			r.setScenarioID(scenario.getScenarioID());
			
			// Für später speichern:
			tmpResourceList.add(r);
			
			// ResourceRestrictions:
			for(ResourceRestriction rr : r.getResourceRestrictions()) {
				rr.setScenarioID(scenario.getScenarioID());
			}
		}
		
		// Schedules:
		for(Schedule s : scenario.getCurrentSchedules()) {
			s.setScenarioID(scenario.getScenarioID());
			
			for(PlannedVariant pv : s.getPlannedVariants()) {
				pv.setScenarioID(scenario.getScenarioID());
				
				for(Allocation a : pv.getAllocationList()) {
					a.setScenarioID(scenario.getScenarioID());
				}
			}
		}
		for(Schedule s : scenario.getOldSchedules()) {
			s.setScenarioID(scenario.getScenarioID());
			
			for(PlannedVariant pv : s.getPlannedVariants()) {
				pv.setScenarioID(scenario.getScenarioID());
				
				for(Allocation a : pv.getAllocationList()) {
					a.setScenarioID(scenario.getScenarioID());
				}
			}
		}
		
		// Orders (falls es welche gibt)
		for(ScheduleOrder so : scenario.getOrders()) {
			so.setScenarioID(scenario.getScenarioID());
		}
		
		// Daten einfügen:
		
		/*
		 * Alle Resources in die DB:
		 * (ResourceRestrictions werden automatisch mit hinzugefügt)
		 */
		this.insertNewResources(tmpResourceList);
		
		/*
		 * Alle Products in die DB:
		 * (Variants und Operations werden automatisch mit hinzugefügt)
		 */
		this.insertNewProducts(tmpProductList);
		
		/*
		 * KeyFigures speichern
		 */
		this.insertNewKeyFigureList(scenario.getScenarioID(), scenario.getKeyFigureList());
		
		/*
		 * ScheduleOrders speichern:
		 * (Normalerweise sollte es keine geben)
		 */
		this.insertNewScheduleOrders(scenario.getOrders());
		
		/*
		 * Schedules speichern:
		 */
		if(!scenario.getCurrentSchedules().isEmpty()) {
			this.insertCurrentSchedules(scenario, scenario.getChosenSchedule(), scenario.getCurrentSchedules());
		}

		/* 
		 * Counter erhöhen:
		 */
		this.setScenarioCounters(scenario);
		
		return tmpScenarioID;
	}
	
	/**
	 * Speichert ein ScheduleChangeByUserEvent.
	 * @param event
	 * @return
	 * @throws SQLException
	 */
	private int insertNewScheduleChangeByUserEvent(ScheduleChangeByUserEvent event) throws SQLException {
		int eventDBID = 0;
		
		if(this.scenarioMap.get(event.getScenarioID()) == null) {
			this.scenarioMap.put(event.getScenarioID(), new Object());
		}
		
		synchronized(this.scenarioMap.get(event.getScenarioID())) {
			PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_SCHEDULE_CHANGE_BY_USER_EVENT.getStmt());
			PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_LAST_SCHEDULE_CHANGE_BY_USER_EVENT_ID.getStmt());
			
			// eventDBID setzen:
			stmt2.setInt(1, event.getScenarioID());
			ResultSet rs = stmt2.executeQuery();
			if(rs.next()) {
				eventDBID = rs.getInt("EVENT_ID") +1;
			}
			
			// Event speichern:
			stmt.setInt(1, event.getScenarioID());
			stmt.setInt(2, eventDBID);
			stmt.setLong(3, event.getThrowTime().getTime());
			stmt.execute();
		}
		
		return eventDBID;
	}
	
	/**
	 * Fügt ein neues RemoveOrdersProductsEvent hinzu.
	 * Ändert auch die Datenbank.
	 * @param event
	 * @return EVENT_ID
	 * @throws SQLException
	 */
	private int insertNewRemoveOrdersProductsEvent(RemoveOrdersProductsEvent event) throws SQLException {
		int eventDBID = 0;
		
		if(this.scenarioMap.get(event.getScenarioID()) == null) {
			this.scenarioMap.put(event.getScenarioID(), new Object());
		}
		
		synchronized(this.scenarioMap.get(event.getScenarioID())) {
			PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_REMOVE_ORDERS_PRODUCTS_EVENT.getStmt());
			PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_LAST_REMOVE_ORDERS_PRODUCTS_EVENT_ID.getStmt());
			PreparedStatement stmt3 = DBConnection.getPstmt(DBStatements.UPDATE_ORDER_PRODUCTS_LIST.getStmt());
			
			// eventDBID setzen:
			stmt2.setInt(1, event.getScenarioID());
			ResultSet rs = stmt2.executeQuery();
			if(rs.next()) {
				eventDBID = rs.getInt("EVENT_ID") +1;
			}
			
			stmt.setInt(1, event.getScenarioID());
			stmt3.setInt(2, event.getScenarioID());
			stmt.setInt(2, eventDBID);
			stmt.setLong(6, event.getThrowTime().getTime());
			
			for(ScheduleOrder so : event.getRemoveMap().keySet()) {
				stmt.setInt(4, so.getOrderID());
				stmt3.setInt(4, so.getOrderID());
				
				// Temp. Map für die produkte erzeugen:
				Map<Integer, Integer> tmpProductMap = new HashMap<Integer, Integer>();
				for(Product p : event.getRemoveMap().get(so)) {
					if(tmpProductMap.get(p) == null) {
						tmpProductMap.put(p.getProductID(), 0);
					}
					tmpProductMap.put(p.getProductID(), tmpProductMap.get(p.getProductID()) +1);
				}
				
				// Product Map durchlaufen:
				for(Integer productID : tmpProductMap.keySet()) {
					stmt.setInt(3, productID);
					stmt.setInt(5, tmpProductMap.get(productID));
					
					stmt.execute();
					
					// Änderungen bewirken:
					stmt3.setInt(1, tmpProductMap.get(productID));
					stmt3.setInt(3, productID);
					
					stmt3.execute();
				}
			}
			
		}
		
		return eventDBID;
	}
	
	/**
	 * Fügt ein neues RemoveVariantsEvent hinzu.
	 * Die Variants werden in der DB als gelöscht markiert!
	 * @param event
	 * @return EVENT_ID
	 * @throws SQLException
	 */
	private int insertNewRemoveVariantsEvent(RemoveVariantsEvent event) throws SQLException {
		int eventDBID = 0;
		
		if(this.scenarioMap.get(event.getScenarioID()) == null) {
			this.scenarioMap.put(event.getScenarioID(), new Object());
		}
		
		synchronized(this.scenarioMap.get(event.getScenarioID())) {
			PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_REMOVE_VARIANTS_EVENT.getStmt());
			PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_LAST_REMOVE_VARIANTS_EVENT_ID.getStmt());
			PreparedStatement stmt3 = DBConnection.getPstmt(DBStatements.MARK_VARIANT_AS_REMOVED.getStmt());
			
			// eventDBID setzen:
			stmt2.setInt(1, event.getScenarioID());
			ResultSet rs = stmt2.executeQuery();
			if(rs.next()) {
				eventDBID = rs.getInt("EVENT_ID") +1;
			}
			
			stmt.setInt(1, event.getScenarioID());
			stmt3.setInt(1, event.getScenarioID());
			stmt.setInt(2, eventDBID);
			stmt.setLong(4, event.getThrowTime().getTime());
			
			for(Variant v : event.getRemovedVariants()) {
				stmt.setInt(3, v.getVariantID());
				
				stmt.execute();
				
				// Variante als gelöscht markieren:
				stmt3.setInt(2, v.getVariantID());
				
				stmt3.execute();
			}
		}
		
		return eventDBID;
	}
	
	/**
	 * Fügt ein neues RemoveProductsEvent ein.
	 * Das Product wird in der DB als gelöscht markiert!
	 * @param event
	 * @return EVENT_ID
	 * @throws SQLException
	 */
	private int insertNewRemoveProductsEvent(RemoveProductsEvent event) throws SQLException {
		int eventDBID = 0;
		
		if(this.scenarioMap.get(event.getScenarioID()) == null) {
			this.scenarioMap.put(event.getScenarioID(), new Object());
		}
		
		synchronized(this.scenarioMap.get(event.getScenarioID())) {
			PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_REMOVE_PRODUCTS_EVENT.getStmt());
			PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_LAST_REMOVE_PRODUCTS_EVENT_ID.getStmt());
			PreparedStatement stmt3 = DBConnection.getPstmt(DBStatements.MARK_PRODUCT_AS_REMOVED.getStmt());
			
			// eventDBID setzen:
			stmt2.setInt(1, event.getScenarioID());
			ResultSet rs = stmt2.executeQuery();
			if(rs.next()) {
				eventDBID = rs.getInt("EVENT_ID") +1;
			}
			
			stmt.setInt(1, event.getScenarioID());
			stmt3.setInt(1, event.getScenarioID());
			stmt.setInt(2, eventDBID);
			stmt.setLong(4, event.getThrowTime().getTime());
			
			for(Product p : event.getRemovedProducts()) {
				stmt.setInt(3, p.getProductID());
				
				stmt.execute();
				
				//Product als gelöscht markieren:
				stmt3.setInt(2, p.getProductID());
				
				stmt3.execute();
			}
			
		}
		
		return eventDBID;
	}
	
	/**
	 * Fügt ein neues RemoveOrdersEvent hinzu.
	 * Markiert die Orders in der DB als gelöscht!
	 * @param event
	 * @return EVENT_ID
	 * @throws SQLException
	 */
	private int insertNewRemoveOrdersEvent(RemoveOrdersEvent event) throws SQLException {
		int eventDBID = 0;
		
		if(this.scenarioMap.get(event.getScenarioID()) == null) {
			this.scenarioMap.put(event.getScenarioID(), new Object());
		}
		
		synchronized(this.scenarioMap.get(event.getScenarioID())) {
			PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_REMOVE_ORDERS_EVENT.getStmt());
			PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_LAST_REMOVE_ORDERS_EVENT_ID.getStmt());
			PreparedStatement stmt3 = DBConnection.getPstmt(DBStatements.MARK_SCHEDULE_ORDER_AS_REMOVED.getStmt());
			
			// eventDBID setzen:
			stmt2.setInt(1, event.getScenarioID());
			ResultSet rs = stmt2.executeQuery();
			if(rs.next()) {
				eventDBID = rs.getInt("EVENT_ID") +1;
			}
			
			stmt.setInt(1, event.getScenarioID());
			stmt3.setInt(1, event.getScenarioID());
			stmt.setInt(2, eventDBID);
			stmt.setLong(4, event.getThrowTime().getTime());
			
			for(ScheduleOrder so : event.getRemoveOrdersList()) {
				stmt.setInt(3, so.getOrderID());
				
				stmt.execute();
				
				// Als gelöscht markieren:
				stmt3.setInt(2, so.getOrderID());
				
				stmt3.execute();
			}
		}
		
		return eventDBID;
	}
	
	/**
	 * Fügt ein neues NewVariantsEvent ein.
	 * Fügt die neuen Variants auch in die DB ein!
	 * @param event
	 * @return EVENT_ID
	 * @throws SQLException
	 */
	private int insertNewVariantsEvent(NewVariantsEvent event) throws SQLException {
		int eventDBID = 0;
		
		if(this.scenarioMap.get(event.getScenarioID()) == null) {
			this.scenarioMap.put(event.getScenarioID(), new Object());
		}
		
		synchronized(this.scenarioMap.get(event.getScenarioID())) {
			PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_VARIANT_EVENT.getStmt());
			PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_LAST_NEW_VARIANT_EVENT_ID.getStmt());
			
			// eventDBID holen:
			stmt2.setInt(1, event.getScenarioID());
			ResultSet rs = stmt2.executeQuery();
			if(rs.next()) {
				eventDBID = rs.getInt("EVENT_ID") +1;
			}
			
			stmt.setInt(1, event.getScenarioID());
			stmt.setInt(2, eventDBID);
			stmt.setLong(4, event.getThrowTime().getTime());
			
			for(Variant v : event.getNewVariants()) {
				stmt.setInt(3, v.getVariantID());
				
				stmt.execute();
				
				// Neue Variante in die DB einfügen:
				this.insertNewVariant(v);
			}
		}
		
		return eventDBID;
	}
	
	/**
	 * Fügt ein neues NewResourceEvent ein.
	 * Fügt die Resource auch in die DB ein!
	 * @param event
	 * @return EVENT_ID
	 * @throws SQLException
	 */
	private int insertNewResourceEvent(NewResourceEvent event) throws SQLException {
		int eventDBID = 0;
		
		if(this.scenarioMap.get(event.getScenarioID()) == null) {
			this.scenarioMap.put(event.getScenarioID(), new Object());
		}
		
		synchronized(this.scenarioMap.get(event.getScenarioID())) {
			PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_RESOURCES_EVENT.getStmt());
			PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_LAST_NEW_RESOURCE_ID.getStmt());
			
			// eventDBID
			stmt2.setInt(1, event.getScenarioID());
			ResultSet rs = stmt2.executeQuery();
			if(rs.next()) {
				eventDBID = rs.getInt("EVENT_ID") +1;
			}
			
			stmt.setInt(1, event.getScenarioID());
			stmt.setInt(2, eventDBID);
			stmt.setLong(4, event.getThrowTime().getTime());
			
			for(Resource r : event.getNewResources()) {
				stmt.setInt(3, r.getResourceID());
				
				stmt.execute();
				
				// neue Resource auch in die DB speichern:
				this.insertNewResource(r);
			}
		}
		
		return eventDBID;
	}
	
	/**
	 * Fügt ein NewProductsEvent in die DB ein:
	 * Neue products werden direkt in die DB eingetragen!
	 * @param event
	 * @return EVENT_ID
	 * @throws SQLException
	 */
	private int insertNewProductsEvent(NewProductsEvent event) throws SQLException {
		int eventDBID = 0;
		
		if(this.scenarioMap.get(event.getScenarioID()) == null) {
			this.scenarioMap.put(event.getScenarioID(), new Object());
		}
		
		synchronized(this.scenarioMap.get(event.getScenarioID())) {
			PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_PRODUCTS_EVENT.getStmt());
			PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_LAST_NEW_PRODUCTS_EVENT_ID.getStmt());
			
			// eventDBID setzen:
			stmt2.setInt(1, event.getScenarioID());
			ResultSet rs = stmt2.executeQuery();
			if(rs.next()) {
				eventDBID = rs.getInt("EVENT_ID") +1;
			}
			
			stmt.setInt(1, event.getScenarioID());
			stmt.setInt(2, eventDBID);
			stmt.setLong(4, event.getThrowTime().getTime());
			
			for(Product p : event.getNewProducts()) {
				stmt.setInt(3, p.getProductID());
				
				stmt.execute();
				
				// Neues Product in die DB eintragen:
				this.insertNewProduct(p);
			}
		}
		
		return eventDBID;
	}
	
	/**
	 * Fügt ein neues NewOrdersEvent ein:
	 * Fügt die neue ScheduleOrder auch in die DB ein!
	 * @param event
	 * @return EVENT_ID
	 * @throws SQLException
	 */
	private int insertNewOrdersEvent(NewOrdersEvent event) throws SQLException {
		int eventDBID = 0;
		
		if(this.scenarioMap.get(event.getScenarioID()) == null) {
			this.scenarioMap.put(event.getScenarioID(), new Object());
		}
		
		synchronized(this.scenarioMap.get(event.getScenarioID())) {
			PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_ORDERS_EVENT.getStmt());
			PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_LAST_NEW_ORDERS_EVENT_ID.getStmt());
			
			// eventDBID holen:
			stmt2.setInt(1, event.getScenarioID());
			ResultSet rs = stmt2.executeQuery();
			if(rs.next()) {
				eventDBID = rs.getInt("EVENT_ID") +1;
			}
			
			stmt.setInt(1, event.getScenarioID());
			stmt.setInt(2, eventDBID);
			stmt.setLong(4, event.getThrowTime().getTime());
			
			for(ScheduleOrder so : event.getNewOrdersList()) {
				stmt.setInt(3, so.getOrderID());
				
				stmt.execute();
				
				// ScheduleOrder auch in die DB eintragen:
				this.insertNewScheduleOrder(so);
			}
		}
		
		return eventDBID;
		
	}
	
	/**
	 * Fügt ein neues MaintenancePeriodsEvent in die DB ein:
	 * Ändert auch die DB!
	 * @param event
	 * @return EVENT_ID
	 * @throws SQLException 
	 */
	private int insertNewMaintenancePeriodsEvent(MaintenancePeriodsEvent event) throws SQLException {
		int eventDBID = 0;
		
		if(this.scenarioMap.get(event.getScenarioID()) == null) {
			this.scenarioMap.put(event.getScenarioID(), new Object());
		}
		
		synchronized(this.scenarioMap.get(event.getScenarioID())) {
			PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_MAINTENANCE_PERIODS_EVENT.getStmt());
			PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_LAST_MAINTENANCE_PERIODS_EVENT.getStmt());
			
			// eventDBID setzen:
			stmt2.setInt(1, event.getScenarioID());
			ResultSet rs = stmt2.executeQuery();
			if(rs.next()) {
				eventDBID = rs.getInt("EVENT_ID") +1;
			}
			
			stmt.setInt(1, event.getScenarioID());
			stmt.setInt(2, eventDBID);
			stmt.setLong(6, event.getThrowTime().getTime());
			
			for(ResourceRestriction rr : event.getMaintenancePeriods()) {
				stmt.setInt(3, rr.getResourceID());
				stmt.setLong(4, rr.getStartTime().getTime());
				stmt.setLong(5, rr.getDueTime().getTime());
				
				stmt.execute();
				
				this.insertNewResourceRestriction(rr);
			}	
		}
		
		return eventDBID;
	}
	
	/**
	 * Fügt ein MaschineRepairedEvent ein:
	 * Ändert auch die DB!
	 * @param event
	 * @return EVENT_ID
	 * @throws SQLException 
	 */
	private int insertNewMachineRepairedEvent(MachineRepairedEvent event) throws SQLException {
		int eventDBID = 0;
		
		if(this.scenarioMap.get(event.getScenarioID()) == null) {
			this.scenarioMap.put(event.getScenarioID(), new Object());
		}
		
		synchronized(this.scenarioMap.get(event.getScenarioID())) {
			
			PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_MACHINE_REPAIRED_EVENT.getStmt());
			PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_LAST_MACHINE_REPAIRED_EVENT.getStmt());
			PreparedStatement stmt3 = DBConnection.getPstmt(DBStatements.UPDATE_RESOURCE_BREAKDOWN.getStmt());
			
			// eventDBID setzen:
			stmt2.setInt(1, event.getScenarioID());
			ResultSet rs = stmt2.executeQuery();
			if(rs.next()) {
				eventDBID = rs.getInt("EVENT_ID") +1;
			}
			
			stmt.setInt(1, event.getScenarioID());
			stmt.setInt(2, eventDBID);
			stmt.setLong(4, event.getThrowTime().getTime());
			
			stmt3.setBoolean(1, false);
			stmt3.setInt(2, event.getScenarioID());
			
			
			for(Resource r : event.getResourceRepairs()) {
				stmt.setInt(3, r.getResourceID());
				
				stmt.execute();
				
				stmt3.setInt(3, r.getResourceID());
				
				stmt3.execute();
			}
			
		}
		
		return eventDBID;
	}
	
	
	/**
	 * Fügt ein MaschineBreakDownsEvent ein:
	 * Ändert auch die DB!
	 * @param event
	 * @return EVENT_ID
	 * @throws SQLException 
	 */
	private int insertNewMachineBreakDownsEvent(MachineBreakDownEvent event) throws SQLException {
		int eventDBID = 0;
		
		if(this.scenarioMap.get(event.getScenarioID()) == null) {
			this.scenarioMap.put(event.getScenarioID(), new Object());
		}
		
		synchronized(this.scenarioMap.get(event.getScenarioID())) {
			
			PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_MACHINE_BREAK_DOWN_EVENT.getStmt());
			PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_LAST_MACHINE_BREAK_DOWN_EVENT.getStmt());
			PreparedStatement stmt3 = DBConnection.getPstmt(DBStatements.UPDATE_RESOURCE_BREAKDOWN.getStmt());
			
			// eventDBID setzen:
			stmt2.setInt(1, event.getScenarioID());
			ResultSet rs = stmt2.executeQuery();
			if(rs.next()) {
				eventDBID = rs.getInt("EVENT_ID") +1;
			}
			
			stmt.setInt(1, event.getScenarioID());
			stmt.setInt(2, eventDBID);
			stmt.setLong(4, event.getThrowTime().getTime());
			
			stmt3.setBoolean(1, true);
			stmt3.setInt(2, event.getScenarioID());
			
			for(Resource r : event.getResourceBreakDowns()) {
				stmt.setInt(3, r.getResourceID());
				
				stmt.execute();
				
				stmt3.setInt(3, r.getResourceID());
				
				stmt3.execute();
			}
			
		}
		
		return eventDBID;
	}
	
	/**
	 * Fügt ein neues ChangeResourceAvailabilityEvent hinzu.
	 * Ändert auch die DB!
	 * @param event
	 * @return EVENT_ID
	 * @throws SQLException
	 */
	private int insertNewChangeResourceAvailabilityEvent(ChangeResourceAvailabilityEvent event) throws SQLException {
		int eventDBID = 0;
		
		if(this.scenarioMap.get(event.getScenarioID()) == null) {
			this.scenarioMap.put(event.getScenarioID(), new Object());
		}
		
		synchronized(this.scenarioMap.get(event.getScenarioID())) {
			
			PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_CHANGE_RESOURCE_AVAILABILITY_EVENT.getStmt());
			PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_LAST_CHANGE_RESOURCE_AVAILABILITY_EVENT.getStmt());
			PreparedStatement stmt3 = DBConnection.getPstmt(DBStatements.UPDATE_RESOURCE_AVAILABILITY.getStmt());
			
			// EventDBID holen:
			stmt2.setInt(1, event.getScenarioID());
			ResultSet rs = stmt2.executeQuery();
			if(rs.next()) {
				eventDBID = rs.getInt("EVENT_ID") +1;
			}
			
			// Veränderungen einfügen:
			stmt.setInt(1, event.getScenarioID());
			stmt.setInt(2, eventDBID);
			stmt.setLong(6, event.getThrowTime().getTime());
			
			stmt3.setInt(2, event.getScenarioID());
			
			// Neue/Alte Availability
			for(Integer resourceID : event.getChangedResourceAvailabilities().keySet()) {
				stmt.setInt(3, resourceID);
				stmt.setInt(4, event.getChangedResourceAvailabilities().get(resourceID));
				stmt.setInt(5, event.getOldResourceAvailabilityMap().get(resourceID));
				
				stmt.execute();
				
				stmt3.setInt(1, event.getChangedResourceAvailabilities().get(resourceID));
				stmt3.setInt(3, resourceID);
				
				stmt3.execute();
			}
		}
		
		return eventDBID;
	}
	
	/**
	 * Fügt ein neues ChangeOrdersPriorityEvent ein
	 * Ändert auch die DB!
	 * @param event
	 * @return EVENT_ID
	 * @throws SQLException
	 */
	private int insertNewChangeOrdersPriorityEvent(ChangeOrderPriorityEvent event) throws SQLException {
		int eventDBID = 0;
		
		if(this.scenarioMap.get(event.getScenarioID()) == null) {
			this.scenarioMap.put(event.getScenarioID(), new Object());
		}
		
		synchronized(this.scenarioMap.get(event.getScenarioID())) {
			PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_CHANGE_ORDERS_PRIORITY_EVENT.getStmt());
			PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_LAST_CHANGE_ORDERS_PRIORITY_EVENT.getStmt());
			PreparedStatement stmt3 = DBConnection.getPstmt(DBStatements.UPDATE_ORDERS_PRIORITY.getStmt());
			
			// EventDBID setzen:
			stmt2.setInt(1, event.getScenarioID());
			ResultSet rs = stmt2.executeQuery();
			if(rs.next()) {
				eventDBID = rs.getInt("EVENT_ID") +1;
			}
			
			stmt.setInt(1, event.getScenarioID());
			stmt.setInt(2, eventDBID);
			stmt.setLong(6, event.getThrowTime().getTime());
			
			stmt3.setInt(2, event.getScenarioID());
			
			for(ScheduleOrder so : event.getChangeOrders()) {
				stmt.setInt(3, so.getOrderID());
				stmt.setInt(4, so.getPriority());
				stmt.setInt(5, event.getOldPriorities().get(so.getOrderID()));
				
				stmt.execute();
				
				stmt3.setInt(1, so.getPriority());
				stmt3.setInt(3, so.getOrderID());
				
				stmt3.execute();
			}
		}
		
		return eventDBID;
	}
	
	/**
	 * Fügt ein neues ChangeOrdersDueTimeEvent hinzu.
	 * Ändert auch die DB!
	 * @param event
	 * @return EVENT_ID
	 * @throws SQLException
	 */
	private int insertNewChangeOrdersDueTimesEvent(ChangeOrderDueTimeEvent event) throws SQLException {
		int eventDBID = 0;
		
		if(this.scenarioMap.get(event.getScenarioID()) == null) {
			this.scenarioMap.put(event.getScenarioID(), new Object());
		}
		
		synchronized(this.scenarioMap.get(event.getScenarioID())) {
			PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_CHANGE_ORDERS_DUE_TIMES_EVENT.getStmt());
			PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_LAST_CHANGE_ORDERS_DUE_TIMES_EVENT.getStmt());
			PreparedStatement stmt3 = DBConnection.getPstmt(DBStatements.UPDATE_ORDERS_DUE_TIME.getStmt());
			
			// EventDBID setzen:
			stmt2.setInt(1, event.getScenarioID());
			ResultSet rs = stmt2.executeQuery();
			if(rs.next()) {
				eventDBID = rs.getInt("EVENT_ID") +1;
			}
			
			stmt.setInt(1, event.getScenarioID());
			stmt.setInt(2, eventDBID);
			stmt.setLong(6, event.getThrowTime().getTime());
			
			stmt3.setInt(2, event.getScenarioID());
			
			for(ScheduleOrder so : event.getChangedOrders()) {

				stmt.setInt(3, so.getOrderID());
				stmt.setLong(4, so.getEarlistDueTime().getTime());
				stmt.setLong(5, event.getOldDueTimes().get(so.getOrderID()).getTime());
				
				stmt.execute();
				
				stmt3.setLong(1, so.getEarlistDueTime().getTime());
				stmt3.setInt(3, so.getOrderID());
				
				stmt3.execute();
			}
		}

		return eventDBID;
	}
	
	/**
	 * Setzt ein ChangeOperation Resource Event.
	 * Ändert auch die DB!
	 * @param event
	 * @return EVENT_ID aus der Datenbank.
	 * @throws SQLException 
	 */
	private int insertNewChangeOperationResourcesEvent(ChangeOperationResourcesEvent event) throws SQLException {
		int eventDBID = -1;
		
		synchronized(changeOperationResourceEventLock) {
			PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_CHANGE_OPERATION_RESOURCES_EVENT.getStmt());
			PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_LAST_CHANGE_OPERATION_RESOURCES_EVENT.getStmt());
			PreparedStatement stmt3 = DBConnection.getPstmt(DBStatements.INSERT_CHANGE_OPERATION_RESOURCE_LIST_ITEM.getStmt());
			PreparedStatement stmt4 = DBConnection.getPstmt(DBStatements.INSERT_CHANGE_OPERATION_OLD_RESOURCE_LIST_ITEM.getStmt());
			PreparedStatement stmt5 = DBConnection.getPstmt(DBStatements.REMOVE_OPERATION_RESOURCES.getStmt());
			PreparedStatement stmt6 = DBConnection.getPstmt(DBStatements.INSERT_OPERATION_RESOURCE.getStmt());
			
			// Alte Resource Alternativen löschen:
			stmt5.setInt(1, event.getScenarioID());
			stmt5.setInt(2, event.getOperationID());
			
			stmt5.execute();
			
			// Neue Resource Alternativen:
			stmt6.setInt(1, event.getScenarioID());
			stmt6.setInt(2, event.getOperationID());
			
			/*
			 * Event einfügen:
			 */
			// Variablen binden
			stmt.setInt(1, event.getScenarioID());
			stmt.setLong(2, event.getThrowTime().getTime());
			stmt.setInt(3, event.getDuration());
			stmt.setInt(4, event.getOldDuration());
			stmt.setInt(5, event.getOperationID());

			// Statement ausführen
			stmt.execute();
			
			/*
			 * EVNET_ID holen:
			 */
			stmt2.setInt(1, event.getScenarioID());
			
			ResultSet rs = stmt2.executeQuery();
			while(rs.next()) {
				eventDBID = rs.getInt("EVENT_ID");
			}
			
			/*
			 * Operation Resource Items setzen:
			 */
			stmt3.setInt(1, eventDBID);
			for(Integer resourceID : event.getNewResourceList()) {
				stmt3.setInt(2, event.getOperationID());
				stmt3.setInt(3, resourceID);
				
				stmt3.execute();
				
				// Resource Alternative in der DB Ändern:
				stmt6.setInt(3, resourceID);
				
				stmt6.execute();
			}
			
			/*
			 * Operation Old Resource Items setzen:
			 */
			stmt4.setInt(1, eventDBID);
			for(Integer resourceID : event.getOldResourceList()) {
				stmt4.setInt(2, event.getOperationID());
				stmt4.setInt(3, resourceID);
				
				stmt4.execute();
			}
		}

		return eventDBID;
	}
	
	/**
	 * Fügt ein neues ScheduleEvent in die DB ein.
	 * @param scheduleEvent
	 * @return EVENT_ID aus der DB.
	 * @throws SQLException 
	 */
	private int insertScheduleEvent(ScheduleEvent scheduleEvent) throws SQLException {
		
		if(scheduleEvent instanceof ChangeOperationResourcesEvent) {
			return this.insertNewChangeOperationResourcesEvent((ChangeOperationResourcesEvent)scheduleEvent);
		}
		if(scheduleEvent instanceof ChangeOrderDueTimeEvent) {
			return this.insertNewChangeOrdersDueTimesEvent((ChangeOrderDueTimeEvent)scheduleEvent);
		}
		if(scheduleEvent instanceof ChangeOrderPriorityEvent) {
			return this.insertNewChangeOrdersPriorityEvent((ChangeOrderPriorityEvent)scheduleEvent);
		}
		if(scheduleEvent instanceof ChangeResourceAvailabilityEvent) {
			return this.insertNewChangeResourceAvailabilityEvent((ChangeResourceAvailabilityEvent)scheduleEvent);
		}
		if(scheduleEvent instanceof MachineBreakDownEvent) {
			return this.insertNewMachineBreakDownsEvent((MachineBreakDownEvent)scheduleEvent);
		}
		if(scheduleEvent instanceof MachineRepairedEvent) {
			return this.insertNewMachineRepairedEvent((MachineRepairedEvent)scheduleEvent);
		}
		if(scheduleEvent instanceof MaintenancePeriodsEvent) {
			return this.insertNewMaintenancePeriodsEvent((MaintenancePeriodsEvent)scheduleEvent);
		}
		if(scheduleEvent instanceof NewOrdersEvent) {
			return this.insertNewOrdersEvent((NewOrdersEvent)scheduleEvent);
		}
		if(scheduleEvent instanceof NewProductsEvent) {
			return this.insertNewProductsEvent((NewProductsEvent)scheduleEvent);
		}
		if(scheduleEvent instanceof NewResourceEvent) {
			return this.insertNewResourceEvent((NewResourceEvent)scheduleEvent);
		}
		if(scheduleEvent instanceof NewVariantsEvent) {
			return this.insertNewVariantsEvent((NewVariantsEvent)scheduleEvent);
		}
		if(scheduleEvent instanceof RemoveOrdersEvent) {
			return this.insertNewRemoveOrdersEvent((RemoveOrdersEvent)scheduleEvent);
		}
		if(scheduleEvent instanceof RemoveProductsEvent) {
			return this.insertNewRemoveProductsEvent((RemoveProductsEvent)scheduleEvent);
		}
		if(scheduleEvent instanceof RemoveVariantsEvent) {
			return this.insertNewRemoveVariantsEvent((RemoveVariantsEvent)scheduleEvent);
		}
		if(scheduleEvent instanceof RemoveOrdersProductsEvent) {
			return this.insertNewRemoveOrdersProductsEvent((RemoveOrdersProductsEvent)scheduleEvent);
		}
		if(scheduleEvent instanceof ScheduleChangeByUserEvent) {
			return this.insertNewScheduleChangeByUserEvent((ScheduleChangeByUserEvent)scheduleEvent);
		}
		
		return -1;
	}
	
	/**
	 * Setzt alle Zähler für das Scenario.
	 * @param scenario
	 * @throws SQLException 
	 */
	private void setScenarioCounters(Scenario scenario) throws SQLException {

		PreparedStatement stmtOP = DBConnection.getPstmt(DBStatements.UPDATE_NEW_OPERATION_ID_COUNT.getStmt());
		PreparedStatement stmtOR = DBConnection.getPstmt(DBStatements.UPDATE_NEW_ORDER_ID_COUNT.getStmt());
		PreparedStatement stmtPR = DBConnection.getPstmt(DBStatements.UPDATE_NEW_PRODUCT_ID.getStmt());
		PreparedStatement stmtRE = DBConnection.getPstmt(DBStatements.UPDATE_NEW_RESOURCE_ID_COUNT.getStmt());
		PreparedStatement stmtVA = DBConnection.getPstmt(DBStatements.UPDATE_NEW_VARIANT_ID_COUNT.getStmt());
		
		// Variablen binden
		stmtOP.setInt(1, scenario.getNewOperationIDCount());
		stmtOP.setInt(2, scenario.getScenarioID());
		stmtOR.setInt(1, scenario.getNewOrderIDCount());
		stmtOR.setInt(2, scenario.getScenarioID());
		stmtPR.setInt(1, scenario.getNewProductIDCount());
		stmtPR.setInt(2, scenario.getScenarioID());
		stmtRE.setInt(1, scenario.getNewResourceIDCount());
		stmtRE.setInt(2, scenario.getScenarioID());
		stmtVA.setInt(1, scenario.getNewVariantIDCount());
		stmtVA.setInt(2, scenario.getScenarioID());
		
		// Statements ausführen
		stmtOP.execute();
		stmtOR.execute();
		stmtPR.execute();
		stmtRE.execute();
		stmtVA.execute();
	}
	
	public synchronized void addSchedule(Scenario scenario, Schedule schedule) throws SQLException {
		int eventDBID = -1;
		int eventDBType = -1;
	
		PreparedStatement stmtNewSchedule = DBConnection.getPstmt(DBStatements.INSERT_NEW_SCHEDULE.getStmt());
		PreparedStatement stmtSelectLastSchedule = DBConnection.getPstmt(DBStatements.SELECT_LAST_SCHEDULE.getStmt());
		PreparedStatement stmtInsertCurrentScheduleListItem = DBConnection.getPstmt(DBStatements.INSERT_CURRENT_SCHEDULE_LIST_ITEM.getStmt());
		PreparedStatement stmtInsertNewScheduleKeyFigure = DBConnection.getPstmt(DBStatements.INSERT_NEW_SCHEDULE_KEY_FIGURE.getStmt());
		PreparedStatement stmtSelectSchedule = DBConnection.getPstmt(DBStatements.SELECT_SCHEDULE.getStmt());
		PreparedStatement stmtSelectScenario = DBConnection.getPstmt(DBStatements.SELECT_SCENARIO.getStmt());
		
		// eventID und eventDBType setzen:
		if(schedule.getAlgorithmInformations().getInitialSchedule(scenario) != null) {
			eventDBType = scenario.getChosenSchedule().getScheduleEvent().getType();
			
			// Scenario laden:
			stmtSelectScenario.setInt(1, scenario.getScenarioID());
			ResultSet rsSelectScenario = stmtSelectScenario.executeQuery();
			if(rsSelectScenario.next()) {
				stmtSelectSchedule.setInt(1, scenario.getScenarioID());
				stmtSelectSchedule.setInt(2, rsSelectScenario.getInt("CHOSEN_SCHEDULE_ID"));
				ResultSet rsChosenSchedule = stmtSelectSchedule.executeQuery();
				if(rsChosenSchedule.next()) {
					eventDBID = rsChosenSchedule.getInt("EVENT_ID");
				}
			}
		}
		
		// neuen schedule einfügen:
		stmtNewSchedule.setInt(1, scenario.getScenarioID());
		stmtNewSchedule.setInt(2, eventDBID);
		stmtNewSchedule.setInt(3, eventDBType);
		stmtNewSchedule.execute();
		
		// Als CurrentSchedule setzen:
		
		// Vorher die ScheduleID holen (DB auto increment):
		stmtSelectLastSchedule.setInt(1, schedule.getScenarioID());
		ResultSet rs = stmtSelectLastSchedule.executeQuery();
		while(rs.next()) {
			schedule.setScheduleID(rs.getInt("SCHEDULE_ID"));
		}
		stmtInsertCurrentScheduleListItem.setInt(1, schedule.getScenarioID());
		stmtInsertCurrentScheduleListItem.setInt(2, schedule.getScheduleID());
		
		stmtInsertCurrentScheduleListItem.execute();
		
		// PlannedVariants setzen:
		
		// Vorher die ScheduleID richtig setzen:
		for(PlannedVariant pv : schedule.getPlannedVariants()) {
			pv.setScheduleID(schedule.getScheduleID());
		}
		
		this.insertNewPlannedVariants(schedule.getPlannedVariants());
		
		// AlgorithmInformations hinzufügen:
		this.insertNewAlgorithmInformations(schedule.getScenarioID(), schedule.getScheduleID(), schedule.getAlgorithmInformations());
		
		// KeyFigures speichern:
		stmtInsertNewScheduleKeyFigure.setInt(1, schedule.getScenarioID());
		stmtInsertNewScheduleKeyFigure.setInt(2, schedule.getScheduleID());
		for(Integer keyFigureType : schedule.getKeyFigureValueMap().keySet()) {
			stmtInsertNewScheduleKeyFigure.setInt(3, keyFigureType);
			stmtInsertNewScheduleKeyFigure.setInt(4, schedule.getKeyFigureValueMap().get(keyFigureType));
			
			stmtInsertNewScheduleKeyFigure.execute();
		}
		
		// Counts updaten:
		setScenarioCounters(scenario);
	}
	
	/**
	 * Fügt den ChosenSchedule hinzu, fügt die CurrentSchedules hinzu und löscht die
	 * vorherigen CurrentSchedules.
	 * @param chosenSchedule
	 * @param currentScheduleList
	 * @throws SQLException
	 */
	public synchronized void insertCurrentSchedules(Scenario scenario, Schedule chosenSchedule, List<Schedule> currentScheduleList) throws SQLException {
		int eventDBID = -1;
		
		// Aktuelle Schedules löschen:
		if(!currentScheduleList.isEmpty()) {
			this.removeCurrentSchedules(currentScheduleList.get(0).getScenarioID(), chosenSchedule.getAlgorithmInformations().getInitialScheduleID());
		}

		// ScheduleEvent speichern:
		if(chosenSchedule.getScheduleEvent() != null) {
			eventDBID = this.insertScheduleEvent(chosenSchedule.getScheduleEvent());
		}
		
		// Neue Schedules setzen:
		PreparedStatement stmtNewSchedule = DBConnection.getPstmt(DBStatements.INSERT_NEW_SCHEDULE.getStmt());
		PreparedStatement stmtSelectLastSchedule = DBConnection.getPstmt(DBStatements.SELECT_LAST_SCHEDULE.getStmt());
		PreparedStatement stmtInsertCurrentScheduleListItem = DBConnection.getPstmt(DBStatements.INSERT_CURRENT_SCHEDULE_LIST_ITEM.getStmt());
		PreparedStatement stmtUpdateScenarioChosenSchedule = DBConnection.getPstmt(DBStatements.UPDATE_SCENARIO_CHOSEN_SCHEDULE.getStmt());
		PreparedStatement stmtInsertNewScheduleKeyFigure = DBConnection.getPstmt(DBStatements.INSERT_NEW_SCHEDULE_KEY_FIGURE.getStmt());
		
		for(Schedule s : currentScheduleList) {
			// Variablen binden
			stmtNewSchedule.setInt(1, s.getScenarioID());
			
			// Kein Event -> Erster Schedule überhaupt
			if(s.getScheduleEvent() == null) {
				stmtNewSchedule.setInt(2, -1); 
				stmtNewSchedule.setInt(3, -1);
			} else {
				stmtNewSchedule.setInt(2, eventDBID);
				stmtNewSchedule.setInt(3, s.getScheduleEvent().getType());
			}
			
			// Statement ausführen
			stmtNewSchedule.execute();
			
			// Als CurrentSchedule setzen:
			
			// Vorher die ScheduleID holen (DB auto increment):
			stmtSelectLastSchedule.setInt(1, s.getScenarioID());
			ResultSet rs = stmtSelectLastSchedule.executeQuery();
			while(rs.next()) {
				s.setScheduleID(rs.getInt("SCHEDULE_ID"));
			}
			stmtInsertCurrentScheduleListItem.setInt(1, s.getScenarioID());
			stmtInsertCurrentScheduleListItem.setInt(2, s.getScheduleID());
			
			stmtInsertCurrentScheduleListItem.execute();
			
			// PlannedVariants setzen:
			
			// Vorher die ScheduleID richtig setzen:
			for(PlannedVariant pv : s.getPlannedVariants()) {
				pv.setScheduleID(s.getScheduleID());
			}
			
			this.insertNewPlannedVariants(s.getPlannedVariants());
			
			// AlgorithmInformations hinzufügen:
			this.insertNewAlgorithmInformations(s.getScenarioID(), s.getScheduleID(), s.getAlgorithmInformations());
			
			// ChosenSchedule beachten:
			if(s.getAlgorithmInformations().getAlgorithmName().equals(chosenSchedule.getAlgorithmInformations().getAlgorithmName())) {
				stmtUpdateScenarioChosenSchedule.setInt(1, s.getScheduleID());
				stmtUpdateScenarioChosenSchedule.setInt(2, s.getScenarioID());
				
				stmtUpdateScenarioChosenSchedule.execute();
			}
			
			// KeyFigures speichern:
			stmtInsertNewScheduleKeyFigure.setInt(1, s.getScenarioID());
			stmtInsertNewScheduleKeyFigure.setInt(2, s.getScheduleID());
			for(Integer keyFigureType : s.getKeyFigureValueMap().keySet()) {
				stmtInsertNewScheduleKeyFigure.setInt(3, keyFigureType);
				stmtInsertNewScheduleKeyFigure.setInt(4, s.getKeyFigureValueMap().get(keyFigureType));
				
				stmtInsertNewScheduleKeyFigure.execute();
			}
			
			// Counts updaten:
			setScenarioCounters(scenario);
		}
	}
	
	/**
	 * Fügt neue AlgorithmInformations hinzu.
	 * @param scenarioID
	 * @param algorithmInformations
	 * @throws SQLException 
	 */
	private void insertNewAlgorithmInformations(int scenarioID, int scheduleID,AlgorithmInformation algorithmInformations) throws SQLException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_ALGORITHM_INFORMATIONS.getStmt());

		// Variablen binden
		stmt.setInt(1, scenarioID);
		stmt.setInt(2, scheduleID);
		stmt.setInt(3, algorithmInformations.getInitialScheduleID());
		stmt.setLong(4, algorithmInformations.getStartTime().getTime());
		stmt.setLong(5, algorithmInformations.getDueTime().getTime());
		stmt.setString(6, algorithmInformations.getAlgorithmName());

		// Statement ausführen
		stmt.execute();
	}
	
	/**
	 * Löscht die Current Schedules, wenn sie durch ein neues Event unbrauchbar werden.
	 * Nur der ChosenSchedule bleibt erhalten!
	 * @param scenarioID
	 * @throws SQLException
	 */
	private void removeCurrentSchedules(int scenarioID, int chosenScheduleID) throws SQLException {
		PreparedStatement stmtSelectCurrentScheduleList = DBConnection.getPstmt(DBStatements.SELECT_CURRENT_SCHEDULES.getStmt());
		PreparedStatement stmtRemoveCurrentScheduleList = DBConnection.getPstmt(DBStatements.REMOVE_CURRENT_SCHEDULE_LIST.getStmt());
		PreparedStatement stmtRemoveSchedule = DBConnection.getPstmt(DBStatements.REMOVE_SCHEDULE.getStmt());
		PreparedStatement stmtRemovePlannedVariants = DBConnection.getPstmt(DBStatements.REMOVE_PLANNED_VARIANTS.getStmt());
		PreparedStatement stmtSelectPlannedVariants = DBConnection.getPstmt(DBStatements.SELECT_PLANNED_VARIANTS.getStmt());
		PreparedStatement stmtRemoveAllocations = DBConnection.getPstmt(DBStatements.REMOVE_PLANNED_VARIANT_ALLOCATIONS.getStmt());
		PreparedStatement stmtSelectAllocations = DBConnection.getPstmt(DBStatements.SELECT_ALLOCATION.getStmt());
		PreparedStatement stmtRemoveAllocationPredecessors = DBConnection.getPstmt(DBStatements.REMOVE_ALLOCATION_PREDECESSORS.getStmt());
		PreparedStatement stmtRemoveAlgorithmInformations = DBConnection.getPstmt(DBStatements.REMOVE_ALGORITHM_INFORMATIONS_FOR_SCHEDULE.getStmt());
		PreparedStatement stmtRemoveKeyFigures = DBConnection.getPstmt(DBStatements.REMOVE_KEY_FIGURES_FOR_SCHEDULE.getStmt());

		/*
		 * Current Schedule List laden durchlaufen:
		 * Alle außer den ChosenSchedule -> Der bleibt in der DB.
		 */
		
		
		stmtSelectCurrentScheduleList.setInt(1, scenarioID);
		ResultSet rsSelectCurrentSchedules = stmtSelectCurrentScheduleList.executeQuery();
		while(rsSelectCurrentSchedules.next()) {
			int tmpScheduleID = rsSelectCurrentSchedules.getInt("SCHEDULE_ID");
			
			if(tmpScheduleID != chosenScheduleID) {
				
				/*
				 * PlannedVariants zum Schedule laden:
				 */
				stmtSelectPlannedVariants.setInt(1, scenarioID);
				stmtSelectPlannedVariants.setInt(2, tmpScheduleID);
				ResultSet rsPlannedVariants = stmtSelectPlannedVariants.executeQuery();
				while(rsPlannedVariants.next()) {
					
					/*
					 * Allocations zur PlannedVariant laden:
					 */
					int tmpPlannedVariantID = rsPlannedVariants.getInt("PLANNED_VARIANT_ID");
					stmtSelectAllocations.setInt(1, scenarioID);
					stmtSelectAllocations.setInt(2, tmpPlannedVariantID);
					ResultSet rsSelectAllocations = stmtSelectAllocations.executeQuery();
					while(rsSelectAllocations.next()) {
						
						/*
						 * Allocation Predecessors löschen:
						 */
						stmtRemoveAllocationPredecessors.setInt(1, scenarioID);
						stmtRemoveAllocationPredecessors.setInt(2, rsSelectAllocations.getInt("ALLOCATION_ID"));
						stmtRemoveAllocationPredecessors.setInt(3, rsSelectAllocations.getInt("SCHEDULE_ID"));
						stmtRemoveAllocationPredecessors.execute();
					}
					
					/*
					 * Allocations zu der PlannedVariant löschen:
					 */
					stmtRemoveAllocations.setInt(1, scenarioID);
					stmtRemoveAllocations.setInt(2, rsPlannedVariants.getInt("PLANNED_VARIANT_ID"));
					stmtRemoveAllocations.execute();
					
				}
				
				/*
				 * PlannedVariants zum Schedule löschen:
				 */
				stmtRemovePlannedVariants.setInt(1, scenarioID);
				stmtRemovePlannedVariants.setInt(2, rsSelectCurrentSchedules.getInt("SCHEDULE_ID"));
				stmtRemovePlannedVariants.execute();
				
				/*
				 * AlgorithmInformations zum Schedule löschen:
				 */
				stmtRemoveAlgorithmInformations.setInt(1, scenarioID);
				stmtRemoveAlgorithmInformations.setInt(2, rsSelectCurrentSchedules.getInt("SCHEDULE_ID"));
				stmtRemoveAlgorithmInformations.execute();
				
				/*
				 * KeyFigures zum Schedule löschen:
				 */
				stmtRemoveKeyFigures.setInt(1, scenarioID);
				stmtRemoveKeyFigures.setInt(2, rsSelectCurrentSchedules.getInt("SCHEDULE_ID"));
				stmtRemoveKeyFigures.execute();
				
				/*
				 * Schedules löschen:
				 */
				stmtRemoveSchedule.setInt(1, scenarioID);
				stmtRemoveSchedule.setInt(2, rsSelectCurrentSchedules.getInt("SCHEDULE_ID"));
				stmtRemoveSchedule.execute();
			}
		}
		
		/*
		 * CURRENT_SCHEDULES komplett leeren:
		 */
		// Variablen binden
		stmtRemoveCurrentScheduleList.setInt(1, scenarioID);

		// Statement ausführen
		stmtRemoveCurrentScheduleList.execute();
	}
	
	/**
	 * Fügt eine neue keyFigureList in die DB hinzu.
	 * Die alte wird dazu gelöscht.
	 * @param keyFigureList
	 * @throws SQLException 
	 */
	public void insertNewKeyFigureList(int scenarioID, List<KeyFigure> keyFigureList) throws SQLException {
		// Erst die alten KeyFigures löschen:
		this.removeKeyFigureList(scenarioID);
		
		// Neue KeyFigureList eintragen:
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_KEY_FIGURE_LIST_ITEM.getStmt());
		int priority = 1;
		
		for(KeyFigure kf : keyFigureList) {
			// Variablen binden
			stmt.setInt(1, scenarioID);
			stmt.setInt(2, kf.getId());
			stmt.setInt(3, priority);

			// Statement ausführen
			stmt.execute();
			
			// Priority erhöhen
			priority++;
		}
	}
	
	private void removeKeyFigureList(int scenarioID) throws SQLException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.REMOVE_KEY_FIGURE_LIST.getStmt());

		// Variablen binden
		stmt.setInt(1, scenarioID);

		// Statement ausführen
		stmt.execute();
	}
	
	private void insertNewScheduleOrder(ScheduleOrder scheduleOrder) throws SQLException {
		List<ScheduleOrder> tmpScheduleOrderList = new ArrayList<ScheduleOrder>();
		tmpScheduleOrderList.add(scheduleOrder);
		
		insertNewScheduleOrders(tmpScheduleOrderList);
	}
	
	private void insertNewScheduleOrders(List<ScheduleOrder> scheduleOrderList) throws SQLException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_SCHEDULE_ORDER.getStmt());

		for(ScheduleOrder so : scheduleOrderList) {
			// Variablen binden
			stmt.setInt(1, so.getScenarioID());
			stmt.setInt(2, so.getOrderID());
			stmt.setInt(3, so.getPriority());
			stmt.setLong(4, so.getEarlistDueTime().getTime());
			stmt.setString(5, so.getName());

			// Statement ausführen
			stmt.execute();
			
			// Order Product List füllen
			Map<Integer, Integer> tmpProductOrderMap = new HashMap<Integer, Integer>();
			for(Integer productID : so.getProducts()) {
				if(tmpProductOrderMap.get(productID) == null) {
					tmpProductOrderMap.put(productID, 1);
				} else {
					tmpProductOrderMap.put(productID, tmpProductOrderMap.get(productID) +1);
				}
			}
			this.insertNewOrderProductList(so.getScenarioID(), so.getOrderID(), tmpProductOrderMap);
		}
	}
	
	/**
	 * Fügt eine neue Order Product List hinzu.
	 * Map<Integer = Product ID, Integer = Anzahl>
	 * @param productOrderList
	 * @throws SQLException 
	 */
	private void insertNewOrderProductList(int scenarioID, int scheduleOrderID, Map<Integer, Integer> productOrderMap) throws SQLException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_ORDER_PRODUCT_LIST_ITEM.getStmt());

		for(Integer productID : productOrderMap.keySet()) {
			// Variablen binden
			stmt.setInt(1, scenarioID);
			stmt.setInt(2, scheduleOrderID);
			stmt.setInt(3, productID);
			stmt.setInt(4, productOrderMap.get(productID));
			
			// Statement ausführen
			stmt.execute();
		}
	}
	
	private void insertNewResourceRestriction(ResourceRestriction resourceRestriction) throws SQLException {
		List<ResourceRestriction> tmpResourceRestrictionList = new ArrayList<ResourceRestriction>();
		tmpResourceRestrictionList.add(resourceRestriction);
		
		insertNewResourceRestrictions(tmpResourceRestrictionList);
	}
	
	private void insertNewResourceRestrictions(List<ResourceRestriction> resourceRestrictionList) throws SQLException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_RESOURCE_RESTRICTION.getStmt());

		for(ResourceRestriction rr : resourceRestrictionList) {
			// Variablen binden
			stmt.setInt(1, rr.getScenarioID());
			stmt.setInt(2, rr.getResourceID());
			stmt.setLong(3, rr.getStartTime().getTime());
			stmt.setLong(4, rr.getDueTime().getTime());

			// Statement ausführen
			stmt.execute();
		}
	}
	
	/**
	 * Fügt eine neue Resource hinzu.
	 * ResourceRestrictions werden mit hinzugefügt!
	 * @param resource
	 * @throws SQLException
	 */
	private void insertNewResource(Resource resource) throws SQLException {
		List<Resource> tmpResourceList = new ArrayList<Resource>();
		tmpResourceList.add(resource);
		
		insertNewResources(tmpResourceList);
	}
	
	/**
	 * Fügt neue Resources hinzu.
	 * ResourceRestrictions werden mit hinzugefügt!
	 * @param resourceList
	 * @throws SQLException
	 */
	private void insertNewResources(List<Resource> resourceList) throws SQLException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_RESOURCE.getStmt());

		for(Resource r : resourceList) {
			// Variablen binden
			stmt.setInt(1, r.getScenarioID());
			stmt.setInt(2, r.getResourceID());
			stmt.setString(3, r.getName());
			stmt.setBoolean(4, r.isBreakDown());
			stmt.setInt(5, r.getAvailability());
			
			// Statement ausführen
			stmt.execute();
			
			// Resource Restrictions hinzufügen:
			insertNewResourceRestrictions(r.getResourceRestrictions());
		}
	}
	
	private void insertNewVariant(Variant variant) throws SQLException {
		List<Variant> tmpVariantList = new ArrayList<Variant>();
		tmpVariantList.add(variant);
		
		insertNewVariants(tmpVariantList);
	}
	
	private void insertNewVariants(List<Variant> variantList) throws SQLException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_VARIANT.getStmt());

		for(Variant v : variantList) {
			// Variablen binden
			stmt.setInt(1, v.getScenarioID());
			stmt.setInt(2, v.getVariantID());
			stmt.setInt(3, v.getProduct().getProductID());

			// Statement ausführen
			stmt.execute();
			
			// Operationen hinzufügen:
			this.insertNewOperations(v.getOperations());
		}
	}
	
	/**
	 * Fügt ein neues Product hinzu.
	 * Varianten werden mit hinzugefügt!
	 * @param product
	 * @throws SQLException
	 */
	private void insertNewProduct(Product product) throws SQLException {
		List<Product> tmpProductList = new ArrayList<Product>();
		tmpProductList.add(product);
		
		insertNewProducts(tmpProductList);
	}
	
	/**
	 * Fügt neue Products hinzu.
	 * Varianten werden mit hinzugefügt!
	 * @param product
	 * @throws SQLException
	 */
	private void insertNewProducts(List<Product> productList) throws SQLException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_PRODUCT.getStmt());

		for(Product p : productList) {
			// Variablen binden
			stmt.setInt(1, p.getScenarioID());
			stmt.setInt(2, p.getProductID());
			stmt.setString(3, p.getName());

			// Statement ausführen
			stmt.execute();
			
			// Variants hinzufügen:
			insertNewVariants(p.getVariants());
		}
	}
	
	@SuppressWarnings("unused")
	private void insertNewPlannedVariant(PlannedVariant plannedVariant) throws SQLException {
		List<PlannedVariant> tmpPlannedVariantList = new ArrayList<PlannedVariant>();
		tmpPlannedVariantList.add(plannedVariant);
		
		insertNewPlannedVariants(tmpPlannedVariantList);
	}
	
	private synchronized void insertNewPlannedVariants(List<PlannedVariant> plannedVariantList) throws SQLException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_PLANNED_VARIANT.getStmt());
		PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.SELECT_LAST_PLANNED_VARIANT.getStmt());
		
		for(PlannedVariant pv : plannedVariantList) {
			// Variablen binden
			stmt.setInt(1, pv.getScenarioID());
			stmt.setInt(2, pv.getScheduleID());
			stmt.setInt(3, pv.getPlannedVariant().getVariantID());

			// Statement ausführen
			stmt.execute();
			
			// Allocations auch hinzufügen:
			stmt2.setInt(1, pv.getScenarioID());
			ResultSet rs = stmt2.executeQuery();
			while(rs.next()) {
				pv.setPlannedVariantID(rs.getInt("PLANNED_VARIANT_ID"));
			}
			
			// Allocations hinzufügen:
			this.insertNewAllocations(pv.getPlannedVariantID(), pv.getAllocationList(), pv.getScheduleID());
		}
	}
	
	@SuppressWarnings("unused")
	private void insertNewOperation(Operation operation) throws SQLException {
		List<Operation> tmpOperationList = new ArrayList<Operation>();
		tmpOperationList.add(operation);
		
		this.insertNewOperations(tmpOperationList);
	}
	
	private void insertNewOperations(List<Operation> operationList) throws SQLException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_OPERATION.getStmt());
		PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.INSERT_OPERATION_PREDECESSOR.getStmt());
		PreparedStatement stmt3 = DBConnection.getPstmt(DBStatements.INSERT_OPERATION_RESOURCE.getStmt());
		
		for(Operation op : operationList) {
			// Variablen binden
			stmt.setInt(1, op.getOperationID());
			stmt.setInt(2, op.getScenarioID());
			stmt.setInt(3, op.getVariantID());
			stmt.setString(4, op.getName());
			stmt.setInt(5, op.getDuration());

			// Statement ausführen
			stmt.execute();
			
			// Operation Predecessors:
			for(Operation opPre : op.getPredecessors()) {
				stmt2.setInt(1, op.getScenarioID());
				stmt2.setInt(2, op.getOperationID());
				stmt2.setInt(3, opPre.getOperationID());
				
				stmt2.execute();
			}
			
			// Operation Resources:
			for(Integer r : op.getResourceAlternatives()) {
				stmt3.setInt(1, op.getScenarioID());
				stmt3.setInt(2, op.getOperationID());
				stmt3.setInt(3, r);
				
				stmt3.execute();
			}
		}
	}
	
	/**
	 * Fügt eine neue Allocation in die DB hinzu.
	 * @param allocation
	 * @throws SQLException 
	 */
	@SuppressWarnings("unused")
	private void insertNewAllocation(int plannedVariantID, Allocation allocation, int scheduleID) throws SQLException {
		List<Allocation> tmpAllocationList = new ArrayList<Allocation>();
		tmpAllocationList.add(allocation);
		
		insertNewAllocations(plannedVariantID, tmpAllocationList, scheduleID);
	}
	
	/**
	 * Fügt eine Liste mit Allocations in die DB hinzu.
	 * @param allocationList
	 * @throws SQLException 
	 */
	private void insertNewAllocations(int plannedVariantID, List<Allocation> allocationList, int scheduleID) throws SQLException {
		PreparedStatement stmt = DBConnection.getPstmt(DBStatements.INSERT_NEW_ALLOCATION.getStmt());
		PreparedStatement stmt2 = DBConnection.getPstmt(DBStatements.INSERT_NEW_ALLOCATION_PREDECESSOR.getStmt());

		for(Allocation a : allocationList) {
			// Variablen binden
			stmt.setInt(1, a.getScenarioID());
			stmt.setInt(2, a.getUID());
			stmt.setInt(3, a.getOperation().getOperationID());
			stmt.setLong(4, a.getStart().getTime());
			stmt.setInt(5, plannedVariantID);
			stmt.setInt(6, a.getResourceID());
			stmt.setInt(7, a.getOrderID());
			stmt.setInt(8, scheduleID);

			// Statement ausführen
			stmt.execute();
			
			// Allocation Predecessors:
			for(Allocation aPre : a.getAllocationPredecessors()) {
				stmt2.setInt(1, a.getUID());
				stmt2.setInt(2, a.getScenarioID());
				stmt2.setInt(3, aPre.getUID());
				stmt2.setInt(4, scheduleID);
				
				stmt2.execute();
			}
		}
	}
	
	public static DBMethods getInstance() {
		if(instance == null) {
			instance = new DBMethods();
		} 
		
		return instance;
	}
}
