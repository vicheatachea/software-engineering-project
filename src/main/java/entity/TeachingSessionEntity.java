package entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "teaching_session")
public class TeachingSessionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "start_date", nullable = false)
	private Timestamp StartDate;

	@Column(name = "end_date", nullable = false)
	private Timestamp EndDate;

	@Column
	private String description;

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

	public TeachingSessionEntity(Timestamp StartDate, Timestamp EndDate, String description, LocationEntity location,
	                             TimetableEntity timetable, SubjectEntity subject) {
		this.StartDate = StartDate;
		this.EndDate = EndDate;
		this.description = description;
		this.location = location;
		this.timetable = timetable;
		this.subject = subject;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setStartDate(Timestamp StartDate) {
		this.StartDate = StartDate;
	}

	public Timestamp getStartDate() {
		return StartDate;
	}

	public void setEndDate(Timestamp EndDate) {
		this.EndDate = EndDate;
	}

	public Timestamp getEndDate() {
		return EndDate;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TeachingSessionEntity that = (TeachingSessionEntity) o;

		return Objects.equals(id, that.id) && Objects.equals(StartDate, that.StartDate) &&
		       Objects.equals(EndDate, that.EndDate) && Objects.equals(location, that.location) &&
		       Objects.equals(timetable, that.timetable) && Objects.equals(subject, that.subject);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, StartDate, EndDate, location, timetable, subject);
	}
}