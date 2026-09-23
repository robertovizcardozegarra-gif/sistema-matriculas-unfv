package com.rckrm.backenddisenobd.malla;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MezclaCursoRepository extends JpaRepository<MezclaCurso, MezclaCursoId> {
    List<MezclaCurso> findByIdCodigoFacultadAndIdCodigoEscuelaAndIdCorrelativoPlanAndIdCodigoCurso(
            Integer codigoFacultad, Integer codigoEscuela, Integer correlativoPlan, String codigoCurso);

    void deleteByIdCodigoFacultadAndIdCodigoEscuelaAndIdCorrelativoPlanAndIdCodigoCurso(
            Integer codigoFacultad, Integer codigoEscuela, Integer correlativoPlan, String codigoCurso);

    @Modifying
    @Query("""
            delete from MezclaCurso requisito
            where requisito.id.codigoFacultad = :facultad
              and requisito.id.codigoEscuela = :escuela
              and requisito.id.correlativoPlan = :plan
              and (requisito.id.codigoCurso = :curso
                   or requisito.id.codigoCursoPrerequisito = :curso)
            """)
    void deleteRelacionesCurso(@Param("facultad") Integer facultad,
                               @Param("escuela") Integer escuela,
                               @Param("plan") Integer plan,
                               @Param("curso") String curso);
}
