package com.artsolo.bookswap.location;

import com.artsolo.bookswap.location.dto.CityResponse;
import com.artsolo.bookswap.location.dto.CountryResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class LocationService {

    @Value("{api.location.key}")
    private String apiKey;

    private final String baseUrl = "https://api.countrystatecity.in/v1/countries";
    private final HttpHeaders headers = new HttpHeaders();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    public LocationService() {
        this.headers.setContentType(MediaType.APPLICATION_JSON);
        this.headers.set("X-CSCAPI-KEY", apiKey);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public ResponseEntity<String> getAllCountries() {
        try {
            RequestEntity<Void> requestEntity = RequestEntity
                    .get(new URI(baseUrl))
                    .headers(headers)
                    .build();

            return restTemplate.exchange(requestEntity, String.class);
        } catch (URISyntaxException e) {
            log.error("Error creating country URI: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating URI");
        }
    }

    public List<CountryResponse> getCountriesResponse(ResponseEntity<String> responseEntity) {
        try {
            return objectMapper.readValue(responseEntity.getBody(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON countries response: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public ResponseEntity<String> getCitiesByCountry(String iso2) {
        try {
            RequestEntity<Void> requestEntity = RequestEntity
                    .get(new URI(baseUrl + "/" + iso2 + "/cities"))
                    .headers(headers)
                    .build();

            return restTemplate.exchange(requestEntity, String.class);
        } catch (URISyntaxException e) {
            log.error("Error creating cities by country URI: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating URI");
        }
    }

    public List<CityResponse> getCitiesResponse(ResponseEntity<String> responseEntity) {
        try {
            return objectMapper.readValue(responseEntity.getBody(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON cities response: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

}
