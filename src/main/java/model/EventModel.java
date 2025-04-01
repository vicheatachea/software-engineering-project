package model;

import dao.*;
import dto.AssignmentDTO;
import dto.Event;
import dto.TeachingSessionDTO;
import entity.AssignmentEntity;
import entity.SubjectEntity;
import entity.TeachingSessionEntity;
import entity.TimetableEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class EventModel {

	private static final AssignmentDAO assignmentDAO = new AssignmentDAO();
	private static final TeachingSessionDAO teachingSessionDAO = new TeachingSessionDAO();
	private static final TimetableDAO timetableDAO = new TimetableDAO();

	private static final UserModel userModel = new UserModel();

	public List<Event> fetchEventsByUser(LocalDateTime startDate, LocalDateTime endDate) {
		List<Event> events = new ArrayList<>();

		long userId = userModel.fetchCurrentUserId();

		List<TimetableEntity> timetables = timetableDAO.findAllByUserId(userId);

		for (TimetableEntity timetable : timetables) {
			List<Event> eventsByTimetable = fetchEventsByTimetable(startDate, endDate, timetable.getId());
			events.addAll(eventsByTimetable);
		}

		return events;
	}

	// Fetch events by timetable (Either group or user)
	public List<Event> fetchEventsByTimetable(LocalDateTime startDate, LocalDateTime endDate, long timetableId) {
		List<Event> events = new ArrayList<>();

		List<TeachingSessionEntity> teachingSessions =
				teachingSessionDAO.findAllByTimetableIdDuringPeriod(timetableId, Timestamp.valueOf(startDate),
				                                                    Timestamp.valueOf(endDate));

		if (teachingSessions != null) {
			for (TeachingSessionEntity teachingSession : teachingSessions) {
				events.add(convertToTeachingSessionDTO(teachingSession));
			}
		}

		List<AssignmentEntity> assignments =
				assignmentDAO.findAllByTimetableIdDuringPeriod(timetableId, Timestamp.valueOf(startDate),
				                                               Timestamp.valueOf(endDate));

		if (assignments != null) {
			for (AssignmentEntity assignment : assignments) {
				events.add(convertToAssignmentDTO(assignment));
			}
		}
		return events;
	}

	// Fetch events by locale
	public List<Event> fetchEventsByLocale(LocalDateTime startDate, LocalDateTime endDate, String localeCode) {
		List<Event> events = new ArrayList<>();

		List<TeachingSessionEntity> teachingSessions =
				teachingSessionDAO.findAllByLocaleDuringPeriod(Timestamp.valueOf(startDate), Timestamp.valueOf(endDate),
				                                               localeCode);

		if (teachingSessions != null) {
			for (TeachingSessionEntity teachingSession : teachingSessions) {
				events.add(convertToTeachingSessionDTO(teachingSession));
			}
		}

		List<AssignmentEntity> assignments =
				assignmentDAO.findAllByLocaleDuringPeriod(Timestamp.valueOf(startDate), Timestamp.valueOf(endDate),
				                                          localeCode);

		if (assignments != null) {
			for (AssignmentEntity assignment : assignments) {
				events.add(convertToAssignmentDTO(assignment));
			}
		}

		return events;
	}

	// Add an event

	public void addEvent(Event event) {
		if (event instanceof TeachingSessionDTO teachingSessionDTO) {
			try {
				isValidTeachingSession(teachingSessionDTO);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid teaching session data: " + e.getMessage());
			}
			TeachingSessionEntity entity = convertToTeachingSessionEntity(teachingSessionDTO);
			teachingSessionDAO.persist(entity);
		} else if (event instanceof AssignmentDTO assignmentDTO) {
			try {
				isValidAssignment(assignmentDTO);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid assignment data: " + e.getMessage());
			}
			AssignmentEntity entity = convertToAssignmentEntity(assignmentDTO);
			assignmentDAO.persist(entity);
		}
	}

	// Update an event
	public void updateEvent(Event event) {
		if (event instanceof TeachingSessionDTO teachingSessionDTO) {
			try {
				isValidTeachingSession(teachingSessionDTO);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid teaching session data: " + e.getMessage());
			}
			TeachingSessionEntity entity = convertToTeachingSessionEntity(teachingSessionDTO);
			teachingSessionDAO.update(entity);
		} else if (event instanceof AssignmentDTO assignmentDTO) {
			try {
				isValidAssignment(assignmentDTO);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid assignment data: " + e.getMessage());
			}
			AssignmentEntity entity = convertToAssignmentEntity(assignmentDTO);
			assignmentDAO.update(entity);
		}
	}

	// Delete an event
	public void deleteEvent(Event event) {
		if (event instanceof TeachingSessionDTO teachingSessionDTO) {
			try {
				isValidTeachingSession(teachingSessionDTO);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid teaching session data: " + e.getMessage());
			}
			TeachingSessionEntity entity = convertToTeachingSessionEntity(teachingSessionDTO);
			teachingSessionDAO.delete(entity);
		} else if (event instanceof AssignmentDTO assignmentDTO) {
			try {
				isValidAssignment(assignmentDTO);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid assignment data: " + e.getMessage());
			}
			AssignmentEntity entity = convertToAssignmentEntity(assignmentDTO);
			assignmentDAO.delete(entity);
		}
	}

	// Converts an Assignment DTO to an Entity
	private AssignmentEntity convertToAssignmentEntity(AssignmentDTO dto) {
		AssignmentEntity entity = new AssignmentEntity();

		if (dto.id() != null) {
			entity.setId(dto.id());
		}

		if (dto.description() != null) {
			entity.setDescription(dto.description());
		}

		entity.setName(dto.assignmentName());

		entity.setType(dto.type());
		entity.setPublishingDate(Timestamp.valueOf(dto.publishingDate()));

		entity.setDeadline(Timestamp.valueOf(dto.deadline()));

		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timeTableDAO = new TimetableDAO();

		SubjectEntity subjectEntity = subjectDAO.findByCode(dto.subjectCode());
		entity.setSubject(subjectEntity);
		entity.setTimetable(timeTableDAO.findById(dto.timetableId()));

		return entity;
	}

	// Converts an Assignment Entity to a DTO
	private AssignmentDTO convertToAssignmentDTO(AssignmentEntity entity) {
		return new AssignmentDTO(entity.getId(), entity.getType(), entity.getPublishingDate().toLocalDateTime(),
		                         entity.getDeadline().toLocalDateTime(), entity.getName(),
		                         entity.getSubject().getCode(),
		                         entity.getDescription() == null ? null : entity.getDescription(),
		                         entity.getTimetable().getId(),
		                         entity.getLocaleCode());
	}

	// Converts an TeachingSession DTO to an Entity
	private TeachingSessionEntity convertToTeachingSessionEntity(TeachingSessionDTO dto) {
		TeachingSessionEntity entity = new TeachingSessionEntity();

		if (dto.id() != null) {
			entity.setId(dto.id());
		}

		if (dto.description() != null) {
			entity.setDescription(dto.description());
		}

		entity.setStartDate(Timestamp.valueOf(dto.startDate()));
		entity.setEndDate(Timestamp.valueOf(dto.endDate()));

		if (dto.locationName() != null) {
			LocationDAO locationDAO = new LocationDAO();
			entity.setLocation(locationDAO.findByName(dto.locationName()));
		}

		SubjectDAO subjectDAO = new SubjectDAO();
		entity.setSubject(subjectDAO.findByCode(dto.subjectCode()));

		TimetableDAO timeTableDAO = new TimetableDAO();
		entity.setTimetable(timeTableDAO.findById(dto.timetableId()));

		return entity;
	}

	// Converts an TeachingSession Entity to a DTO
	private TeachingSessionDTO convertToTeachingSessionDTO(TeachingSessionEntity entity) {
		return new TeachingSessionDTO(entity.getId(), entity.getStartDate().toLocalDateTime(),
		                              entity.getEndDate().toLocalDateTime(),
		                              entity.getLocation() == null ? null : entity.getLocation().getName(),
		                              entity.getSubject().getCode(),
		                              entity.getDescription() == null ? null : entity.getDescription(),
		                              entity.getTimetable().getId(),
		                              entity.getLocaleCode());
	}

	public void deleteAllEvents() {
		assignmentDAO.deleteAll();
		teachingSessionDAO.deleteAll();
	}

	private boolean isValidTeachingSession(TeachingSessionDTO teachingSessionDTO) {
		if (teachingSessionDTO.startDate() == null) {
			throw new IllegalArgumentException("Start date cannot be null.");
		}
		if (teachingSessionDTO.endDate() == null) {
			throw new IllegalArgumentException("End date cannot be null.");
		}
		if (teachingSessionDTO.startDate().isAfter(teachingSessionDTO.endDate())) {
			throw new IllegalArgumentException("Start date cannot be after end date.");
		}
		if (teachingSessionDTO.subjectCode() == null || teachingSessionDTO.subjectCode().isEmpty()) {
			throw new IllegalArgumentException("Subject code cannot be null or empty.");
		}
		if (teachingSessionDTO.timetableId() <= 0) {
			throw new IllegalArgumentException("Timetable ID cannot be less than or equal to 0.");
		}
		return true;
	}

	private boolean isValidAssignment(AssignmentDTO assignmentDTO) {
		if (assignmentDTO.assignmentName() == null || assignmentDTO.assignmentName().isEmpty()) {
			throw new IllegalArgumentException("Assignment name cannot be null or empty.");
		}
		if (assignmentDTO.type() == null || assignmentDTO.type().isEmpty()) {
			throw new IllegalArgumentException("Assignment type cannot be null or empty.");
		}
		if (assignmentDTO.publishingDate() == null) {
			throw new IllegalArgumentException("Publishing date cannot be null.");
		}
		if (assignmentDTO.deadline() == null) {
			throw new IllegalArgumentException("Deadline cannot be null.");
		}
		if (assignmentDTO.deadline().isBefore(assignmentDTO.publishingDate())) {
			throw new IllegalArgumentException("Deadline cannot be before publishing date.");
		}
		if (assignmentDTO.subjectCode() == null || assignmentDTO.subjectCode().isEmpty()) {
			throw new IllegalArgumentException("Subject code cannot be null or empty.");
		}
		if (assignmentDTO.timetableId() <= 0) {
			throw new IllegalArgumentException("Timetable ID cannot be less than or equal to 0.");
		}
		return true;
	}

}
