package com.artsolo.bookswap.attributes.language;

import com.artsolo.bookswap.responses.ErrorDescription;
import com.artsolo.bookswap.responses.ErrorResponse;
import com.artsolo.bookswap.responses.MessageResponse;
import com.artsolo.bookswap.responses.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/language")
public class LanguageController {
    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @PostMapping("/add")
    public ResponseEntity<SuccessResponse<Language>> addNewLanguage(@RequestParam("language") String language) {
        return ResponseEntity.ok().body(new SuccessResponse<>(languageService.addNewLanguage(language)));
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
    public ResponseEntity<SuccessResponse<Language>> getLanguageById(@PathVariable Long id) {
        return ResponseEntity.ok().body(new SuccessResponse<>(languageService.getLanguageById(id)));
    }

    @GetMapping("/get/all")
    public ResponseEntity<SuccessResponse<List<Language>>> getAllLanguages() {
        return ResponseEntity.ok().body(new SuccessResponse<>(languageService.getAllLanguages()));
    }

}
