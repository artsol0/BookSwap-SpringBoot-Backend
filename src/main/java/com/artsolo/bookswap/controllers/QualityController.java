package com.artsolo.bookswap.controllers;

import com.artsolo.bookswap.models.Quality;
import com.artsolo.bookswap.services.QualityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/quality")
public class QualityController {
    private final QualityService qualityService;

    public QualityController(QualityService qualityService) {
        this.qualityService = qualityService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addNewStatus(@RequestBody Map<String, String> request) {
        try {
            if (qualityService.addNewQuality(request.get("quality"))) {
                return ResponseEntity.ok("New quality added");
            }
            return ResponseEntity.badRequest().body("Requested data was not presented in the database");
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteQualityById(@PathVariable Long id) {
        try {
            if (qualityService.deleteQualityById(id)) {
                return ResponseEntity.ok("Quality was deleted");
            }
            return ResponseEntity.badRequest().body("Requested data was not presented in the database");
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getQualityById(@PathVariable Long id) {
        try {
            Quality quality = qualityService.getQualityById(id);
            if (quality != null) {
                return ResponseEntity.ok().body(quality);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllStatuses() {
        try {
            return ResponseEntity.ok().body(qualityService.getAllQualities());
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
