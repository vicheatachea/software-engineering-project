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
import java.util.Objects;

public class GroupModel {

	private static final UserGroupDAO userGroupDAO = new UserGroupDAO();
	private static final UserDAO userDAO = new UserDAO();
	private static final TimetableDAO timetableDAO = new TimetableDAO();
	private static final SubjectDAO subjectDAO = new SubjectDAO();

	private static final UserModel userModel = new UserModel();

	// Fetch all available groups
	public List<GroupDTO> fetchAllGroups() {

		List<UserGroupEntity> groups = userGroupDAO.findAll();
		List<GroupDTO> groupDTOs = new ArrayList<>();

		for (UserGroupEntity group : groups) {
			groupDTOs.add(
					new GroupDTO(group.getName(), group.getCode(), group.getCapacity(), group.getTeacher().getId(),
					             group.getSubject().getCode()));
		}

		return groupDTOs;
	}

	// Fetch all groups for a user
	public List<GroupDTO> fetchGroupsByUser() {
		long userId = userModel.fetchCurrentUserId();

		List<UserGroupEntity> groups = userGroupDAO.findAllByUserId(userId);
		List<GroupDTO> groupDTOs = new ArrayList<>();

		for (UserGroupEntity group : groups) {
			groupDTOs.add(convertToGroupDTO(group));
		}

		return groupDTOs;
	}

	// Fetch a group DTO by the group name
	public GroupDTO fetchGroupByName(String groupName) {
		UserGroupEntity group = userGroupDAO.findByName(groupName);

		if (group == null) {
			throw new IllegalArgumentException("Group does not exist.");
		}

		return convertToGroupDTO(group);
	}

	public GroupDTO fetchGroupByTimetableId(long timetableId) {
		UserGroupEntity group = userGroupDAO.findByTimetableId(timetableId);

		if (group == null) {
			throw new IllegalArgumentException("Group does not exist.");
		}

		return convertToGroupDTO(group);
	}

	public void addGroup(GroupDTO groupDTO) {
		if (!userModel.isCurrentUserTeacher()) {
			throw new IllegalArgumentException("Only teacher can add group");
		}

		UserEntity teacher = userDAO.findTeacherById(groupDTO.teacherId());

		if (teacher == null) {
			throw new IllegalArgumentException("Teacher does not exist.");
		}

		SubjectEntity subject = subjectDAO.findByCode(groupDTO.subjectCode());

		if (subject == null) {
			throw new IllegalArgumentException("Subject does not exist.");
		}

		TimetableEntity timetable = new TimetableEntity();

		timetableDAO.persist(timetable);

		UserGroupEntity group =
				new UserGroupEntity(groupDTO.name(), groupDTO.code(), groupDTO.capacity(), teacher, new HashSet<>(),
				                    subject, timetable);

		userGroupDAO.persist(group);
	}

	// Updates group details excluding the students and timetable
	public void updateGroup(GroupDTO groupDTO, String currentName) {

		if (!Objects.equals(currentName, groupDTO.name())) {
			UserGroupEntity existingGroup = userGroupDAO.findByName(groupDTO.name());
			if (existingGroup != null) {
				throw new IllegalArgumentException("Group already exists.");
			}
		}

		if (!userModel.isCurrentUserTeacher()) {
			throw new IllegalArgumentException("Only teachers can update groups");
		}

		UserGroupEntity existingGroup = userGroupDAO.findByName(currentName);

		if (existingGroup == null) {
			throw new IllegalArgumentException("Group does not exist.");
		}

		if (groupDTO.capacity() < existingGroup.getStudents().size()) {
			throw new IllegalArgumentException(
					"Group capacity cannot be less than the number of students in the group.");
		}

		if (groupDTO.capacity() < 1) {
			throw new IllegalArgumentException("Group capacity cannot be less than 1.");
		}

		if (groupDTO.name().isEmpty() || groupDTO.code().isEmpty()) {
			throw new IllegalArgumentException("Group name and code cannot be empty.");
		}

		existingGroup.setName(groupDTO.name());
		existingGroup.setCode(groupDTO.code());
		existingGroup.setCapacity(groupDTO.capacity());
		existingGroup.setTeacher(userDAO.findTeacherById(groupDTO.teacherId()));
		existingGroup.setSubject(subjectDAO.findByCode(groupDTO.subjectCode()));
		existingGroup.setTimetable(existingGroup.getTimetable());

		userGroupDAO.persist(existingGroup);
	}

	// Adds a student to a group
	public void addStudentToGroup(GroupDTO groupDTO, String studentUsername) {
		if (!userModel.isCurrentUserTeacher()) {
			throw new IllegalArgumentException("Only teachers can add student to group");
		}

		UserGroupEntity existingGroup = userGroupDAO.findByName(groupDTO.name());

		if (existingGroup == null) {
			throw new IllegalArgumentException("Group does not exist.");
		}

		if (existingGroup.getStudents().size() >= existingGroup.getCapacity()) {
			throw new IllegalArgumentException("Group is full.");
		}

		UserEntity student = userDAO.findByUsername(studentUsername);

		if (student == null) {
			throw new IllegalArgumentException("Student does not exist.");
		}

		existingGroup.setName(existingGroup.getName());
		existingGroup.setCode(existingGroup.getCode());
		existingGroup.setCapacity(existingGroup.getCapacity());
		existingGroup.setTeacher(existingGroup.getTeacher());
		existingGroup.getStudents().add(student);
		existingGroup.setTimetable(existingGroup.getTimetable());
		userGroupDAO.persist(existingGroup);

	}

	public void removeStudentFromGroup(GroupDTO groupDTO, String studentUsername) {
		if (!userModel.isCurrentUserTeacher()) {
			throw new IllegalArgumentException("Only teachers can remove student from group");
		}

		UserGroupEntity existingGroup = userGroupDAO.findByName(groupDTO.name());

		if (existingGroup == null) {
			throw new IllegalArgumentException("Group does not exist.");
		}

		UserEntity user = userDAO.findByUsername(studentUsername);

		if (user == null) {
			throw new IllegalArgumentException("Student does not exist.");
		}

		existingGroup.getStudents().remove(user);
		userGroupDAO.persist(existingGroup);

	}

	public void deleteGroup(GroupDTO groupDTO) {
		if (!userModel.isCurrentUserTeacher()) {
			throw new IllegalArgumentException("Only teachers can delete groups");
		}

		UserGroupEntity existingGroup = userGroupDAO.findByName(groupDTO.name());

		if (existingGroup == null) {
			throw new IllegalArgumentException("Group does not exist.");
		}

		userGroupDAO.delete(existingGroup);
		timetableDAO.delete(existingGroup.getTimetable());
	}

	public boolean isUserGroupOwner(String groupName) {
		UserGroupEntity group = userGroupDAO.findByName(groupName);

		if (!userModel.isCurrentUserTeacher()) {
			return false;
		}

		if (group == null) {
			throw new IllegalArgumentException("Group does not exist.");
		}

		return Objects.equals(group.getTeacher().getId(), userModel.fetchCurrentUserId());
	}

	private GroupDTO convertToGroupDTO(UserGroupEntity group) {
		return new GroupDTO(group.getName(), group.getCode(), group.getCapacity(), group.getTeacher().getId(),
		                    group.getSubject().getCode());
	}
}
