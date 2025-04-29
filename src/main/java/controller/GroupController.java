package controller;

import dto.GroupDTO;
import dto.UserDTO;
import java.util.List;
import java.util.Set;
import model.GroupModel;


public class GroupController {
	private final GroupModel groupModel = new GroupModel();

	// Fetch all available groups
	public List<GroupDTO> fetchAllGroups() {
		return groupModel.fetchAllGroups();
	}

	// Fetch all groups for a user
	public List<GroupDTO> fetchGroupsByUser() {
		return groupModel.fetchGroupsByUser();
	}

	// Fetch a group DTO by the group name
	public GroupDTO fetchGroupByName(String groupName) {
		return groupModel.fetchGroupByName(groupName);
	}

	// Fetch a group DTO by the timetable ID
	public GroupDTO fetchGroupByTimetableId(long timetableId) {
		return groupModel.fetchGroupByTimetableId(timetableId);
	}

	// Check if the logged-in user is the owner of a group
	public boolean isUserGroupOwner(String groupName) {
		return groupModel.isUserGroupOwner(groupName);
	}

	// Add a group
	public void addGroup(GroupDTO groupDTO) {
		groupModel.addGroup(groupDTO);
	}

	// Update a group
	public void updateGroup(GroupDTO groupDTO, String currentName) {
		groupModel.updateGroup(groupDTO, currentName);
	}

	// Delete a group
	public void deleteGroup(GroupDTO groupDTO) {
		groupModel.deleteGroup(groupDTO);
	}

	// Add a student to a group
	public void addStudentToGroup(GroupDTO groupDTO, String studentUsername) {
		groupModel.addStudentToGroup(groupDTO, studentUsername);
	}

	// Remove a student from a group
	public void removeStudentFromGroup(GroupDTO groupDTO, String studentUsername) {
		groupModel.removeStudentFromGroup(groupDTO, studentUsername);
	}
}
