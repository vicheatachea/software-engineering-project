package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

	public SubjectEntity(String name, String code) {
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