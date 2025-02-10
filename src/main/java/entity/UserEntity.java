package entity;

import jakarta.persistence.*;
import util.PasswordHashUtil;

import java.sql.Date;
import java.util.Set;

@Entity
@Table(name = "user")
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long userId;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String salt;

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String lastName;

	@Column(nullable = false)
	private Date dateOfBirth;

	@Column(nullable = false)
	private String socialNumber;

	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToOne(optional = false)
	@JoinColumn(name = "timetable_id", nullable = false)
	private TimetableEntity timetable;

	@ManyToMany
	@JoinTable(name = "belongs_to",
	           joinColumns = @JoinColumn(name = "user_id"),
	           inverseJoinColumns = @JoinColumn(name = "group_id")
	)
	private Set<UserGroupEntity> groups;

	@ManyToMany
	@JoinTable(name = "teaches",
	           joinColumns = @JoinColumn(name = "user_id"),
	           inverseJoinColumns = @JoinColumn(name = "teaching_session_id")
	)
	private Set<TeachingSessionEntity> teachingSessions;

	public UserEntity() {
	}

	public UserEntity(String firstName, String lastName, Date dateOfBirth, String socialNumber, Role role) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.socialNumber = socialNumber;
		this.role = role;
	}

	public void setId(Long id) {
		this.userId = id;
	}

	public Long getId() {
		return userId;
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

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Date getDateOfBirth() {
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
}
