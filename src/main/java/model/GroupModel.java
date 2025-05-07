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

/**
 * Manages operations related to user groups including creation, modification,
 * and student enrollment. Enforces business rules for group management.
 */
public class GroupModel {

	private static final UserGroupDAO userGroupDAO = new UserGroupDAO();
	private static final UserDAO userDAO = new UserDAO();
	private static final TimetableDAO timetableDAO = new TimetableDAO();
	private static final SubjectDAO subjectDAO = new SubjectDAO();

	private static final UserModel userModel = new UserModel();

	/**
	 * Retrieves all available groups in the system.
	 *
	 * @return A list of GroupDTOs for all groups
	 */
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

	/**
	 * Retrieves all groups associated with the current user.
	 *
	 * @return A list of GroupDTOs for the current user
	 */
	public List<GroupDTO> fetchGroupsByUser() {
		long userId = userModel.fetchCurrentUserId();

		List<UserGroupEntity> groups = userGroupDAO.findAllByUserId(userId);
		List<GroupDTO> groupDTOs = new ArrayList<>();

		for (UserGroupEntity group : groups) {
			groupDTOs.add(convertToGroupDTO(group));
		}

		return groupDTOs;
	}

	/**
	 * Finds a group by its name.
	 *
	 * @param groupName The name of the group to find
	 * @return A GroupDTO representing the found group
	 * @throws IllegalArgumentException if the group does not exist
	 */
	public GroupDTO fetchGroupByName(String groupName) {
		UserGroupEntity group = userGroupDAO.findByName(groupName);

		if (group == null) {
			throw new IllegalArgumentException("Group does not exist.");
		}

		return convertToGroupDTO(group);
	}

	/**
	 * Finds a group by its timetable ID.
	 *
	 * @param timetableId The timetable ID to search for
	 * @return A GroupDTO representing the found group
	 * @throws IllegalArgumentException if the group does not exist
	 */
	public GroupDTO fetchGroupByTimetableId(long timetableId) {
		UserGroupEntity group = userGroupDAO.findByTimetableId(timetableId);

		if (group == null) {
			throw new IllegalArgumentException("Group does not exist.");
		}

		return convertToGroupDTO(group);
	}

	/**
	 * Creates a new group in the system.
	 *
	 * @param groupDTO The group information to add
	 * @throws IllegalArgumentException if the user is not a teacher or required entities don't exist
	 */
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

	/**
	 * Updates an existing group's details except students and timetable.
	 *
	 * @param groupDTO    The updated group information
	 * @param currentName The current name of the group to update
	 * @throws IllegalArgumentException if validation fails or the user lacks permission
	 */
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

	/**
	 * Adds a student to a group.
	 *
	 * @param groupDTO        The group to add the student to
	 * @param studentUsername The username of the student to add
	 * @throws IllegalArgumentException if validation fails or the user lacks permission
	 */
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

	/**
	 * Removes a student from a group.
	 *
	 * @param groupDTO        The group to remove the student from
	 * @param studentUsername The username of the student to remove
	 * @throws IllegalArgumentException if validation fails or the user lacks permission
	 */
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

	/**
	 * Deletes a group and its associated timetable.
	 *
	 * @param groupDTO The group to delete
	 * @throws IllegalArgumentException if validation fails or the user lacks permission
	 */
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

	/**
	 * Checks if the current user is the owner (teacher) of a group.
	 *
	 * @param groupName The name of the group to check
	 * @return true if the current user is the owner, false otherwise
	 * @throws IllegalArgumentException if the group does not exist
	 */
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

	/**
	 * Converts a UserGroupEntity to a GroupDTO.
	 *
	 * @param group The entity to convert
	 * @return A GroupDTO representation of the entity
	 */
	private GroupDTO convertToGroupDTO(UserGroupEntity group) {
		return new GroupDTO(group.getName(), group.getCode(), group.getCapacity(), group.getTeacher().getId(),
		                    group.getSubject().getCode());
	}
}
