package model;

import dao.SubjectDAO;
import dto.SubjectDTO;
import entity.SubjectEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Manages subject-related operations including creation, retrieval,
 * update, and deletion. Enforces access control based on user roles.
 */
public class SubjectModel {

	private static final SubjectDAO subjectDAO = new SubjectDAO();
	private static final UserModel userModel = new UserModel();

	/**
	 * Retrieves all subjects in the system.
	 *
	 * @return A list of all subject DTOs
	 */
	public List<SubjectDTO> fetchAllSubjects() {
		List<SubjectDTO> subjects = new ArrayList<>();
		List<SubjectEntity> subjectsEntities = subjectDAO.findAll();

		for (SubjectEntity subject : subjectsEntities) {
			subjects.add(convertToSubjectDTO(subject));
		}

		return subjects;
	}

	/**
	 * Retrieves all subjects associated with the current user.
	 *
	 * @return A list of subject DTOs for the current user
	 */
	public List<SubjectDTO> fetchSubjectsByUser() {
		List<SubjectDTO> subjectDTOs = new ArrayList<>();

		long userId = userModel.fetchCurrentUserId();

		List<SubjectEntity> subjects = subjectDAO.findAllByUserId(userId);

		for (SubjectEntity subject : subjects) {
			subjectDTOs.add(convertToSubjectDTO(subject));
		}

		return subjectDTOs;
	}

	/**
	 * Finds a subject by its code.
	 *
	 * @param subjectDTO The subject DTO containing the code to search for
	 * @return The found subject DTO
	 * @throws IllegalArgumentException if the subject is not found
	 */
	public SubjectDTO fetchSubjectByCode(SubjectDTO subjectDTO) {
		SubjectEntity subject = subjectDAO.findByCode(subjectDTO.code());

		if (subject == null) {
			throw new IllegalArgumentException("Subject not found");
		}

		return convertToSubjectDTO(subject);
	}

	/**
	 * Adds a new subject to the system.
	 *
	 * @param subjectDTO The subject information to add
	 * @throws IllegalArgumentException if the user is not a teacher
	 */
	public void addSubject(SubjectDTO subjectDTO) {
		if (!userModel.isCurrentUserTeacher()) {
			throw new IllegalArgumentException("Only teacher can add subject");
		}

		SubjectEntity subject = convertToSubjectEntity(subjectDTO);
		subjectDAO.persist(subject);
	}

	/**
	 * Updates an existing subject.
	 *
	 * @param subjectDTO  The updated subject information
	 * @param currentCode The current code of the subject to update
	 * @throws IllegalArgumentException if validation fails or the user lacks permission
	 */
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

	/**
	 * Deletes a subject from the system.
	 *
	 * @param subjectDTO The subject to delete
	 * @throws IllegalArgumentException if the user is not a teacher
	 */
	public void deleteSubject(SubjectDTO subjectDTO) {
		if (!userModel.isCurrentUserTeacher()) {
			throw new IllegalArgumentException("Only teacher can delete subject");
		}

		SubjectEntity subject = convertToSubjectEntity(subjectDTO);
		subjectDAO.delete(subject);
	}

	/**
	 * Deletes all subjects from the system.
	 */
	public void deleteAllSubjects() {
		subjectDAO.deleteAll();
	}

	/**
	 * Converts a SubjectEntity to a SubjectDTO.
	 *
	 * @param subject The entity to convert
	 * @return A SubjectDTO representation of the entity
	 */
	private SubjectDTO convertToSubjectDTO(SubjectEntity subject) {
		return new SubjectDTO(subject.getName(), subject.getCode());
	}

	/**
	 * Converts a SubjectDTO to a SubjectEntity.
	 *
	 * @param subjectDTO The DTO to convert
	 * @return A SubjectEntity representation of the DTO
	 */
	private SubjectEntity convertToSubjectEntity(SubjectDTO subjectDTO) {
		return new SubjectEntity(subjectDTO.name(), subjectDTO.code());
	}
}
