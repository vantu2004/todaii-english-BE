package com.todaii.english.server.dictionary;

import com.todaii.english.core.port.DictionaryPort;
import com.todaii.english.shared.response.TodaiiEnglishResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/todaii-dictionary")
public class TodaiiDictionaryApiController {
    private final DictionaryPort dictionaryPort;

    @GetMapping
    public ResponseEntity<TodaiiEnglishResponse> search(@RequestParam(required = false) String word, @RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page, @RequestParam(defaultValue = "50") @Min(value = 1, message = "Size must be at least 1") int size){
        return ResponseEntity.ok().body(dictionaryPort.lookupTodaiiDictionaryApi(word, page, size));
    }
}
