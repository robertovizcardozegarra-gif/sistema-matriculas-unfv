package com.rckrm.backenddisenobd.catalogo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FacultadRepository extends JpaRepository<Facultad, Integer> {
    List<Facultad> findAllByOrderByNombreAsc();
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsBySiglaIgnoreCase(String sigla);

    @Query("select coalesce(max(f.codigo), 0) + 1 from Facultad f")
    Integer siguienteCodigo();
}
