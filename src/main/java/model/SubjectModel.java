package model;

import dao.SubjectDAO;
import dto.SubjectDTO;
import entity.SubjectEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SubjectModel {

	private static final SubjectDAO subjectDAO = new SubjectDAO();
	private static final UserModel userModel = new UserModel();

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

		long userId = userModel.fetchCurrentUserId();

		List<SubjectEntity> subjects = subjectDAO.findAllByUserId(userId);

		for (SubjectEntity subject : subjects) {
			subjectDTOs.add(convertToSubjectDTO(subject));
		}

		return subjectDTOs;
	}

	public SubjectDTO fetchSubjectByCode(SubjectDTO subjectDTO) {
		SubjectEntity subject = subjectDAO.findByCode(subjectDTO.code());

		if (subject == null) {
			throw new IllegalArgumentException("Subject not found");
		}

		return convertToSubjectDTO(subject);
	}

	public void addSubject(SubjectDTO subjectDTO) {
		if (!userModel.isCurrentUserTeacher()) {
			throw new IllegalArgumentException("Only teacher can add subject");
		}

		SubjectEntity subject = convertToSubjectEntity(subjectDTO);
		subjectDAO.persist(subject);
	}

	public void updateSubject(SubjectDTO subjectDTO, String currentCode) {
		if (!userModel.isCurrentUserTeacher()) {
			throw new IllegalArgumentException("Only teacher can update subject");
		}

		if (Objects.equals(currentCode, subjectDTO.code())) {
			SubjectEntity existingSubject = subjectDAO.findByCode(subjectDTO.code());
			if (existingSubject != null) {
				throw new IllegalArgumentException("Subject already exists");
			}
		}

		SubjectEntity subject = subjectDAO.findByCode(currentCode);

		if (subject == null) {
			throw new IllegalArgumentException("Subject not found");
		}

		subject.setName(subjectDTO.name());
		subject.setCode(subjectDTO.code());

		subjectDAO.update(subject);
	}

	public void deleteSubject(SubjectDTO subjectDTO) {
		if (!userModel.isCurrentUserTeacher()) {
			throw new IllegalArgumentException("Only teacher can delete subject");
		}

		SubjectEntity subject = convertToSubjectEntity(subjectDTO);
		subjectDAO.delete(subject);
	}

	public void deleteAllSubjects() {
		subjectDAO.deleteAll();
	}

	private SubjectDTO convertToSubjectDTO(SubjectEntity subject) {
		return new SubjectDTO(subject.getName(), subject.getCode());
	}

	private SubjectEntity convertToSubjectEntity(SubjectDTO subjectDTO) {
		return new SubjectEntity(subjectDTO.name(), subjectDTO.code());
	}

}
