package entity;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.Set;

@Entity
@Table(name = "teaching_session")
public class TeachingSessionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long teachingSessionId;

	@Column(nullable = false)
	private Date StartDate;

	@Column(nullable = false)
	private Date EndDate;

	@ManyToOne
	@JoinColumn(name = "location_id")
	private LocationEntity location;

	@ManyToOne(optional = false)
	@JoinColumn(name = "timetable_id", nullable = false)
	private TimetableEntity timetable;

	@ManyToOne(optional = false)
	@JoinColumn(name = "subject_id", nullable = false)
	private SubjectEntity subject;

	@ManyToMany(mappedBy = "teachingSessions")
	private Set<UserEntity> teachers;

	public TeachingSessionEntity() {
	}

	public TeachingSessionEntity(Date StartDate, Date EndDate, LocationEntity location, TimetableEntity timetable,
	                             SubjectEntity subject) {
		this.StartDate = StartDate;
		this.EndDate = EndDate;
		this.location = location;
		this.timetable = timetable;
		this.subject = subject;
	}

	public void setId(Long id) {
		this.teachingSessionId = id;
	}

	public Long getId() {
		return teachingSessionId;
	}

	public void setStartDate(Date StartDate) {
		this.StartDate = StartDate;
	}

	public Date getStartDate() {
		return StartDate;
	}

	public void setEndDate(Date EndDate) {
		this.EndDate = EndDate;
	}

	public Date getEndDate() {
		return EndDate;
	}

	public void setLocation(LocationEntity location) {
		this.location = location;
	}

	public LocationEntity getLocation() {
		return location;
	}

	public void setTimetable(TimetableEntity timetable) {
		this.timetable = timetable;
	}

	public TimetableEntity getTimetable() {
		return timetable;
	}

	public void setSubject(SubjectEntity subject) {
		this.subject = subject;
	}

	public SubjectEntity getSubject() {
		return subject;
	}

	public void setTeachers(Set<UserEntity> teachers) {
		this.teachers = teachers;
	}

	public Set<UserEntity> getTeachers() {
		return teachers;
	}
}