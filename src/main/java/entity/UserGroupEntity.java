package entity;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user_group")
public class UserGroupEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String code;

	@Column(nullable = false)
	private Integer capacity;

	@ManyToOne(optional = false)
	@JoinColumn(name = "teacher_id", nullable = false)
	private UserEntity teacher;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "belongs_to",
	           joinColumns = @JoinColumn(name = "group_id"),
	           inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	private Set<UserEntity> students;

	@OneToOne(optional = false)
	@JoinColumn(name = "timetable_id", nullable = false)
	private TimetableEntity timetable;

	public UserGroupEntity() {
	}

	public UserGroupEntity(String name, String code, Integer capacity, UserEntity teacher, Set<UserEntity> students,
	                       TimetableEntity timetable) {
		this.name = name;
		this.code = code;
		this.capacity = capacity;
		this.teacher = teacher;
		this.students = students;
		this.timetable = timetable;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setTeacher(UserEntity teacher) {
		if (teacher.getRole() != Role.TEACHER) {
			throw new IllegalArgumentException("Assigned user must have a teacher role.");
		}
		this.teacher = teacher;
	}

	public UserEntity getTeacher() {
		return teacher;
	}

	public void setStudents(Set<UserEntity> students) {
		if (students.stream().anyMatch(user -> user.getRole() != Role.STUDENT)) {
			throw new IllegalArgumentException("Assigned users must have a student role.");
		}
		if (students.size() > capacity) {
			throw new IllegalArgumentException("The group capacity is exceeded.");
		}
		this.students = students;
	}

	public Set<UserEntity> getStudents() {
		return students;
	}

	public TimetableEntity getTimetable() {
		return timetable;
	}

	public void setTimetable(TimetableEntity timetable) {
		this.timetable = timetable;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		UserGroupEntity userGroup = (UserGroupEntity) obj;
		return Objects.equals(id, userGroup.id) &&
		       Objects.equals(name, userGroup.name) &&
		       Objects.equals(code, userGroup.code) &&
		       Objects.equals(capacity, userGroup.capacity) &&
		       Objects.equals(teacher, userGroup.teacher) &&
		       Objects.equals(students, userGroup.students) &&
		       Objects.equals(timetable, userGroup.timetable);
	}
}
