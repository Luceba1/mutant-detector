package org.example.mutants.controller;

import lombok.RequiredArgsConstructor;
import org.example.mutants.dto.DnaRequest;
import org.example.mutants.dto.StatsResponse;
import org.example.mutants.service.MutantService;
import org.example.mutants.service.StatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
public class MutantController {

    private final MutantService mutantService;
    private final StatsService statsService;

    @PostMapping("/mutant")
    public ResponseEntity<Void> isMutant(@RequestBody @Valid DnaRequest request) {
        boolean isMutant = mutantService.process(request.getDna());
        return isMutant
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> stats() {
        return ResponseEntity.ok(statsService.getStats());
    }
}
