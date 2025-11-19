package org.example.mutants.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mutants.validation.ValidDnaSequence;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DnaRequest {

    @ValidDnaSequence
    private String[] dna;
}
