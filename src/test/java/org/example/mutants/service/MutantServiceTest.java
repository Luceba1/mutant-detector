package org.example.mutants.service;

import org.example.mutants.entity.DnaRecord;
import org.example.mutants.repository.DnaRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MutantServiceTest {

    @Mock
    private MutantDetector mutantDetector;

    @Mock
    private DnaRecordRepository dnaRecordRepository;

    @InjectMocks
    private MutantService mutantService;

    // 1) Si el ADN ya existe como mutante en la BD, debe devolver true sin llamar al detector
    @Test
    void whenDnaAlreadyStoredAsMutant_returnTrueWithoutCallingDetector() {
        String[] dna = {"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};

        DnaRecord record = DnaRecord.builder()
                .id(1L)
                .dnaHash("hash123")
                .isMutant(true)
                .build();

        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.of(record));

        boolean result = mutantService.process(dna);

        assertTrue(result);
        verify(mutantDetector, never()).isMutant(any());
        verify(dnaRecordRepository, never()).save(any());
    }

    // 2) Si el ADN ya existe como humano en la BD, debe devolver false sin llamar al detector
    @Test
    void whenDnaAlreadyStoredAsHuman_returnFalseWithoutCallingDetector() {
        String[] dna = {"ATGCGA", "CAGTGC", "TTATGT", "AGACGG", "GCGTCA", "TCACTG"};

        DnaRecord record = DnaRecord.builder()
                .id(2L)
                .dnaHash("hash456")
                .isMutant(false)
                .build();

        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.of(record));

        boolean result = mutantService.process(dna);

        assertFalse(result);
        verify(mutantDetector, never()).isMutant(any());
        verify(dnaRecordRepository, never()).save(any());
    }

    // 3) Si el ADN no existe, llama al detector, guarda como mutante y devuelve true
    @Test
    void whenDnaNotStoredAndDetectorReturnsMutant_saveAsMutantAndReturnTrue() {
        String[] dna = {"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};

        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.empty());
        when(mutantDetector.isMutant(dna)).thenReturn(true);

        ArgumentCaptor<DnaRecord> captor = ArgumentCaptor.forClass(DnaRecord.class);

        boolean result = mutantService.process(dna);

        assertTrue(result);
        verify(mutantDetector).isMutant(dna);
        verify(dnaRecordRepository).save(captor.capture());

        DnaRecord saved = captor.getValue();
        assertTrue(saved.isMutant());
        assertNotNull(saved.getDnaHash());
        assertFalse(saved.getDnaHash().isBlank());
    }

    // 4) Si el ADN no existe, llama al detector, guarda como humano y devuelve false
    @Test
    void whenDnaNotStoredAndDetectorReturnsHuman_saveAsHumanAndReturnFalse() {
        String[] dna = {"ATGCGA", "CAGTGC", "TTATGT", "AGACGG", "GCGTCA", "TCACTG"};

        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.empty());
        when(mutantDetector.isMutant(dna)).thenReturn(false);

        ArgumentCaptor<DnaRecord> captor = ArgumentCaptor.forClass(DnaRecord.class);

        boolean result = mutantService.process(dna);

        assertFalse(result);
        verify(mutantDetector).isMutant(dna);
        verify(dnaRecordRepository).save(captor.capture());

        DnaRecord saved = captor.getValue();
        assertFalse(saved.isMutant());
        assertNotNull(saved.getDnaHash());
    }

    // 5) Si llamo dos veces con el mismo ADN, la segunda vez no debería volver a llamar al detector
    @Test
    void whenProcessingSameDnaTwice_detectorCalledOnlyOnce() {
        String[] dna = {"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};

        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.empty()) // primera vez no está
                .thenReturn(Optional.of(DnaRecord.builder()
                        .id(1L)
                        .dnaHash("hashXYZ")
                        .isMutant(true)
                        .build())); // segunda vez ya está

        when(mutantDetector.isMutant(dna)).thenReturn(true);

        boolean first = mutantService.process(dna);
        boolean second = mutantService.process(dna);

        assertTrue(first);
        assertTrue(second);

        verify(mutantDetector, times(1)).isMutant(dna);
        verify(dnaRecordRepository, times(1)).save(any());
    }

    // 6) Dos ADN diferentes deben generar dos hashes distintos guardados en la BD
    @Test
    void differentDnaShouldBeSavedWithDifferentHashes() {
        String[] dna1 = {"AAAA", "AAAA", "TGCT", "CGTA"};
        String[] dna2 = {"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};

        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.empty());

        when(mutantDetector.isMutant(dna1)).thenReturn(true);
        when(mutantDetector.isMutant(dna2)).thenReturn(false);

        ArgumentCaptor<DnaRecord> captor = ArgumentCaptor.forClass(DnaRecord.class);

        mutantService.process(dna1);
        mutantService.process(dna2);

        verify(dnaRecordRepository, times(2)).save(captor.capture());

        DnaRecord first = captor.getAllValues().get(0);
        DnaRecord second = captor.getAllValues().get(1);

        assertNotEquals(first.getDnaHash(), second.getDnaHash());
    }

    // 7) Si el detector lanza una RuntimeException, la excepción debe propagarse
    @Test
    void whenDetectorThrowsException_exceptionIsPropagated() {
        String[] dna = {"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};

        when(dnaRecordRepository.findByDnaHash(anyString()))
                .thenReturn(Optional.empty());
        when(mutantDetector.isMutant(dna)).thenThrow(new RuntimeException("Error interno"));

        assertThrows(RuntimeException.class, () -> mutantService.process(dna));

        verify(mutantDetector).isMutant(dna);
    }
}
