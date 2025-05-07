package controller;

import dto.GroupDTO;
import model.GroupModel;

import java.util.List;

/**
 * Controller class for managing student groups in the system.
 * Provides methods for group creation, retrieval, modification, and student management
 * by interfacing with the GroupModel layer.
 */
public class GroupController {
	private final GroupModel groupModel = new GroupModel();

	/**
	 * Retrieves all available groups in the system.
	 *
	 * @return A list of all group DTOs
	 */
	public List<GroupDTO> fetchAllGroups() {
		return groupModel.fetchAllGroups();
	}

	/**
	 * Fetches all groups associated with the currently logged-in user.
	 *
	 * @return A list of group DTOs for the current user
	 */
	public List<GroupDTO> fetchGroupsByUser() {
		return groupModel.fetchGroupsByUser();
	}

	/**
	 * Retrieves a specific group by its name.
	 *
	 * @param groupName The name of the group to fetch
	 * @return The group DTO if found, null otherwise
	 */
	public GroupDTO fetchGroupByName(String groupName) {
		return groupModel.fetchGroupByName(groupName);
	}

	/**
	 * Retrieves a group associated with a specific timetable.
	 *
	 * @param timetableId The ID of the timetable to find the group for
	 * @return The group DTO if found, null otherwise
	 */
	public GroupDTO fetchGroupByTimetableId(long timetableId) {
		return groupModel.fetchGroupByTimetableId(timetableId);
	}

	/**
	 * Determines if the currently logged-in user is the owner of a specific group.
	 *
	 * @param groupName The name of the group to check ownership for
	 * @return true if the current user owns the group, false otherwise
	 */
	public boolean isUserGroupOwner(String groupName) {
		return groupModel.isUserGroupOwner(groupName);
	}

	/**
	 * Creates a new group in the system.
	 *
	 * @param groupDTO The group data to add
	 */
	public void addGroup(GroupDTO groupDTO) {
		groupModel.addGroup(groupDTO);
	}

	/**
	 * Updates an existing group's information.
	 *
	 * @param groupDTO    The updated group data
	 * @param currentName The current name of the group being updated
	 */
	public void updateGroup(GroupDTO groupDTO, String currentName) {
		groupModel.updateGroup(groupDTO, currentName);
	}

	/**
	 * Removes a group from the system.
	 *
	 * @param groupDTO The group to delete
	 */
	public void deleteGroup(GroupDTO groupDTO) {
		groupModel.deleteGroup(groupDTO);
	}

	/**
	 * Adds a student to an existing group.
	 *
	 * @param groupDTO        The group to add the student to
	 * @param studentUsername The username of the student to add
	 */
	public void addStudentToGroup(GroupDTO groupDTO, String studentUsername) {
		groupModel.addStudentToGroup(groupDTO, studentUsername);
	}

	/**
	 * Removes a student from a group.
	 *
	 * @param groupDTO        The group to remove the student from
	 * @param studentUsername The username of the student to remove
	 */
	public void removeStudentFromGroup(GroupDTO groupDTO, String studentUsername) {
		groupModel.removeStudentFromGroup(groupDTO, studentUsername);
	}
}
