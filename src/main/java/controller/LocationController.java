package controller;

import dto.LocationDTO;
import model.LocationModel;

import java.util.ArrayList;
import java.util.List;

public class LocationController {
    private final LocationModel locationModel = new LocationModel();

    // Fetch all available locations
    public List<LocationDTO> fetchAllLocations() {
        // Placeholder
        return new ArrayList<>();
    }
}
