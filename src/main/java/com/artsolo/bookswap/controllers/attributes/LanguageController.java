package com.artsolo.bookswap.controllers.attributes;

import com.artsolo.bookswap.models.Language;
import com.artsolo.bookswap.services.LanguageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/language")
public class LanguageController {
    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addNewLanguage(@RequestBody Map<String, String> request) {
        try {
            if (languageService.addNewLanguage(request.get("language"))) {
                return ResponseEntity.ok("New language added");
            }
            return ResponseEntity.badRequest().body("Requested data was not presented in the database");
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteLanguageById(@PathVariable Long id) {
        try {
            if (languageService.deleteLanguageById(id)) {
                return ResponseEntity.ok("Language was deleted");
            }
            return ResponseEntity.badRequest().body("Requested data was not presented in the database");
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getLanguageById(@PathVariable Long id) {
        try {
            Language language = languageService.getLanguageById(id);
            if (language != null) {
                return ResponseEntity.ok().body(language);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllLanguages() {
        try {
            return ResponseEntity.ok().body(languageService.getAllLanguages());
        } catch (Exception e) {
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
