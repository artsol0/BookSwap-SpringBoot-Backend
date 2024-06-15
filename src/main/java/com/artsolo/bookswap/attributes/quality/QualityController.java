package com.artsolo.bookswap.attributes.quality;

import com.artsolo.bookswap.responses.ErrorDescription;
import com.artsolo.bookswap.responses.ErrorResponse;
import com.artsolo.bookswap.responses.MessageResponse;
import com.artsolo.bookswap.responses.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quality")
public class QualityController {
    private final QualityService qualityService;

    public QualityController(QualityService qualityService) {
        this.qualityService = qualityService;
    }

    @PostMapping("/add")
    public ResponseEntity<SuccessResponse<Quality>> addNewQuality(@RequestParam("quality") String quality) {
        return ResponseEntity.ok().body(new SuccessResponse<>(qualityService.addNewQuality(quality)));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteQualityById(@PathVariable Long id) {
        if (qualityService.deleteQuality(qualityService.getQualityById(id))) {
            return ResponseEntity.ok().body(MessageResponse.builder().message("Quality was deleted successfully").build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Quality still exist")).build());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<SuccessResponse<Quality>> getQualityById(@PathVariable Long id) {
        return ResponseEntity.ok().body(new SuccessResponse<>(qualityService.getQualityById(id)));
    }

    @GetMapping("/get/all")
    public ResponseEntity<SuccessResponse<List<Quality>>> getAllQualities() {
        return ResponseEntity.ok().body(new SuccessResponse<>(qualityService.getAllQualities()));
    }
}
