package model;

import dao.SubjectDAO;
import dto.SubjectDTO;
import entity.SubjectEntity;

import java.util.ArrayList;
import java.util.List;

public class SubjectModel {

	private static final SubjectDAO subjectDAO = new SubjectDAO();

	public List<SubjectDTO> fetchAllSubjects() {
		List<SubjectDTO> subjects = new ArrayList<>();
		List<SubjectEntity> subjectsEntities = subjectDAO.findAll();

		for (SubjectEntity subject : subjectsEntities) {
			subjects.add(convertToSubjectDTO(subject));
		}

		return subjects;
	}

	public List<SubjectDTO> fetchSubjectsByUser() {
		List<SubjectDTO> subjectDTOs = new ArrayList<>();
		List<SubjectEntity> subjects = subjectDAO.findAllByUserId(UserPreferences.getUserId());

		for (SubjectEntity subject : subjects) {
			subjectDTOs.add(convertToSubjectDTO(subject));
		}

		return subjectDTOs;
	}

	public void saveSubject(SubjectDTO subjectDTO) {
		SubjectEntity subject = convertToSubjectEntity(subjectDTO);
		subjectDAO.persist(subject);
	}

	public void deleteSubject(SubjectDTO subjectDTO) {
		SubjectEntity subject = convertToSubjectEntity(subjectDTO);
		subjectDAO.delete(subject);
	}

	private SubjectDTO convertToSubjectDTO(SubjectEntity subject) {
		return new SubjectDTO(subject.getName(), subject.getCode());
	}

	private SubjectEntity convertToSubjectEntity(SubjectDTO subjectDTO) {
		return new SubjectEntity(subjectDTO.name(), subjectDTO.code());
	}

}
