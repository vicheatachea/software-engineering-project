package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "timetable")
public class TimetableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long timetableId;

	public void setId(Long id) {
		this.timetableId = id;
	}

	public Long getId() {
		return timetableId;
	}
}
