package dto;

/**
 * Data Transfer Object (DTO) representing a physical location.
 * Contains information about a location's name, campus, and building.
 */
public record LocationDTO(String name, String campus, String building) {
}
