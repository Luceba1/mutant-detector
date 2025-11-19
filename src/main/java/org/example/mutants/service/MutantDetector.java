package org.example.mutants.service;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MutantDetector {

    private static final int SEQUENCE_LENGTH = 4;
    private static final Set<Character> VALID_BASES = Set.of('A', 'T', 'C', 'G');

    public boolean isMutant(String[] dna) {
        if (!isValidDna(dna)) {
            return false;
        }

        int n = dna.length;

        // Conversión a char[][] para acceso O(1)
        char[][] matrix = new char[n][];
        for (int i = 0; i < n; i++) {
            matrix[i] = dna[i].toCharArray();
        }

        int sequenceCount = 0;

        // Single pass con early termination
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {

                char base = matrix[row][col];

                // Horizontal →
                if (col <= n - SEQUENCE_LENGTH &&
                        base == matrix[row][col + 1] &&
                        base == matrix[row][col + 2] &&
                        base == matrix[row][col + 3]) {
                    if (++sequenceCount > 1) return true;
                }

                // Vertical ↓
                if (row <= n - SEQUENCE_LENGTH &&
                        base == matrix[row + 1][col] &&
                        base == matrix[row + 2][col] &&
                        base == matrix[row + 3][col]) {
                    if (++sequenceCount > 1) return true;
                }

                // Diagonal ↘
                if (row <= n - SEQUENCE_LENGTH && col <= n - SEQUENCE_LENGTH &&
                        base == matrix[row + 1][col + 1] &&
                        base == matrix[row + 2][col + 2] &&
                        base == matrix[row + 3][col + 3]) {
                    if (++sequenceCount > 1) return true;
                }

                // Diagonal ↗
                if (row >= SEQUENCE_LENGTH - 1 && col <= n - SEQUENCE_LENGTH &&
                        base == matrix[row - 1][col + 1] &&
                        base == matrix[row - 2][col + 2] &&
                        base == matrix[row - 3][col + 3]) {
                    if (++sequenceCount > 1) return true;
                }
            }
        }
        return false;
    }

    private boolean isValidDna(String[] dna) {
        if (dna == null || dna.length == 0) return false;

        int n = dna.length;
        if (n < SEQUENCE_LENGTH) return false;

        for (String row : dna) {
            if (row == null || row.length() != n) return false;
            for (char c : row.toCharArray()) {
                if (!VALID_BASES.contains(c)) return false;
            }
        }
        return true;
    }
}
