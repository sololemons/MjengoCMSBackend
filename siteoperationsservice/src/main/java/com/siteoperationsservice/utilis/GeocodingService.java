package com.siteoperationsservice.utilis;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Locale;

@Slf4j
@Service
public class GeocodingService {

  private final RestTemplate restTemplate = new RestTemplate();

  public String getLocationName(Double lat, Double lon) {
    if (lat == null || lon == null) {
      return "Unknown Location";
    }

    String url = String.format(Locale.US,
        "https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f&zoom=14",
        lat, lon);

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set("User-Agent", "SiteOperationsApp/1.0");
      HttpEntity<String> entity = new HttpEntity<>(headers);

      ResponseEntity<JsonNode> response = restTemplate.exchange(
          url, HttpMethod.GET, entity, JsonNode.class);

      JsonNode root = response.getBody();
      if (root != null && root.has("address")) {
        JsonNode address = root.get("address");

        String suburb = address.has("suburb") ? address.get("suburb").asText() : "";
        String city = address.has("city") ? address.get("city").asText() :
            (address.has("town") ? address.get("town").asText() :
                (address.has("county") ? address.get("county").asText() : ""));

        if (!suburb.isEmpty() && !city.isEmpty()) {
          return suburb + ", " + city;
        } else if (!city.isEmpty()) {
          return city;
        } else if (root.has("display_name")) {
          return root.get("display_name").asText().split(",")[0];
        }
      }
    } catch (Exception e) {
      log.error("Failed to geocode coordinates: {}, {}", lat, lon, e);
    }

    return lat + ", " + lon;
  }
}