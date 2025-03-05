package entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "location")
public class LocationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = false)
	private String campus;

	@Column(nullable = false)
	private String building;

	public LocationEntity() {
	}

	public LocationEntity(String name, String campus, String building) {
		this.name = name;
		this.campus = campus;
		this.building = building;
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

	public void setCampus(String campus) {
		this.campus = campus;
	}

	public String getCampus() {
		return campus;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getBuilding() {
		return building;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		LocationEntity location = (LocationEntity) obj;
		return name.equals(location.name) && campus.equals(location.campus)
		       && building.equals(location.building);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, campus, building);
	}
}
