package org.example.mutants.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DtoCoverageTest {

    @Test
    void testDnaRequestAllLombokMethods() {
        String[] dna1 = {"ATGC", "CAGT"};
        String[] dna2 = {"TTTT", "CCCC"};

        // 1) Constructor sin argumentos + setter
        DnaRequest req1 = new DnaRequest();
        req1.setDna(dna1);
        assertArrayEquals(dna1, req1.getDna());

        // 2) Constructor con argumentos
        DnaRequest req2 = new DnaRequest(dna2);
        assertArrayEquals(dna2, req2.getDna());

        // 3) Builder
        DnaRequest req3 = DnaRequest.builder()
                .dna(dna1)
                .build();
        assertArrayEquals(dna1, req3.getDna());

        // 4) equals / hashCode / toString
        DnaRequest req4 = DnaRequest.builder()
                .dna(dna1)
                .build();

        assertEquals(req3, req4);
        assertEquals(req3.hashCode(), req4.hashCode());
        assertNotNull(req3.toString());
    }

    @Test
    void testStatsResponseAllLombokMethods() {
        // 1) Constructor sin argumentos + setters
        StatsResponse s1 = new StatsResponse();
        s1.setCount_mutant_dna(10L);
        s1.setCount_human_dna(5L);
        s1.setRatio(0.5);

        assertEquals(10L, s1.getCount_mutant_dna());
        assertEquals(5L, s1.getCount_human_dna());
        assertEquals(0.5, s1.getRatio());

        // 2) Constructor con argumentos
        StatsResponse s2 = new StatsResponse(10L, 5L, 0.5);

        // 3) equals / hashCode / toString
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
        assertNotNull(s1.toString());
    }
}
