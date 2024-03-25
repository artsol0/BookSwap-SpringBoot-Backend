package com.artsolo.bookswap.controllers;

import com.artsolo.bookswap.models.Language;
import com.artsolo.bookswap.models.Status;
import com.artsolo.bookswap.services.StatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/status")
public class StatusController {
    public final StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addNewStatus(@RequestBody Map<String, String> request) {
        try {
            if (statusService.addNewStatus(request.get("status"))) {
                return ResponseEntity.ok("New status added");
            }
            return ResponseEntity.badRequest().body("Requested data was not presented in the database");
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteStatusById(@PathVariable Long id) {
        try {
            if (statusService.deleteStatusById(id)) {
                return ResponseEntity.ok("Status was deleted");
            }
            return ResponseEntity.badRequest().body("Requested data was not presented in the database");
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getStatusById(@PathVariable Long id) {
        try {
            Status status = statusService.getStatusById(id);
            if (status != null) {
                return ResponseEntity.ok().body(status);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllStatuses() {
        try {
            return ResponseEntity.ok().body(statusService.getAllStatuses());
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
