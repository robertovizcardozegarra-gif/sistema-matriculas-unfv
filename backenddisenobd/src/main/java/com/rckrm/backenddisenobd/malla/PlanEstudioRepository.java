package com.rckrm.backenddisenobd.malla;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlanEstudioRepository extends JpaRepository<PlanEstudio, PlanEstudioId> {
    List<PlanEstudio> findByIdCodigoFacultadAndIdCodigoEscuelaOrderByAnioDesc(
            Integer codigoFacultad, Integer codigoEscuela);

    boolean existsByIdCodigoFacultadAndIdCodigoEscuelaAndAnio(
            Integer codigoFacultad, Integer codigoEscuela, Short anio);

    @Query("select coalesce(max(p.id.correlativo), 0) + 1 from PlanEstudio p "
            + "where p.id.codigoFacultad = :codigoFacultad and p.id.codigoEscuela = :codigoEscuela")
    Integer siguienteCorrelativo(@Param("codigoFacultad") Integer codigoFacultad,
                                 @Param("codigoEscuela") Integer codigoEscuela);
}
