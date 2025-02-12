package entity;

import jakarta.persistence.*;
import util.PasswordHashUtil;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user")
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "username", nullable = false)
	private String username;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "salt", nullable = false)
	private String salt;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "last_name", nullable = false)
	private String lastName;

	@Column(name = "date_of_birth", nullable = false)
	private Timestamp dateOfBirth;

	@Column(name = "social_number", nullable = false)
	private String socialNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private Role role;

	@OneToOne(optional = false)
	@JoinColumn(name = "timetable_id", nullable = false)
	private TimetableEntity timetable;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "belongs_to", joinColumns = @JoinColumn(name = "user_id"),
	           inverseJoinColumns = @JoinColumn(name = "group_id"))
	private Set<UserGroupEntity> groups;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "teaches", joinColumns = @JoinColumn(name = "user_id"),
	           inverseJoinColumns = @JoinColumn(name = "teaching_session_id"))
	private Set<TeachingSessionEntity> teachingSessions;

	public UserEntity() {
	}

	public UserEntity(String firstName, String lastName, String username, String password, Timestamp dateOfBirth,
	                  String socialNumber, Role role, TimetableEntity timetable) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		setPassword(password);
		this.dateOfBirth = dateOfBirth;
		this.socialNumber = socialNumber;
		this.role = role;
		this.timetable = timetable;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setDateOfBirth(Timestamp dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Timestamp getDateOfBirth() {
		return dateOfBirth;
	}

	public void setSocialNumber(String socialNumber) {
		this.socialNumber = socialNumber;
	}

	public String getSocialNumber() {
		return socialNumber;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Role getRole() {
		return role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.salt = PasswordHashUtil.generateSalt();
		this.password = PasswordHashUtil.hashPassword(password, this.salt);
	}

	public String getSalt() {
		return salt;
	}

	public Set<UserGroupEntity> getGroups() {
		return groups;
	}

	public TimetableEntity getTimetable() {
		return timetable;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		UserEntity user = (UserEntity) obj;
		return Objects.equals(id, user.id) && Objects.equals(username, user.username) &&
		       Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) &&
		       Objects.equals(dateOfBirth, user.dateOfBirth) && Objects.equals(socialNumber, user.socialNumber) &&
		       Objects.equals(role, user.role) && Objects.equals(timetable, user.timetable);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, username, firstName, lastName, dateOfBirth, socialNumber, role, timetable);
	}
}