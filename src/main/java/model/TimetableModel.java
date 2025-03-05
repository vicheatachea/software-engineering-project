package model;

import dao.TimetableDAO;

public class TimetableModel {

	private static final TimetableDAO timetableDAO = new TimetableDAO();

	public Long fetchTimetableForUser() {
		long userId = UserPreferences.getUserId();

		if (userId == -1) {
			throw new IllegalArgumentException("User not found");
		}

		Long timetableId = timetableDAO.findByUserId(userId).getId();

		if (timetableId == null) {
			throw new IllegalArgumentException("Timetable not found");
		}

		return timetableId;
	}

	public Long fetchTimetableForGroup(String groupName) {
		Long timetableId = timetableDAO.findByGroupName(groupName).getId();

		if (timetableId == null) {
			throw new IllegalArgumentException("Timetable not found");
		}

		return timetableId;
	}

	public void deleteAllTimetables() {
		timetableDAO.deleteAll();
	}
}
