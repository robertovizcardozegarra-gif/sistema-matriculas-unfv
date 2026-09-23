package com.rckrm.backenddisenobd.catalogo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EscuelaRepository extends JpaRepository<Escuela, EscuelaId> {
    List<Escuela> findByIdCodigoFacultadOrderByNombreAsc(Integer codigoFacultad);
    boolean existsByIdCodigoFacultadAndNombreIgnoreCase(Integer codigoFacultad, String nombre);

    @Query("select coalesce(max(e.id.codigoEscuela), 0) + 1 from Escuela e "
            + "where e.id.codigoFacultad = :codigoFacultad")
    Integer siguienteCodigo(@Param("codigoFacultad") Integer codigoFacultad);
}
