package model;

import dao.SubjectDAO;
import dao.TimetableDAO;
import dao.UserDAO;
import dao.UserGroupDAO;
import dto.GroupDTO;
import entity.SubjectEntity;
import entity.TimetableEntity;
import entity.UserEntity;
import entity.UserGroupEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GroupModel {

	private static final UserGroupDAO userGroupDAO = new UserGroupDAO();
	private static final UserDAO userDAO = new UserDAO();
	private static final TimetableDAO timetableDAO = new TimetableDAO();
	private static final SubjectDAO subjectDAO = new SubjectDAO();

	// Fetch all available groups
	public List<GroupDTO> fetchAllGroups() {

		List<UserGroupEntity> groups = userGroupDAO.findAll();
		List<GroupDTO> groupDTOs = new ArrayList<>();

		for (UserGroupEntity group : groups) {
			groupDTOs.add(
					new GroupDTO(group.getName(), group.getCode(), group.getCapacity(), group.getTeacher().getId(),
					             group.getSubject().getName()));
		}

		return groupDTOs;
	}

	// Fetch all groups for a user
	public List<GroupDTO> fetchGroupsByUser(GroupDTO groupDTO, long userId) {

		List<UserGroupEntity> groups = userGroupDAO.findAllByUserId(userId);
		List<GroupDTO> groupDTOs = new ArrayList<>();

		for (UserGroupEntity group : groups) {
			groupDTOs.add(ConvertToGroupDTO(group));
		}

		return groupDTOs;
	}

	public void addGroup(GroupDTO groupDTO) {

		UserEntity teacher = userDAO.findTeacherById(groupDTO.teacherId());

		SubjectEntity subject = subjectDAO.findByName(groupDTO.subjectName());

		TimetableEntity timetable = new TimetableEntity();

		timetableDAO.persist(timetable);

		UserGroupEntity group =
				new UserGroupEntity(groupDTO.name(), groupDTO.code(), groupDTO.capacity(), teacher, new HashSet<>(),
				                    subject, timetable);

		userGroupDAO.persist(group);
	}

	// Updates group details excluding the students and timetable
	public void updateGroup(GroupDTO groupDTO) {

		UserGroupEntity existingGroup = userGroupDAO.findByName(groupDTO.name());

		if (existingGroup == null) {
			throw new IllegalArgumentException("Group does not exist.");
		} else {
			existingGroup.setName(groupDTO.name());
			existingGroup.setCode(groupDTO.code());
			existingGroup.setCapacity(groupDTO.capacity());
			existingGroup.setTeacher(userDAO.findTeacherById(groupDTO.teacherId()));
			userGroupDAO.persist(existingGroup);
		}
	}

	// Adds a student to a group
	public void addStudentToGroup(GroupDTO groupDTO, long studentId) {

		UserGroupEntity existingGroup = userGroupDAO.findByName(groupDTO.name());

		UserEntity student = userDAO.findStudentById(studentId);

		if (existingGroup == null) {
			throw new IllegalArgumentException("Group does not exist.");
		} else {
			existingGroup.setName(existingGroup.getName());
			existingGroup.setCode(existingGroup.getCode());
			existingGroup.setCapacity(existingGroup.getCapacity());
			existingGroup.setTeacher(existingGroup.getTeacher());
			existingGroup.getStudents().add(student);
			existingGroup.setTimetable(existingGroup.getTimetable());
			userGroupDAO.persist(existingGroup);
		}
	}

	public void removeStudentFromGroup(GroupDTO groupDTO, long studentId) {

		UserGroupEntity existingGroup = userGroupDAO.findByName(groupDTO.name());

		UserEntity user = userDAO.findStudentById(studentId);

		if (existingGroup == null) {
			throw new IllegalArgumentException("Group does not exist.");
		} else {
			existingGroup.getStudents().remove(user);
			userGroupDAO.persist(existingGroup);
		}
	}

	public void deleteGroup(GroupDTO groupDTO) {

		UserGroupEntity existingGroup = userGroupDAO.findByName(groupDTO.name());

		if (existingGroup == null) {
			throw new IllegalArgumentException("Group does not exist.");
		}

		userGroupDAO.delete(existingGroup);
		timetableDAO.delete(existingGroup.getTimetable());
	}

	private GroupDTO ConvertToGroupDTO(UserGroupEntity group) {
		return new GroupDTO(group.getName(), group.getCode(), group.getCapacity(), group.getTeacher().getId(),
		                    group.getSubject().getName());
	}
}
