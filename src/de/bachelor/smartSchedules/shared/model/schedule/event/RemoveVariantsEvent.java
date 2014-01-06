package de.bachelor.smartSchedules.shared.model.schedule.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.Variant;

/**
 * Wenn Varianten gelöscht werden.
 * @author timo
 *
 */
public class RemoveVariantsEvent implements ScheduleEvent {
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -8475341265311616660L;
	private List<Variant> variants;
	private Date throwTime;
	private int scenarioID;
	private List<Schedule> currentSchedules;
	private Schedule chosenSchedule;
	
	/**
	 * Default
	 */
	public RemoveVariantsEvent() {
		this.variants = new ArrayList<Variant>();
		chosenSchedule = null;
		currentSchedules = null;
	}
	
	public RemoveVariantsEvent(int scenarioID, List<Variant> variants, Date throwTime) {
		chosenSchedule = null;
		currentSchedules = null;
		this.scenarioID = scenarioID;
		this.variants = variants;
		this.throwTime = throwTime;
	}
	
	public RemoveVariantsEvent(int scenarioID, Variant variant, Date throwTime) {
		this();
		this.scenarioID = scenarioID;
		this.variants.add(variant);
		this.throwTime = throwTime;
	}
	
	public List<Variant> getRemovedVariants() {
		return this.variants;
	}

	@Override
	public Date getThrowTime() {
		return this.throwTime;
	}

	@Override
	public int getScenarioID() {
		return this.scenarioID;
	}

	@Override
	public void changeScenario(Scenario scenario) {
		for(Variant v : variants) {
			scenario.getProduct(v.getProduct().getProductID()).removeVariant(v);
		}
		
		// Falls die gewählten Schedules schon gesetzt sind, dann auch updaten:
		if((this.getChosenSchedule() != null) && (this.getCurrentSchedules() != null)) {
			scenario.setChosenSchedule(this.getChosenSchedule());
			scenario.setCurrentSchedules(this.getCurrentSchedules());
		}
	}
	
	@Override
	public String getName() {
		return "Remove Variant";
	}
	
	@Override
	public String getDescription() {
		return "Description of " + this.getName();
	}
	
	@Override
	public int getType() {
		return ScheduleEvent.TYPE_REMOVE_VARIANTS_EVENT;
	}
	
	@Override
	public Schedule getChosenSchedule() {
		return this.chosenSchedule;
	}

	@Override
	public void setChosenSchedule(Schedule chosenSchedule) {
		this.chosenSchedule = chosenSchedule;
	}

	@Override
	public List<Schedule> getCurrentSchedules() {
		return this.currentSchedules;
	}

	@Override
	public void setCurrentSchedules(List<Schedule> currentScheduleList) {
		this.currentSchedules = currentScheduleList;
	}

	@Override
	public void changeScenarioBackwards(Scenario scenario) {
		for(Variant v : this.variants) {
			scenario.getProduct(v.getProduct().getProductID()).addVariant(v);
		}
	}
}
