package org.example.mutants.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsResponse {

    private long count_mutant_dna;
    private long count_human_dna;
    private double ratio;
}
