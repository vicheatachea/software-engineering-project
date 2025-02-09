package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "subject")
public class SubjectEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long subjectId;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String code;

	public Long getId() {
		return subjectId;
	}

	public void setId(Long id) {
		this.subjectId = id;
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
}