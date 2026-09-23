package com.rckrm.backenddisenobd;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.admin.password=Admin123!")
@AutoConfigureMockMvc
class BackendApiIntegrationTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void administradorPuedeCrearUnaMallaConPrerequisito() throws Exception {
        String login = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"usuario":"admin","clave":"Admin123!"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andReturn().getResponse().getContentAsString();

        JsonNode loginJson = objectMapper.readTree(login);
        String authorization = "Bearer " + loginJson.get("token").asText();

        mvc.perform(post("/api/facultades")
                        .header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"codigo":901,"sigla":"FTEST","nombre":"Facultad de Prueba","estado":"A"}
                                """))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/facultades/901/escuelas")
                        .header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"codigo":1,"nombre":"Ingeniería de Prueba","estado":"A"}
                                """))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/facultades/901/escuelas/1/planes")
                        .header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"correlativo":1,"anio":2019,"nombre":"Plan 2019","estado":"VIGENTE"}
                                """))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/facultades/901/escuelas/1/planes/1/cursos")
                        .header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"codigoCurso":"MAT101","nombre":"Matemática I","semestre":1,
                                 "horasTeoria":3,"horasPractica":2,"creditos":4.0,"tipo":"OBLIGATORIO",
                                 "estado":"A","prerequisitos":[]}
                                """))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/facultades/901/escuelas/1/planes/1/cursos")
                        .header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"codigoCurso":"MAT202","nombre":"Matemática II","semestre":2,
                                 "horasTeoria":3,"horasPractica":2,"creditos":4.0,"tipo":"OBLIGATORIO",
                                 "estado":"A","prerequisitos":[{"codigoCurso":"MAT101","tipo":"OBLIGATORIO"}]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.prerequisitos", hasSize(1)));

        mvc.perform(get("/api/facultades/901/escuelas/1/planes/1/cursos")
                        .header("Authorization", authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mvc.perform(post("/api/facultades/901/escuelas/1/planes/1/cursos/lote")
                        .header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [{"codigoCurso":"FIS101","nombre":"Física","semestre":1,
                                  "horasTeoria":3,"horasPractica":2,"creditos":4.0,"tipo":"OBLIGATORIO",
                                  "estado":"A","prerequisitos":[]}]
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(1)));

        mvc.perform(post("/api/facultades/901/escuelas/1/planes/1/cursos")
                        .header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"codigoCurso":"DEC101","nombre":"Crédito decimal","semestre":1,
                                 "horasTeoria":1,"horasPractica":0,"creditos":1.5,"tipo":"OBLIGATORIO",
                                 "estado":"A","prerequisitos":[]}
                                """))
                .andExpect(status().isBadRequest());

        mvc.perform(delete("/api/facultades/901/escuelas/1/planes/1/cursos/MAT101")
                        .header("Authorization", authorization))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/facultades/901/escuelas/1/planes/1/cursos/MAT202")
                        .header("Authorization", authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prerequisitos", hasSize(0)));
    }
}
