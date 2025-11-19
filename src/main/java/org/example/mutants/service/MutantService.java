package org.example.mutants.service;

import lombok.RequiredArgsConstructor;
import org.example.mutants.entity.DnaRecord;
import org.example.mutants.exception.DnaHashCalculationException;
import org.example.mutants.repository.DnaRecordRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MutantService {

    private final MutantDetector mutantDetector;
    private final DnaRecordRepository dnaRecordRepository;
    public boolean process(String[] dna) {
        String dnaHash = calculateHash(dna);

        return dnaRecordRepository.findByDnaHash(dnaHash)
                .map(DnaRecord::isMutant)
                .orElseGet(() -> {
                    boolean isMutant = mutantDetector.isMutant(dna);

                    DnaRecord record = DnaRecord.builder()
                            .dnaHash(dnaHash)
                            .isMutant(isMutant)
                            .createdAt(LocalDateTime.now())
                            .build();

                    dnaRecordRepository.save(record);
                    return isMutant;
                });
    }

    private String calculateHash(String[] dna) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String joined = String.join("-", dna); // misma secuencia → mismo hash
            byte[] hashBytes = digest.digest(joined.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new DnaHashCalculationException("Error calculando hash de ADN", e);
        }
    }
}
