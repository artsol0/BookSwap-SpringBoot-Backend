package com.artsolo.bookswap.controllers.attributes;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.models.Status;
import com.artsolo.bookswap.services.StatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/status")
public class StatusController {
    public final StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNewStatus(@RequestBody Map<String, String> request) {
        try {
            if (request.get("status") != null) {
                if (statusService.addNewStatus(request.get("status"))) {
                    return ResponseEntity.ok().body(MessageResponse.builder().message("Status was added successfully")
                            .build());
                }
                return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                        HttpStatus.BAD_REQUEST.value(), "Filed to add new status")).build());
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
    public ResponseEntity<?> deleteStatusById(@PathVariable Long id) {
        try {
            Optional<Status> status = statusService.getStatusById(id);
            if (status.isPresent()) {
                if (statusService.deleteStatus(status.get())) {
                    return ResponseEntity.ok().body(MessageResponse.builder().message("Status was deleted successfully")
                            .build());
                }
                return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                        HttpStatus.BAD_REQUEST.value(), "Status still exist")).build());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.NOT_FOUND.value(), "Status with id '" + id + "' not found")).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getStatusById(@PathVariable Long id) {
        try {
            Optional<Status> status = statusService.getStatusById(id);
            if (status.isPresent()) {
                return ResponseEntity.ok().body(SuccessResponse.builder().data(status).build());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.NOT_FOUND.value(), "Status with id '" + id + "' not found")).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllStatuses() {
        try {
            return ResponseEntity.ok().body(SuccessResponse.builder().data(statusService.getAllStatuses()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }
}
