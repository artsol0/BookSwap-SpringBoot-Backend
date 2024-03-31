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
import java.util.Optional;

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
                    return ResponseEntity.ok().body(MessageResponse.builder().message("Quality was added successfully")
                            .build());
                }
                return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                        HttpStatus.BAD_REQUEST.value(), "Filed to add new quality")).build());
            }
            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "Bad request")).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteQualityById(@PathVariable Long id) {
        try {
            Optional<Quality> quality = qualityService.getQualityById(id);
            if (quality.isPresent()) {
                if (qualityService.deleteQuality(quality.get())) {
                    return ResponseEntity.ok().body(MessageResponse.builder().message("Quality was deleted successfully")
                            .build());
                }
                return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                        HttpStatus.BAD_REQUEST.value(), "Quality still exist")).build());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.NOT_FOUND.value(), "Quality with id '" + id + "' not found")).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getQualityById(@PathVariable Long id) {
        try {
            Optional<Quality> quality = qualityService.getQualityById(id);
            if (quality.isPresent()) {
                return ResponseEntity.ok().body(SuccessResponse.builder().data(quality).build());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.NOT_FOUND.value(), "Quality with id '" + id + "' not found")).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllQualities() {
        try {
            return ResponseEntity.ok().body(SuccessResponse.builder().data(qualityService.getAllQualities()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }
}
