package controller;

import dto.SubjectDTO;
import model.SubjectModel;

import java.util.ArrayList;
import java.util.List;

public class SubjectController {
    private final SubjectModel subjectModel = new SubjectModel();

    // Fetch all available subjects
    public List<SubjectDTO> fetchAllSubjects() {
        return subjectModel.fetchAllSubjects();
    }

    public List<SubjectDTO> fetchSubjectsByUser() {
        return subjectModel.fetchSubjectsByUser();
    }

    // Adds/Updates a subject
    public void saveSubject(SubjectDTO subjectDTO) {
        subjectModel.saveSubject(subjectDTO);
    }

    // Deletes a subject
    public void deleteSubject(SubjectDTO subjectDTO) {
        subjectModel.deleteSubject(subjectDTO);
    }
}
