package cl.huertohogar.backend.dto;

import lombok.Data;

@Data
public class ComentarioRequestDTO {
    private String texto;
    private Integer nota; // por ejemplo 1..5
}
