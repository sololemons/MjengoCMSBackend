package com.siteoperationsservice.utilis;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.lang.GeoLocation;
import com.siteoperationsservice.entities.ConstructionProject;
import com.siteoperationsservice.repositories.ConstructionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationExtractor {

    private static final double EARTH_RADIUS_METERS = 6371000;
    private final ConstructionRepository projectRepository;

    public record GeoCoordinates(double latitude, double longitude) {
    }

    public GeoCoordinates extractGPS(InputStream imageInputStream) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(imageInputStream);
            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);

            if (gpsDirectory != null && gpsDirectory.getGeoLocation() != null) {
                GeoLocation geoLocation = gpsDirectory.getGeoLocation();

                double lat = geoLocation.getLatitude();
                double lon = geoLocation.getLongitude();

                log.info("Successfully extracted coordinates from image: Lat {}, Lon {}", lat, lon);
                return new GeoCoordinates(lat, lon);
            } else {
                log.warn("No embedded GPS information found in image metadata.");
            }
        } catch (ImageProcessingException | IOException e) {
            log.error("Failed to parse image EXIF metadata", e);
        } catch (Exception e) {
            log.error("Unexpected error while extracting GPS data", e);
        }

        return null;
    }

    public boolean verifyGeofence(String projectId, double photoLat, double photoLon) {
        ConstructionProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        double siteLat = project.getLatitude();
        double siteLon = project.getLongitude();
        double allowedRadius = project.getAllowedRadiusMeters() != null ? project.getAllowedRadiusMeters() : 100.0;

        double latDistance = Math.toRadians(photoLat - siteLat);
        double lonDistance = Math.toRadians(photoLon - siteLon);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(siteLat)) * Math.cos(Math.toRadians(photoLat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanceInMeters = EARTH_RADIUS_METERS * c;

        log.info("Geofence Check - Project: {}, Distance calculated: {} meters. Allowed limit: {} meters.",
                project.getConstructionName(), Math.round(distanceInMeters), allowedRadius);

        return distanceInMeters <= allowedRadius;
    }
}