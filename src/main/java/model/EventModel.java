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

	public List<Event> fetchEventsByUser(LocalDateTime startDate, LocalDateTime endDate) {
		List<Event> events = new ArrayList<>();

		List<TimetableEntity> timetables = timetableDAO.findAllByUserId(UserPreferences.getUserId());

		for (TimetableEntity timetable : timetables) {
			List<Event> eventsByTimetable = fetchEventsByTimetable(startDate, endDate, timetable.getId());
			events.addAll(eventsByTimetable);
		}

		return events;
	}

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

	// Add an event
	public void addEvent(Event event) {
		if (event instanceof TeachingSessionDTO teachingSessionDTO) {
			TeachingSessionEntity entity = convertToTeachingSessionEntity(teachingSessionDTO);
			teachingSessionDAO.persist(entity);
		} else if (event instanceof AssignmentDTO assignmentDTO) {
			AssignmentEntity entity = convertToAssignmentEntity(assignmentDTO);
			assignmentDAO.persist(entity);
		}
	}

	// Update an event
	public void updateEvent(Event event) {
		if (event instanceof TeachingSessionDTO teachingSessionDTO) {
			TeachingSessionEntity entity = convertToTeachingSessionEntity(teachingSessionDTO);
			teachingSessionDAO.update(entity);
		} else if (event instanceof AssignmentDTO assignmentDTO) {
			AssignmentEntity entity = convertToAssignmentEntity(assignmentDTO);
			assignmentDAO.update(entity);
		}
	}

	// Delete an event
	public void deleteEvent(Event event) {
		if (event instanceof TeachingSessionDTO teachingSessionDTO) {
			TeachingSessionEntity entity = convertToTeachingSessionEntity(teachingSessionDTO);
			teachingSessionDAO.delete(entity);
		} else if (event instanceof AssignmentDTO assignmentDTO) {
			AssignmentEntity entity = convertToAssignmentEntity(assignmentDTO);
			assignmentDAO.delete(entity);
		}
	}

	private AssignmentEntity convertToAssignmentEntity(AssignmentDTO dto) {
		AssignmentEntity entity = new AssignmentEntity();


		if (dto.id() != null) {
			entity.setId(dto.id());
		}
		if (dto.description() != null) {
			entity.setDescription(dto.description());
		}
		if (dto.assignmentName() != null) {
			entity.setName(dto.assignmentName());
		}
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

	private AssignmentDTO convertToAssignmentDTO(AssignmentEntity entity) {
		return new AssignmentDTO(entity.getId(), entity.getType(), entity.getPublishingDate().toLocalDateTime(),
		                         entity.getDeadline().toLocalDateTime(), entity.getName(),
		                         entity.getSubject().getCode(),
		                         entity.getDescription() == null ? null : entity.getDescription(),
		                         entity.getTimetable().getId());
	}

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

		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timeTableDAO = new TimetableDAO();

		if (dto.locationName() != null) {
			LocationDAO locationDAO = new LocationDAO();
			entity.setLocation(locationDAO.findByName(dto.locationName()));
		}

		SubjectEntity subjectEntity = subjectDAO.findByCode(dto.subjectCode());
		entity.setSubject(subjectEntity);
		entity.setTimetable(timeTableDAO.findById(dto.timetableId()));

		return entity;
	}

	private TeachingSessionDTO convertToTeachingSessionDTO(TeachingSessionEntity entity) {
		return new TeachingSessionDTO(entity.getId(),
		                              entity.getStartDate().toLocalDateTime(),
		                              entity.getEndDate().toLocalDateTime(),
		                              entity.getLocation() == null ? null : entity.getLocation().getName(),
		                              entity.getSubject().getCode(),
		                              entity.getDescription() == null ? null : entity.getDescription(),
		                              entity.getTimetable().getId());
	}

	public void deleteAllEvents() {
		assignmentDAO.deleteAll();
		teachingSessionDAO.deleteAll();
	}
}
