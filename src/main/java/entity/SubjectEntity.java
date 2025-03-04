package entity;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "subject")
public class SubjectEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String code;

	@OneToMany(mappedBy = "subject")
	private Set<UserGroupEntity> userGroups;


	public SubjectEntity() {
	}

	public SubjectEntity(Long id, String name, String code) {
		this.id = id;
		this.name = name;
		this.code = code;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SubjectEntity that = (SubjectEntity) o;
		return Objects.equals(id, that.id) &&
		       Objects.equals(name, that.name) &&
		       Objects.equals(code, that.code);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, code);
	}
}