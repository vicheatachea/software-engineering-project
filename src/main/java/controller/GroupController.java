package controller;

import dto.GroupDTO;
import model.GroupModel;

import java.util.ArrayList;
import java.util.List;

public class GroupController {
    private final GroupModel groupModel = new GroupModel();

    // Fetch all available groups
    public List<GroupDTO> fetchAllGroups() {
        // Placeholder
        return new ArrayList<>();
    }

    // Fetch all groups for a user
    public List<GroupDTO> fetchGroupsByUser() {
        // Placeholder
        return new ArrayList<>();
    }
}
