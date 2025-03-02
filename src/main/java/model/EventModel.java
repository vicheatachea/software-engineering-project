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
			teachingSessionDAO.persist(entity);
		} else if (event instanceof AssignmentDTO assignmentDTO) {
			AssignmentEntity entity = convertToAssignmentEntity(assignmentDTO);
			assignmentDAO.persist(entity);
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

		entity.setId(dto.id());
		entity.setType(dto.type());
		entity.setPublishingDate(Timestamp.valueOf(dto.publishingDate()));

		entity.setDeadline(Timestamp.valueOf(dto.deadline()));

		SubjectDAO subjectDAO = new SubjectDAO();
		TimetableDAO timeTableDAO = new TimetableDAO();

		SubjectEntity subjectEntity = subjectDAO.findByName(dto.subjectName());
		entity.setSubject(subjectEntity);
		entity.setTimetable(timeTableDAO.findById(dto.timetableId()));

		return entity;
	}

	private AssignmentDTO convertToAssignmentDTO(AssignmentEntity entity) {
		return new AssignmentDTO(entity.getId(),
		                         entity.getType(),
		                         entity.getPublishingDate().toLocalDateTime(),
		                         entity.getDeadline().toLocalDateTime(),
		                         entity.getName(),
		                         entity.getSubject().getName(),
		                         entity.getDescription(),
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
		return new TeachingSessionDTO(entity.getId(),
		                              entity.getStartDate().toLocalDateTime(),
		                              entity.getEndDate().toLocalDateTime(),
		                              entity.getLocation().getName(),
		                              entity.getSubject().getName(),
		                              entity.getDescription(),
		                              entity.getTimetable().getId());
	}
}
