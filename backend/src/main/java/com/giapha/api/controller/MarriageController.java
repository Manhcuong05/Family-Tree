package com.giapha.api.controller;

import com.giapha.api.entity.Marriage;
import com.giapha.api.repository.MarriageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/marriages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MarriageController {

    private final MarriageRepository marriageRepository;

    @GetMapping
    public ResponseEntity<List<Marriage>> getAllMarriages() {
        return ResponseEntity.ok(marriageRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Marriage> createMarriage(@RequestBody Marriage marriage) {
        return ResponseEntity.ok(marriageRepository.save(marriage));
    }
}
