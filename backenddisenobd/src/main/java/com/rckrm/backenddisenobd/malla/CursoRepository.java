package com.rckrm.backenddisenobd.malla;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CursoRepository extends JpaRepository<Curso, CursoId> {
    List<Curso> findByIdCodigoFacultadAndIdCodigoEscuelaAndIdCorrelativoPlanOrderBySemestreAscNombreAsc(
            Integer codigoFacultad, Integer codigoEscuela, Integer correlativoPlan);
}
