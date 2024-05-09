package com.artsolo.bookswap.controllers.attributes;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.services.LanguageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/language")
public class LanguageController {
    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNewLanguage(@RequestParam("language") String language) {
        if (languageService.addNewLanguage(language)) {
            return ResponseEntity.ok().body(MessageResponse.builder().message("Language was added successfully").build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Filed to add new language")).build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteLanguageById(@PathVariable Long id) {
        if (languageService.deleteLanguage(languageService.getLanguageById(id))) {
            return ResponseEntity.ok().body(MessageResponse.builder().message("Language was deleted successfully").build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Language still exist")).build());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getLanguageById(@PathVariable Long id) {
        return ResponseEntity.ok().body(SuccessResponse.builder().data(languageService.getLanguageById(id)).build());
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllLanguages() {
        return ResponseEntity.ok().body(SuccessResponse.builder().data(languageService.getAllLanguages()).build());
    }

}
