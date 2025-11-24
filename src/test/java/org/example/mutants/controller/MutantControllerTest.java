package org.example.mutants.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mutants.dto.DnaRequest;
import org.example.mutants.dto.StatsResponse;
import org.example.mutants.service.MutantService;
import org.example.mutants.service.StatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MutantController.class)
class MutantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MutantService mutantService;

    @MockBean
    private StatsService statsService;

    @Autowired
    private ObjectMapper objectMapper;

    // 1) POST /mutant → mutante → 200 OK
    @Test
    @DisplayName("POST /mutant con ADN mutante debería devolver 200 OK")
    void postMutantShouldReturn200() throws Exception {
        String[] dna = {"ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"};
        DnaRequest request = new DnaRequest(dna);

        when(mutantService.process(dna)).thenReturn(true);

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // 2) POST /mutant → humano → 403 Forbidden
    @Test
    @DisplayName("POST /mutant con ADN humano debería devolver 403 Forbidden")
    void postHumanShouldReturn403() throws Exception {
        String[] dna = {"ATGCGA","CAGTGC","TTATGT","AGACGG","GCGTCA","TCACTG"};
        DnaRequest request = new DnaRequest(dna);

        when(mutantService.process(dna)).thenReturn(false);

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // 3) GET /stats → devuelve JSON con stats
    @Test
    @DisplayName("GET /stats debería devolver estadísticas con 200 OK")
    void getStatsShouldReturnStats() throws Exception {
        StatsResponse response = new StatsResponse(40L, 100L, 0.4);
        when(statsService.getStats()).thenReturn(response);

        mockMvc.perform(get("/stats")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(40))
                .andExpect(jsonPath("$.count_human_dna").value(100))
                .andExpect(jsonPath("$.ratio").value(0.4));
    }

    // 4) POST /mutant con body vacío → ahora es inválido → 400 Bad Request
    @Test
    @DisplayName("POST /mutant con body vacío debería devolver 400 Bad Request")
    void postMutantWithEmptyBodyShouldReturn400() throws Exception {
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // 5) POST /mutant con dna = null → también es inválido → 400 Bad Request
    @Test
    @DisplayName("POST /mutant con dna null debería devolver 400 Bad Request")
    void postMutantWithNullDnaShouldReturn400() throws Exception {
        String json = "{\"dna\":null}";

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }


    // 6) GET /stats cuando no hay registros → 200 y todos 0
    @Test
    @DisplayName("GET /stats sin registros debería devolver ceros")
    void getStatsWithZeroValuesShouldReturnZeros() throws Exception {
        StatsResponse response = new StatsResponse(0L, 0L, 0.0);
        when(statsService.getStats()).thenReturn(response);

        mockMvc.perform(get("/stats")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(0))
                .andExpect(jsonPath("$.count_human_dna").value(0))
                .andExpect(jsonPath("$.ratio").value(0.0));
    }

    // 7) POST /mutant cuando el servicio lanza RuntimeException → se propaga la excepción
    @Test
    @DisplayName("POST /mutant cuando el servicio lanza excepción debería propagarla")
    void postMutantWhenServiceThrowsExceptionShouldPropagate() {
        String[] dna = {"ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"};
        DnaRequest request = new DnaRequest(dna);

        when(mutantService.process(dna)).thenThrow(new RuntimeException("Error inesperado"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(post("/mutant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andReturn()
        );
    }
}
