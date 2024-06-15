package com.artsolo.bookswap.attributes.status;

import com.artsolo.bookswap.responses.ErrorDescription;
import com.artsolo.bookswap.responses.ErrorResponse;
import com.artsolo.bookswap.responses.MessageResponse;
import com.artsolo.bookswap.responses.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/status")
public class StatusController {
    public final StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @PostMapping("/add")
    public ResponseEntity<SuccessResponse<Status>> addNewStatus(@RequestParam("status") String status) {
        return ResponseEntity.ok().body(new SuccessResponse<>(statusService.addNewStatus(status)));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteStatusById(@PathVariable Long id) {
        if (statusService.deleteStatus(statusService.getStatusById(id))) {
            return ResponseEntity.ok().body(MessageResponse.builder().message("Status was deleted successfully").build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Status still exist")).build());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<SuccessResponse<Status>> getStatusById(@PathVariable Long id) {
        return ResponseEntity.ok().body(new SuccessResponse<>(statusService.getStatusById(id)));
    }

    @GetMapping("/get/all")
    public ResponseEntity<SuccessResponse<List<Status>>> getAllStatuses() {
        return ResponseEntity.ok().body(new SuccessResponse<>(statusService.getAllStatuses()));
    }
}
