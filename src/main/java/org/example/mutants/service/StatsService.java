package org.example.mutants.service;

import lombok.RequiredArgsConstructor;
import org.example.mutants.dto.StatsResponse;
import org.example.mutants.repository.DnaRecordRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final DnaRecordRepository dnaRecordRepository;

    public StatsResponse getStats() {
        long mutants = dnaRecordRepository.countByIsMutant(true);
        long humans = dnaRecordRepository.countByIsMutant(false);
        double ratio = humans == 0 ? 0.0 : (double) mutants / humans;
        return new StatsResponse(mutants, humans, ratio);
    }
}
