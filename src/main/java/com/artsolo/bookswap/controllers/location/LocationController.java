package com.artsolo.bookswap.controllers.location;

import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.services.LocationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/location")
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/get/countries")
    public ResponseEntity<?> getCountries() throws URISyntaxException, JsonProcessingException {
        ResponseEntity<String> responseEntity = locationService.getAllCountries();
        List<CountryResponse> countries = locationService.getCountriesResponse(responseEntity);
        return ResponseEntity.ok().body(SuccessResponse.builder().data(countries).build());
    }

    @GetMapping("/get/countries/{iso2}/cities")
    public ResponseEntity<?> getCities(@PathVariable String iso2) throws URISyntaxException, JsonProcessingException {
        ResponseEntity<String> responseEntity = locationService.getCitiesByCountry(iso2);
        List<CityResponse> cities = locationService.getCitiesResponse(responseEntity);
        return ResponseEntity.ok().body(SuccessResponse.builder().data(cities).build());
    }
}
