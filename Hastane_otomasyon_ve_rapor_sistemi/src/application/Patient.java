package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Patient {
	private final StringProperty username;
	private final StringProperty borndate;
	private final StringProperty usergender;
	private final StringProperty userssn;

	public Patient(String username, String borndate, String usergender, String userssn) {
		this.username = new SimpleStringProperty(username);
		this.borndate = new SimpleStringProperty(borndate);
		this.usergender = new SimpleStringProperty(usergender);
		this.userssn = new SimpleStringProperty(userssn);
	}

	// Getter methods
	public String getUsername() {
		return username.get();
	}

	public String getBorndate() {
		return borndate.get();
	}

	public String getUsergender() {
		return usergender.get();
	}

	public String getUserssn() {
		return userssn.get();
	}

	// Property methods
	public StringProperty usernameProperty() {
		return username;
	}

	public StringProperty borndateProperty() {
		return borndate;
	}

	public StringProperty usergenderProperty() {
		return usergender;
	}

	public StringProperty userssnProperty() {
		return userssn;
	}
}