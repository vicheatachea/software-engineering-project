package entity;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "assignment")
public class AssignmentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long assignmentId;

	@Column(nullable = false)
	private String type;

	@Column(nullable = false)
	private Date publishingDate;

	@Column(nullable = false)
	private Date deadline;

	@ManyToOne(optional = false)
	@JoinColumn(name = "subject_id", nullable = false)
	private SubjectEntity subject;

	@ManyToOne(optional = false)
	@JoinColumn(name = "timetable_id", nullable = false)
	private TimetableEntity timetable;

	public AssignmentEntity() {
	}

	public AssignmentEntity(String type, Date publishingDate, Date deadline, SubjectEntity subject,
	                        TimetableEntity timetable) {
		this.type = type;
		this.publishingDate = publishingDate;
		this.deadline = deadline;
		this.subject = subject;
		this.timetable = timetable;
	}

	public void setId(Long id) {
		this.assignmentId = id;
	}

	public Long getId() {
		return assignmentId;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setPublishingDate(Date publishingDate) {
		this.publishingDate = publishingDate;
	}

	public Date getPublishingDate() {
		return publishingDate;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public Date getDeadline() {
		return deadline;
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
}
