package de.bachelor.smartSchedules.shared.model.util.event;

import java.util.ArrayList;
import java.util.List;

import de.bachelor.smartSchedules.client.presenter.ScenarioPresenter;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;

/**
 * Bei Veränderungen der KeyFigureList.
 * @author timo
 *
 */
public class ChangeKeyFiguresEvent implements UtilEvent{

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -4226412247156449098L;
	private List<KeyFigure> keyFigureList;
	
	/**
	 * Default
	 */
	public ChangeKeyFiguresEvent() {
		this.keyFigureList = new ArrayList<KeyFigure>();
	}
	
	/**
	 * Konstruktor
	 * @param newKeyFigureList
	 */
	public ChangeKeyFiguresEvent(List<KeyFigure> newKeyFigureList) {
		this.keyFigureList = newKeyFigureList;
	}
	
	@Override
	public void process(ScenarioPresenter scenarioPresenter) {
		// KeyFigures ändern:
		scenarioPresenter.getScenario().setKeyFigureList(this.keyFigureList);
		
		// Irgendwas neu zeichnen: TODO Dennis
	}

	
}
