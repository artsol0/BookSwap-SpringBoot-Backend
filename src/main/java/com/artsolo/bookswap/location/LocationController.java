package com.artsolo.bookswap.location;

import com.artsolo.bookswap.responses.SuccessResponse;
import com.artsolo.bookswap.location.dto.CityResponse;
import com.artsolo.bookswap.location.dto.CountryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {this.locationService = locationService;}

    @GetMapping("/get/countries")
    public ResponseEntity<SuccessResponse<List<CountryResponse>>> getCountries() {
        ResponseEntity<String> responseEntity = locationService.getAllCountries();
        List<CountryResponse> countries = locationService.getCountriesResponse(responseEntity);
        return ResponseEntity.ok().body(new SuccessResponse<>(countries));
    }

    @GetMapping("/get/countries/{iso2}/cities")
    public ResponseEntity<SuccessResponse<List<CityResponse>>> getCities(@PathVariable String iso2) {
        ResponseEntity<String> responseEntity = locationService.getCitiesByCountry(iso2);
        List<CityResponse> cities = locationService.getCitiesResponse(responseEntity);
        return ResponseEntity.ok().body(new SuccessResponse<>(cities));
    }
}
