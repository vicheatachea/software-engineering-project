package entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "assignment")
public class AssignmentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String type;

	@Column(name = "publishing_date", nullable = false)
	private Timestamp publishingDate;

	@Column(nullable = false)
	private Timestamp deadline;

	@Column
	private String description;

	@ManyToOne(optional = false)
	@JoinColumn(name = "subject_id", nullable = false)
	private SubjectEntity subject;

	@ManyToOne(optional = false)
	@JoinColumn(name = "timetable_id", nullable = false)
	private TimetableEntity timetable;

	@Column(nullable = false, name = "locale_code")
	private String localeCode;

	public AssignmentEntity() {
	}

	public AssignmentEntity(String name, String type, Timestamp publishingDate, Timestamp deadline, String description,
	                        SubjectEntity subject, TimetableEntity timetable, String localeCode) {
		this.name = name;
		this.type = type;
		this.publishingDate = publishingDate;
		this.deadline = deadline;
		this.description = description;
		this.subject = subject;
		this.timetable = timetable;
		this.localeCode = localeCode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Timestamp getPublishingDate() {
		return publishingDate;
	}

	public void setPublishingDate(Timestamp publishingDate) {
		this.publishingDate = publishingDate;
	}

	public Timestamp getDeadline() {
		return deadline;
	}

	public void setDeadline(Timestamp deadline) {
		this.deadline = deadline;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TimetableEntity getTimetable() {
		return timetable;
	}

	public void setTimetable(TimetableEntity timetable) {
		this.timetable = timetable;
	}

	public SubjectEntity getSubject() {
		return subject;
	}

	public void setSubject(SubjectEntity subject) {
		this.subject = subject;
	}

	public String getLocaleCode() {
		return localeCode;
	}

	public void setLocaleCode(String localeCode) {
		this.localeCode = localeCode;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AssignmentEntity that = (AssignmentEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(type, that.type) &&
		       Objects.equals(publishingDate, that.publishingDate) && Objects.equals(deadline, that.deadline) &&
		       Objects.equals(subject, that.subject) && Objects.equals(timetable, that.timetable);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, type, publishingDate, deadline, subject, timetable);
	}
}
