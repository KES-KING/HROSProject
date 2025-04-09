package application;

import javafx.beans.property.*;

public class OffDay {
	private final IntegerProperty id;
	private final IntegerProperty patientId;
	private final StringProperty docName;
	private final StringProperty docTc;
	private final StringProperty docDate;
	private final StringProperty docTime;
	private final StringProperty docStatus;
	private final StringProperty docComment;

	public OffDay(int id, int patientId, String docName, String docTc, String docDate, String docTime, String docStatus,
			String docComment) {
		this.id = new SimpleIntegerProperty(id);
		this.patientId = new SimpleIntegerProperty(patientId);
		this.docName = new SimpleStringProperty(docName);
		this.docTc = new SimpleStringProperty(docTc);
		this.docDate = new SimpleStringProperty(docDate);
		this.docTime = new SimpleStringProperty(docTime);
		this.docStatus = new SimpleStringProperty(docStatus);
		this.docComment = new SimpleStringProperty(docComment);
	}

	// Getter methods
	public int getId() {
		return id.get();
	}

	public int getPatientId() {
		return patientId.get();
	}

	public String getDocName() {
		return docName.get();
	}

	public String getDocTc() {
		return docTc.get();
	}

	public String getDocDate() {
		return docDate.get();
	}

	public String getDocTime() {
		return docTime.get();
	}

	public String getDocStatus() {
		return docStatus.get();
	}

	public String getDocComment() {
		return docComment.get();
	}

	// Property methods
	public IntegerProperty idProperty() {
		return id;
	}

	public IntegerProperty patientIdProperty() {
		return patientId;
	}

	public StringProperty docNameProperty() {
		return docName;
	}

	public StringProperty docTcProperty() {
		return docTc;
	}

	public StringProperty docDateProperty() {
		return docDate;
	}

	public StringProperty docTimeProperty() {
		return docTime;
	}

	public StringProperty docStatusProperty() {
		return docStatus;
	}

	public StringProperty docCommentProperty() {
		return docComment;
	}
}