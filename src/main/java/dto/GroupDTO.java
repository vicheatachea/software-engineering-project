package dto;

/**
 * Data Transfer Object (DTO) representing a student group.
 * Contains group identification and capacity information, along with references to
 * associated teacher and subject.
 */
public record GroupDTO(String name, String code, int capacity, Long teacherId, String subjectCode) {
}
