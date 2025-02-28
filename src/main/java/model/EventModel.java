package model;

import dao.AssignmentDAO;
import dao.SubjectDAO;
import dao.TeachingSessionDAO;
import dao.TimetableDAO;
import dto.AssignmentDTO;
import dto.Event;
import dto.TeachingSessionDTO;
import entity.AssignmentEntity;
import entity.SubjectEntity;
import entity.TeachingSessionEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventModel {

	private static final AssignmentDAO assignmentDAO = new AssignmentDAO();
	private static final TeachingSessionDAO teachingSessionDAO = new TeachingSessionDAO();

	public List<Event> fetchEventsByUser(LocalDateTime startDate, LocalDateTime endDate, Long timetableId) {
		List<Event> events = new ArrayList<>();

		List<TeachingSessionEntity> teachingSessions =
				teachingSessionDAO.findAllByTimetableIdDuringPeriod(timetableId, startDate, endDate);

		if (teachingSessions != null) {
			for (TeachingSessionEntity teachingSession : teachingSessions) {
				events.add(convertToTeachingSessionDTO(teachingSession));
			}
		}

		List<AssignmentEntity> assignments =
				assignmentDAO.findAllByTimetableIdDuringPeriod(timetableId, startDate, endDate);

		if (assignments != null) {
			for (AssignmentEntity assignment : assignments) {
				events.add(convertToAssignmentDTO(assignment));
			}
		}
		return events;
	}

	// Add an event for a user
	public void addEventForUser(Event event) {
		if (event instanceof TeachingSessionDTO teachingSessionDTO) {
			TeachingSessionEntity entity = convertToTeachingSessionEntity(teachingSessionDTO);
			teachingSessionDAO.persist(entity);
		} else if (event instanceof AssignmentDTO assignmentDTO) {
			AssignmentEntity entity = convertToAssignmentEntity(assignmentDTO);
			assignmentDAO.persist(entity);
		}
	}

	// Update an event for a user
	public void updateEventForUser(Event event) {
		if (event instanceof TeachingSessionDTO teachingSessionDTO) {
			TeachingSessionEntity entity = convertToTeachingSessionEntity(teachingSessionDTO);
			teachingSessionDAO.persist(entity);
		} else if (event instanceof AssignmentDTO assignmentDTO) {
			AssignmentEntity entity = convertToAssignmentEntity(assignmentDTO);
			assignmentDAO.persist(entity);
		}
	}

	// Delete an event for a user
	public void deleteEventForUser(Event event) {
		if (event instanceof TeachingSessionDTO teachingSessionDTO) {
			TeachingSessionEntity entity = convertToTeachingSessionEntity(teachingSessionDTO);
			teachingSessionDAO.deleteById(entity.getId());
		} else if (event instanceof AssignmentDTO assignmentDTO) {
			AssignmentEntity entity = convertToAssignmentEntity(assignmentDTO);
			assignmentDAO.deleteById(entity.getId());
		}
	}

	// Add an event for a group
	public void addEventForGroup(Event event, String groupName) {
		if (event instanceof TeachingSessionDTO teachingSessionDTO) {
			TeachingSessionEntity entity = convertToTeachingSessionEntity(teachingSessionDTO);
			teachingSessionDAO.persistForGroup(entity, groupName);
		} else if (event instanceof AssignmentDTO assignmentDTO) {
			AssignmentEntity entity = convertToAssignmentEntity(assignmentDTO);
			assignmentDAO.persistForGroup(entity, groupName);
		}
	}

	// Update an event for a group
	public void updateEventForGroup(Event event, String groupName) {
		if (event instanceof TeachingSessionDTO teachingSessionDTO) {
			TeachingSessionEntity entity = convertToTeachingSessionEntity(teachingSessionDTO);
			teachingSessionDAO.persistForGroup(entity, groupName);
		} else if (event instanceof AssignmentDTO assignmentDTO) {
			AssignmentEntity entity = convertToAssignmentEntity(assignmentDTO);
			assignmentDAO.persistForGroup(entity, groupName);
		}
	}

	// Delete an event for a group
	public void deleteEventForGroup(Event event, String groupName) {
		if (event instanceof TeachingSessionDTO teachingSessionDTO) {
			TeachingSessionEntity entity = convertToTeachingSessionEntity(teachingSessionDTO);
			teachingSessionDAO.deleteByGroupName(entity, groupName);
		} else if (event instanceof AssignmentDTO assignmentDTO) {
			AssignmentEntity entity = convertToAssignmentEntity(assignmentDTO);
			assignmentDAO.deleteByGroupName(entity, groupName);
		}
	}

	private AssignmentEntity convertToAssignmentEntity(AssignmentDTO dto) {
		AssignmentEntity entity = new AssignmentEntity();

		entity.setId(dto.id());
		entity.setType(dto.type());
		entity.setPublishingDate(Timestamp.valueOf(dto.publishingDate()));

		entity.setDeadline(dto.deadline() != null ? Timestamp.valueOf(dto.deadline()) : null);

		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timeTableDAO = new TimetableDAO();

		SubjectEntity subjectEntity = subjectDAO.findByName(dto.subjectName());
		entity.setSubject(subjectEntity);
		entity.setTimetable(timeTableDAO.findById(dto.timetableId()));

		return entity;
	}

	private AssignmentDTO convertToAssignmentDTO(AssignmentEntity entity) {
		return new AssignmentDTO(entity.getId(), entity.getType(), entity.getPublishingDate().toLocalDateTime(),
		                         entity.getDeadline() != null ? entity.getDeadline().toLocalDateTime() : null,
		                         entity.getName(), entity.getSubject().getName(), entity.getDescription(),
		                         entity.getTimetable().getId());
	}

	private TeachingSessionEntity convertToTeachingSessionEntity(TeachingSessionDTO dto) {
		TeachingSessionEntity entity = new TeachingSessionEntity();

		entity.setId(dto.id());
		entity.setStartDate(Timestamp.valueOf(dto.startDate()));
		entity.setEndDate(Timestamp.valueOf(dto.endDate()));

		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timeTableDAO = new TimetableDAO();

		SubjectEntity subjectEntity = subjectDAO.findByName(dto.subjectName());
		entity.setSubject(subjectEntity);
		entity.setTimetable(timeTableDAO.findById(dto.timetableId()));

		return entity;
	}

	private TeachingSessionDTO convertToTeachingSessionDTO(TeachingSessionEntity entity) {
		return new TeachingSessionDTO(entity.getId(), entity.getStartDate().toLocalDateTime(),
		                              entity.getEndDate().toLocalDateTime(), entity.getLocation().getName(),
		                              entity.getSubject().getName(), entity.getDescription(),
		                              entity.getTimetable().getId());
	}
}
