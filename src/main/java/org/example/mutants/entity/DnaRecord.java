package org.example.mutants.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dna_records",
        indexes = {
                @Index(name = "idx_dna_hash", columnList = "dnaHash", unique = true),
                @Index(name = "idx_is_mutant", columnList = "isMutant")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DnaRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 512)
    private String dnaHash;

    @Column(nullable = false)
    private boolean isMutant;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
