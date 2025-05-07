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

/**
 * Manages calendar events for users, including teaching sessions and assignments.
 * Provides functionality for retrieving, creating, updating, and deleting events
 * with support for filtering by time periods, timetable, and locale.
 */
public class EventModel {

	private static final AssignmentDAO assignmentDAO = new AssignmentDAO();
	private static final TeachingSessionDAO teachingSessionDAO = new TeachingSessionDAO();
	private static final TimetableDAO timetableDAO = new TimetableDAO();

	private static final UserModel userModel = new UserModel();

	/**
	 * Retrieves all events for the current user within a specified time period.
	 *
	 * @param startDate The beginning of the time period
	 * @param endDate   The end of the time period
	 * @return A list of events (teaching sessions and assignments) for the user
	 */
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

	/**
	 * Retrieves all events associated with a specific timetable within a time period.
	 *
	 * @param startDate   The beginning of the time period
	 * @param endDate     The end of the time period
	 * @param timetableId The ID of the timetable to find events for
	 * @return A list of events (teaching sessions and assignments) for the timetable
	 */
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

	/**
	 * Retrieves all events for the current user within a specified time period and locale.
	 *
	 * @param startDate  The beginning of the time period
	 * @param endDate    The end of the time period
	 * @param localeCode The locale code to filter events by
	 * @return A list of events (teaching sessions and assignments) matching the criteria
	 */
	public List<Event> fetchEventsByUserAndLocale(LocalDateTime startDate, LocalDateTime endDate, String localeCode) {
		List<Event> events = new ArrayList<>();

		long userId = userModel.fetchCurrentUserId();

		List<TimetableEntity> timetables = timetableDAO.findAllByUserId(userId);

		for (TimetableEntity timetable : timetables) {
			List<Event> eventsByTimetable =
					fetchEventsByTimetableAndLocale(startDate, endDate, timetable.getId(), localeCode);
			events.addAll(eventsByTimetable);
		}

		return events;
	}

	/**
	 * Retrieves events for a specific timetable and locale within a time period.
	 *
	 * @param startDate   The beginning of the time period
	 * @param endDate     The end of the time period
	 * @param timetableId The ID of the timetable
	 * @param localeCode  The locale code to filter events by
	 * @return A list of events matching the criteria
	 */
	private List<Event> fetchEventsByTimetableAndLocale(LocalDateTime startDate, LocalDateTime endDate,
	                                                    Long timetableId, String localeCode) {
		List<Event> events = new ArrayList<>();

		List<TeachingSessionEntity> teachingSessions =
				teachingSessionDAO.findAllByLocaleDuringPeriod(Timestamp.valueOf(startDate), Timestamp.valueOf(endDate),
				                                               localeCode, timetableId);

		if (teachingSessions != null) {
			for (TeachingSessionEntity teachingSession : teachingSessions) {
				events.add(convertToTeachingSessionDTO(teachingSession));
			}
		}

		List<AssignmentEntity> assignments =
				assignmentDAO.findAllByLocaleDuringPeriod(Timestamp.valueOf(startDate), Timestamp.valueOf(endDate),
				                                          localeCode, timetableId);

		if (assignments != null) {
			for (AssignmentEntity assignment : assignments) {
				events.add(convertToAssignmentDTO(assignment));
			}
		}

		return events;
	}

	/**
	 * Adds a new event (teaching session or assignment) to the system.
	 *
	 * @param event The event to add (must be TeachingSessionDTO or AssignmentDTO)
	 * @throws IllegalArgumentException if the event data is invalid
	 */
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

	/**
	 * Updates an existing event in the system.
	 *
	 * @param event The event to update (must be TeachingSessionDTO or AssignmentDTO)
	 * @throws IllegalArgumentException if the event data is invalid
	 */
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

	/**
	 * Deletes an event from the system.
	 *
	 * @param event The event to delete (must be TeachingSessionDTO or AssignmentDTO)
	 * @throws IllegalArgumentException if the event data is invalid
	 */
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

	/**
	 * Converts an AssignmentDTO to an AssignmentEntity.
	 *
	 * @param dto The AssignmentDTO to convert
	 * @return The corresponding AssignmentEntity
	 */
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
		entity.setLocaleCode(dto.localeCode());

		return entity;
	}

	/**
	 * Converts an AssignmentEntity to an AssignmentDTO.
	 *
	 * @param entity The AssignmentEntity to convert
	 * @return The corresponding AssignmentDTO
	 */
	private AssignmentDTO convertToAssignmentDTO(AssignmentEntity entity) {
		return new AssignmentDTO(entity.getId(), entity.getType(), entity.getPublishingDate().toLocalDateTime(),
		                         entity.getDeadline().toLocalDateTime(), entity.getName(),
		                         entity.getSubject().getCode(),
		                         entity.getDescription() == null ? null : entity.getDescription(),
		                         entity.getTimetable().getId(),
		                         entity.getLocaleCode());
	}

	/**
	 * Converts a TeachingSessionDTO to a TeachingSessionEntity.
	 *
	 * @param dto The TeachingSessionDTO to convert
	 * @return The corresponding TeachingSessionEntity
	 */
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

		entity.setLocaleCode(dto.localeCode());

		return entity;
	}

	/**
	 * Converts a TeachingSessionEntity to a TeachingSessionDTO.
	 *
	 * @param entity The TeachingSessionEntity to convert
	 * @return The corresponding TeachingSessionDTO
	 */
	private TeachingSessionDTO convertToTeachingSessionDTO(TeachingSessionEntity entity) {
		return new TeachingSessionDTO(entity.getId(), entity.getStartDate().toLocalDateTime(),
		                              entity.getEndDate().toLocalDateTime(),
		                              entity.getLocation() == null ? null : entity.getLocation().getName(),
		                              entity.getSubject().getCode(),
		                              entity.getDescription() == null ? null : entity.getDescription(),
		                              entity.getTimetable().getId(),
		                              entity.getLocaleCode());
	}

	/**
	 * Deletes all events from the system.
	 */
	public void deleteAllEvents() {
		assignmentDAO.deleteAll();
		teachingSessionDAO.deleteAll();
	}

	/**
	 * Validates a teaching session's data.
	 *
	 * @param teachingSessionDTO The teaching session to validate
	 * @throws IllegalArgumentException if any validation fails
	 */
	private void isValidTeachingSession(TeachingSessionDTO teachingSessionDTO) {
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
	}

	/**
	 * Validates an assignment's data.
	 *
	 * @param assignmentDTO The assignment to validate
	 * @throws IllegalArgumentException if any validation fails
	 */
	private void isValidAssignment(AssignmentDTO assignmentDTO) {
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
	}
}
