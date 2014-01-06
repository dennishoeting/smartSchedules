package de.bachelor.smartSchedules.client.presenter;

import de.bachelor.smartSchedules.shared.model.util.User;

public class UserPresenter {
	private static UserPresenter instance = new UserPresenter();

	/**
	 * Flag: AdvancedUser oder nur simpel
	 */
	private boolean isAdvancedUser;
	
	private User user;
	
	private UserPresenter() {
		/*
		 * Temp!
		 */
		this.isAdvancedUser = true;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return this.user;
	}
	
	public static UserPresenter getInstance() {
		return instance;
	}

	public boolean isAdvancedUser() {
		return isAdvancedUser;
	}

	public void setAdvancedUser(boolean isAdvancedUser) {
		this.isAdvancedUser = isAdvancedUser;
	}
}
