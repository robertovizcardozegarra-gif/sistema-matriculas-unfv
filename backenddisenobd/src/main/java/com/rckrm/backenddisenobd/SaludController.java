package com.rckrm.backenddisenobd;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/salud")
public class SaludController {

    @GetMapping
    public Map<String, Object> salud() {
        return Map.of("estado", "OK", "servicio", "backenddisenobd", "instante", Instant.now());
    }
}
