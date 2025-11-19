package org.example.mutants.service;

import org.example.mutants.dto.StatsResponse;
import org.example.mutants.repository.DnaRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private DnaRecordRepository dnaRecordRepository;

    @InjectMocks
    private StatsService statsService;

    // 1) Ratio normal: humanos > 0
    @Test
    void statsWithHumansAndMutants() {
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(40L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(100L);

        StatsResponse stats = statsService.getStats();

        assertEquals(40L, stats.getCount_mutant_dna());
        assertEquals(100L, stats.getCount_human_dna());
        assertEquals(0.4, stats.getRatio(), 0.0001);
    }

    // 2) Humanos = 0 → ratio debe ser 0 y no explotar
    @Test
    void statsWithZeroHumansRatioShouldBeZero() {
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(10L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(0L);

        StatsResponse stats = statsService.getStats();

        assertEquals(10L, stats.getCount_mutant_dna());
        assertEquals(0L, stats.getCount_human_dna());
        assertEquals(0.0, stats.getRatio(), 0.0001);
    }

    // 3) Sin registros en absoluto
    @Test
    void statsWithNoRecords() {
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(0L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(0L);

        StatsResponse stats = statsService.getStats();

        assertEquals(0L, stats.getCount_mutant_dna());
        assertEquals(0L, stats.getCount_human_dna());
        assertEquals(0.0, stats.getRatio(), 0.0001);
    }

    // 4) Más mutantes que humanos (ratio > 1)
    @Test
    void statsWithMoreMutantsThanHumans() {
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(200L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(50L);

        StatsResponse stats = statsService.getStats();

        assertEquals(200L, stats.getCount_mutant_dna());
        assertEquals(50L, stats.getCount_human_dna());
        assertEquals(4.0, stats.getRatio(), 0.0001);
    }

    // 5) Números grandes para verificar que el ratio se calcula bien
    @Test
    void statsWithLargeNumbers() {
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(1_000_000L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(2_000_000L);

        StatsResponse stats = statsService.getStats();

        assertEquals(1_000_000L, stats.getCount_mutant_dna());
        assertEquals(2_000_000L, stats.getCount_human_dna());
        assertEquals(0.5, stats.getRatio(), 0.0001);
    }
}
