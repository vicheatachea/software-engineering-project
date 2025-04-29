package controller;

import dto.SubjectDTO;
import java.util.List;
import model.SubjectModel;

public class SubjectController {
	private final SubjectModel subjectModel = new SubjectModel();

	// Fetch all available subjects
	public List<SubjectDTO> fetchAllSubjects() {
		return subjectModel.fetchAllSubjects();
	}

	public List<SubjectDTO> fetchSubjectsByUser() {
		return subjectModel.fetchSubjectsByUser();
	}

	public SubjectDTO fetchSubjectByCode(SubjectDTO subjectDTO) {
		return subjectModel.fetchSubjectByCode(subjectDTO);
	}

	// Adds a subject
	public void addSubject(SubjectDTO subjectDTO) {
		subjectModel.addSubject(subjectDTO);
	}

	public void updateSubject(SubjectDTO subjectDTO, String currentCode) {
		subjectModel.updateSubject(subjectDTO, currentCode);
	}

	// Deletes a subject
	public void deleteSubject(SubjectDTO subjectDTO) {
		subjectModel.deleteSubject(subjectDTO);
	}

	public void deleteAllSubjects() {
		subjectModel.deleteAllSubjects();
	}
}
