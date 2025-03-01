package model;

import dao.TimetableDAO;
import dao.UserDAO;
import dao.UserGroupDAO;
import dto.GroupDTO;
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

	// Fetch all available groups
	public List<GroupDTO> fetchAllGroups() {

		List<UserGroupEntity> groups = userGroupDAO.findAll();
		List<GroupDTO> groupDTOs = new ArrayList<>();

		for (UserGroupEntity group : groups) {
			groupDTOs.add(
					new GroupDTO(group.getName(), group.getCode(), group.getCapacity(), group.getTeacher().getId()));
		}

		return groupDTOs;
	}

	// Fetch all groups for a user
	public List<GroupDTO> fetchGroupsByUser(GroupDTO groupDTO) {

		List<UserGroupEntity> groups = userGroupDAO.findAllByUserId(groupDTO.userId());
		List<GroupDTO> groupDTOs = new ArrayList<>();

		for (UserGroupEntity group : groups) {
			groupDTOs.add(ConvertToGroupDTO(group));
		}

		return groupDTOs;
	}

	public void addGroup(GroupDTO groupDTO) {

		UserEntity teacher = userDAO.findTeacherById(groupDTO.userId());

		TimetableEntity timetable = new TimetableEntity();

		timetableDAO.persist(timetable);

		UserGroupEntity group = new UserGroupEntity(groupDTO.name(),
		                                            groupDTO.code(),
		                                            groupDTO.capacity(),
		                                            teacher,
		                                            new HashSet<>(),
		                                            timetable);

		userGroupDAO.persist(group);
	}

	public void addGroupForUser(GroupDTO groupDTO) {

		UserGroupEntity existingGroup = userGroupDAO.findByName(groupDTO.name());

		UserEntity user = userDAO.findStudentById(groupDTO.userId());

		if (existingGroup == null) {
			throw new IllegalArgumentException("Group does not exist.");
		} else {
			existingGroup.setName(existingGroup.getName());
			existingGroup.setCode(existingGroup.getCode());
			existingGroup.setCapacity(existingGroup.getCapacity());
			existingGroup.setTeacher(existingGroup.getTeacher());
			existingGroup.getStudents().add(user);
			existingGroup.setTimetable(existingGroup.getTimetable());
			userGroupDAO.persist(existingGroup);
		}
	}

	public void updateGroupForUser(GroupDTO groupDTO) {
		// Placeholder
	}

	public void deleteGroupForUser(GroupDTO groupDTO) {
		// Placeholder
	}

	private GroupDTO ConvertToGroupDTO(UserGroupEntity group) {
		return new GroupDTO(group.getName(),
		                    group.getCode(),
		                    group.getCapacity(),
		                    group.getTeacher().getId());
	}

}
