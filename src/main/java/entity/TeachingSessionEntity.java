package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
	private Timestamp startDate;

	@Column(name = "end_date", nullable = false)
	private Timestamp endDate;

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

	@Column(nullable = false, name = "locale_code")
	private String localeCode;

	public TeachingSessionEntity() {
	}

	public TeachingSessionEntity(Timestamp startDate, Timestamp endDate, String description, LocationEntity location,
	                             TimetableEntity timetable, SubjectEntity subject, String localeCode) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.description = description;
		this.location = location;
		this.timetable = timetable;
		this.subject = subject;
		this.localeCode = localeCode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocationEntity getLocation() {
		return location;
	}

	public void setLocation(LocationEntity location) {
		this.location = location;
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

	public Set<UserEntity> getTeachers() {
		return teachers;
	}

	public void setTeachers(Set<UserEntity> teachers) {
		this.teachers = teachers;
	}

	public String getLocaleCode() {
		return localeCode;
	}

	public void setLocaleCode(String localeCode) {
		this.localeCode = localeCode;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		TeachingSessionEntity that = (TeachingSessionEntity) o;

		return Objects.equals(id, that.id)
				&& Objects.equals(startDate, that.startDate)
				&& Objects.equals(endDate, that.endDate)
				&& Objects.equals(location, that.location)
				&& Objects.equals(timetable, that.timetable)
				&& Objects.equals(subject, that.subject);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, startDate, endDate, location, timetable, subject);
	}
}