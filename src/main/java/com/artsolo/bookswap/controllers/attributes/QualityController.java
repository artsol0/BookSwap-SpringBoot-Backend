package com.artsolo.bookswap.controllers.attributes;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
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
    public ResponseEntity<?> addNewQuality(@RequestBody Map<String, String> request) {
        try {
            if (request.get("quality") != null) {
                if (qualityService.addNewQuality(request.get("quality"))) {
                    return ResponseEntity.ok().body(new MessageResponse("Quality was added successfully"));
                }
                return ResponseEntity.badRequest().body(new ErrorResponse(new ErrorDescription(
                        HttpStatus.BAD_REQUEST.value(),
                        "Failed to add genre")
                ));
            }
            return ResponseEntity.badRequest().body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad request")
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error")
            ));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteQualityById(@PathVariable Long id) {
        try {
            if (qualityService.deleteQualityById(id)) {
                return ResponseEntity.ok().body(new MessageResponse("Quality was deleted successfully"));
            }
            return ResponseEntity.badRequest().body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(),
                    "Quality still exits")
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error")
            ));
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getQualityById(@PathVariable Long id) {
        try {
            Quality quality = qualityService.getQualityById(id);
            if (quality != null) {
                return ResponseEntity.ok().body(new SuccessResponse<>(quality));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.NOT_FOUND.value(),
                    "Quality with id '" + id + "' not found")
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error")
            ));
        }
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllQualities() {
        try {
            return ResponseEntity.ok().body(new SuccessResponse<>(qualityService.getAllQualities()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error")
            ));
        }
    }
}
