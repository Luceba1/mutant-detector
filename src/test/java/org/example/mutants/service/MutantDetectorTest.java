package org.example.mutants.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MutantDetectorTest {

    private final MutantDetector detector = new MutantDetector();

    // 1) Mutante clásico del enunciado (horizontal + vertical/diagonal)
    @Test
    void mutantClassicExample() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertTrue(detector.isMutant(dna));
    }

    // 2) Humano clásico: ADN válido sin suficientes secuencias
    @Test
    void humanClassicExample() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
        };
        assertFalse(detector.isMutant(dna));
    }

    // 3) Mutante con muchas secuencias horizontales en los bordes
    @Test
    void mutantHorizontalAtBorders() {
        String[] dna = {
                "AAAAAA",
                "CCCCCC",
                "GGGGGG",
                "TTTTTT",
                "ATGCAT",
                "CGTACG"
        };
        assertTrue(detector.isMutant(dna));
    }

    // 4) Humano: exactamente UNA secuencia horizontal de 4 iguales
    @Test
    void humanWithSingleHorizontalSequence() {
        String[] dna = {
                "AAAA",
                "TGCT",
                "CGTG",
                "GTCT"
        };
        assertFalse(detector.isMutant(dna));
    }

    // 5) Mutante con varias secuencias verticales
    @Test
    void mutantWithVerticalSequences() {
        String[] dna = {
                "ATGCGA",
                "ATGCGA",
                "ATGCGA",
                "ATGCGA",
                "CCCCCC",
                "GGGGGG"
        };
        assertTrue(detector.isMutant(dna));
    }

    // 6) Mutante válido en tamaño mínimo 4x4 (dos filas con AAAA)
    @Test
    void mutantMinimumSize4x4() {
        String[] dna = {
                "AAAA",
                "AAAA",
                "TGCT",
                "CGTA"
        };
        assertTrue(detector.isMutant(dna));
    }

    // 7) Mutante con patrón más complejo (varias direcciones mezcladas)
    @Test
    void mutantWithComplexPatterns() {
        String[] dna = {
                "AGTCGT",
                "TACGTG",
                "GCATGA",
                "CGTACT",
                "ATGCAT",
                "CGTACG"
        };
        assertTrue(detector.isMutant(dna));
    }

    // 8) Mutante en matriz grande 10x10 (stress test)
    @Test
    void mutantLarge10x10() {
        String[] dna = {
                "AAAAATTTTT",
                "CCCCCGGGGG",
                "TTTTTAAAAA",
                "GGGGGCCCCC",
                "ATGCATGCAT",
                "CGTACGTACG",
                "ATGCATGCAT",
                "CGTACGTACG",
                "AAAACCCCGG",
                "TTTTGGGGCC"
        };
        assertTrue(detector.isMutant(dna));
    }

    // 9) Humano: otra variante de ADN válido sin secuencias suficientes
    @Test
    void humanWithoutAnySequenceVariant() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGACGG",
                "GCGACT",
                "TCACTG"
        };
        assertFalse(detector.isMutant(dna));
    }

    // 10) Humano 4x4 sin secuencias de 4 iguales
    @Test
    void human4x4WithoutSequences() {
        String[] dna = {
                "ATGC",
                "TACG",
                "CGTA",
                "GCAT"
        };
        assertFalse(detector.isMutant(dna));
    }

    // 11) Humano con diagonales pero sin llegar a 2 secuencias válidas
    @Test
    void humanDiagonalNoMutant() {
        String[] dna = {
                "AAGC",
                "CAAT",
                "TACA",
                "GTCA"
        };
        assertFalse(detector.isMutant(dna));
    }

    // 12) Inválido: array null
    @Test
    void invalidNullDna() {
        String[] dna = null;
        assertFalse(detector.isMutant(dna));
    }

    // 13) Inválido: array vacío
    @Test
    void invalidEmptyDna() {
        String[] dna = {};
        assertFalse(detector.isMutant(dna));
    }

    // 14) Inválido: matriz demasiado chica (< 4x4)
    @Test
    void invalidTooSmallMatrix() {
        String[] dna = {
                "ATG",
                "CAG",
                "TTA"
        };
        assertFalse(detector.isMutant(dna));
    }

    // 15) Inválido: matriz NO cuadrada (más filas que columnas)
    @Test
    void invalidNotSquareMoreRows() {
        String[] dna = {
                "ATGC",
                "CAGT",
                "TTAT",
                "AGAC",
                "GCGT"
        };
        assertFalse(detector.isMutant(dna));
    }

    // 16) Inválido: matriz NO cuadrada (más columnas que filas)
    @Test
    void invalidNotSquareMoreCols() {
        String[] dna = {
                "ATGCA",
                "CAGTC",
                "TTATT",
                "AGACC"
        };
        assertFalse(detector.isMutant(dna));
    }

    // 17) Inválido: contiene caracteres que no son A/T/C/G
    @Test
    void invalidDnaWithInvalidCharacter() {
        String[] dna = {
                "ATGX",
                "CAGT",
                "TTAT",
                "AGAC"
        };
        assertFalse(detector.isMutant(dna));
    }
}
