package controller;

import dto.GroupDTO;
import model.GroupModel;

import java.util.List;

public class GroupController {
	private final GroupModel groupModel = new GroupModel();

	// Fetch all available groups
	public List<GroupDTO> fetchAllGroups() {
		return groupModel.fetchAllGroups();
	}

	// Fetch all groups for a user
	public List<GroupDTO> fetchGroupsByUser(GroupDTO groupDTO, long userId) {
		return groupModel.fetchGroupsByUser(groupDTO, userId);
	}

	// Check if the logged-in user is the owner of a group
	public boolean isUserGroupOwner(GroupDTO groupDTO) {
		// TODO: Implement this method
		return false;
	}

	// Add a group
	public void addGroup(GroupDTO groupDTO) {
		groupModel.addGroup(groupDTO);
	}

	// Update a group
	public void updateGroup(GroupDTO groupDTO) {
		groupModel.updateGroup(groupDTO);
	}

	// Delete a group
	public void deleteGroup(GroupDTO groupDTO) {
		groupModel.deleteGroup(groupDTO);
	}

	// Add a student to a group
	public void addStudentToGroup(GroupDTO groupDTO, long studentId) {
		groupModel.addStudentToGroup(groupDTO, studentId);
	}

	// Remove a student from a group
	public void removeStudentFromGroup(GroupDTO groupDTO, long studentId) {
		groupModel.removeStudentFromGroup(groupDTO, studentId);
	}
}
