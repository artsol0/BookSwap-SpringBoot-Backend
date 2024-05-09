package com.artsolo.bookswap.controllers.attributes;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.services.StatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/status")
public class StatusController {
    public final StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNewStatus(@RequestParam("status") String status) {
        if (statusService.addNewStatus(status)) {
            return ResponseEntity.ok().body(MessageResponse.builder().message("Status was added successfully").build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Filed to add new status")).build());
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
    public ResponseEntity<?> getStatusById(@PathVariable Long id) {
        return ResponseEntity.ok().body(SuccessResponse.builder().data(statusService.getStatusById(id)).build());
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllStatuses() {
        return ResponseEntity.ok().body(SuccessResponse.builder().data(statusService.getAllStatuses()).build());
    }
}
