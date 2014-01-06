package de.bachelor.smartSchedules.shared.model.util.event;

import de.bachelor.smartSchedules.client.presenter.ScenarioPresenter;
/**
 * Erhört die IDs im Scenario beim User.
 * @author timo
 *
 */
public class IncreaseScenarioIDsEvent implements UtilEvent{

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -7691566989971598634L;
	private int newOrderIDCount, newProductIDCount, newVariantIDCount, newOperationIDCount, newResourceIDCount;
	
	/**
	 * Default
	 */
	public IncreaseScenarioIDsEvent() {
		
	}
	
	public IncreaseScenarioIDsEvent(int newOrderIDCount, int newProductIDCount, int newVariantIDCount, 
			int newOperationIDCount, int newResourceIDCount) {
		
		this.newOrderIDCount = newOrderIDCount;
		this.newProductIDCount = newProductIDCount;
		this.newVariantIDCount = newVariantIDCount;
		this.newOperationIDCount = newOperationIDCount;
		this.newResourceIDCount = newResourceIDCount;
	}

	@Override
	public void process(ScenarioPresenter scenarioPresenter) {
		
		scenarioPresenter.getScenario().setNewOrderIDCount(newOrderIDCount);
		scenarioPresenter.getScenario().setNewProductIDCount(newProductIDCount);
		scenarioPresenter.getScenario().setNewVariantIDCount(newVariantIDCount);
		scenarioPresenter.getScenario().setNewOperationIDCount(newOperationIDCount);
		scenarioPresenter.getScenario().setNewResourceIDCount(newResourceIDCount);
		
		// TODO Dennis -> InvokeEventView wieder freigeben.
	}
}
