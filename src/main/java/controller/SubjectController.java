package controller;

import dto.SubjectDTO;
import model.SubjectModel;

import java.util.List;

/**
 * Controller class for managing subjects in the system.
 * Handles operations related to fetching, adding, updating, and deleting subjects
 * by interfacing with the SubjectModel layer.
 */
public class SubjectController {
	private final SubjectModel subjectModel = new SubjectModel();

	/**
	 * Retrieves all available subjects from the system.
	 *
	 * @return A list of all subject DTOs
	 */
	public List<SubjectDTO> fetchAllSubjects() {
		return subjectModel.fetchAllSubjects();
	}

	/**
	 * Fetches subjects associated with the currently logged-in user.
	 *
	 * @return A list of subject DTOs for the current user
	 */
	public List<SubjectDTO> fetchSubjectsByUser() {
		return subjectModel.fetchSubjectsByUser();
	}

	/**
	 * Retrieves a specific subject by its code.
	 *
	 * @param subjectDTO The subject DTO containing the code to search for
	 * @return The found subject DTO or null if not found
	 */
	public SubjectDTO fetchSubjectByCode(SubjectDTO subjectDTO) {
		return subjectModel.fetchSubjectByCode(subjectDTO);
	}

	/**
	 * Adds a new subject to the system.
	 *
	 * @param subjectDTO The subject data to be added
	 */
	public void addSubject(SubjectDTO subjectDTO) {
		subjectModel.addSubject(subjectDTO);
	}

	/**
	 * Updates an existing subject in the system.
	 *
	 * @param subjectDTO  The updated subject data
	 * @param currentCode The current code of the subject to be updated
	 */
	public void updateSubject(SubjectDTO subjectDTO, String currentCode) {
		subjectModel.updateSubject(subjectDTO, currentCode);
	}

	/**
	 * Removes a subject from the system.
	 *
	 * @param subjectDTO The subject to be deleted
	 */
	public void deleteSubject(SubjectDTO subjectDTO) {
		subjectModel.deleteSubject(subjectDTO);
	}

	/**
	 * Deletes all subjects from the system.
	 * This is a maintenance operation and should be used with caution.
	 */
	public void deleteAllSubjects() {
		subjectModel.deleteAllSubjects();
	}
}
