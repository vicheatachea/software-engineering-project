package model;

import dao.TimetableDAO;

public class TimetableModel {

	private static final TimetableDAO timetableDAO = new TimetableDAO();

	public Long fetchTimetableForUser() {
		return timetableDAO.findByUserId(UserPreferences.getUserId()).getId();
	}

	public Long fetchTimetableForGroup(String groupName) {
		return timetableDAO.findByGroupName(groupName).getId();
	}
}
