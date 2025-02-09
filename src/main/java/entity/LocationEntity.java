package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "location")
public class LocationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long locationId;

	@Column(nullable = false)
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
		this.locationId = id;
	}

	public Long getId() {
		return locationId;
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
}
