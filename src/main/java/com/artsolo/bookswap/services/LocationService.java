package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.location.CityResponse;
import com.artsolo.bookswap.controllers.location.CountryResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Service
public class LocationService {

    private final String baseUrl = "https://api.countrystatecity.in/v1/countries";
    private final HttpHeaders headers = new HttpHeaders();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    public LocationService() throws IOException {
        this.headers.setContentType(MediaType.APPLICATION_JSON);
        this.headers.set("X-CSCAPI-KEY", getAPIkey());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public ResponseEntity<String> getAllCountries() throws URISyntaxException {
        RequestEntity<Void> requestEntity = RequestEntity
                .get(new URI(baseUrl))
                .headers(headers)
                .build();

        return restTemplate.exchange(requestEntity, String.class);
    }

    public List<CountryResponse> getCountriesResponse(ResponseEntity<String> responseEntity) throws JsonProcessingException {
        return objectMapper.readValue(responseEntity.getBody(), new TypeReference<>() {});
    }

    public ResponseEntity<String> getCitiesByCountry(String iso2) throws URISyntaxException {
        RequestEntity<Void> requestEntity = RequestEntity
                .get(new URI(baseUrl + "/" + iso2 + "/cities"))
                .headers(headers)
                .build();

        return restTemplate.exchange(requestEntity, String.class);
    }

    public List<CityResponse> getCitiesResponse(ResponseEntity<String> responseEntity) throws JsonProcessingException {
        return objectMapper.readValue(responseEntity.getBody(), new TypeReference<>() {});
    }

    private String getAPIkey() throws IOException {
        FileReader fileReader = new FileReader("../../key.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String key = bufferedReader.readLine();
        bufferedReader.close();
        return key;
    }

}
