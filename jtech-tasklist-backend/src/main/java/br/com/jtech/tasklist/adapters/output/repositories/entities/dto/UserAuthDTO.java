package br.com.jtech.tasklist.adapters.output.repositories.entities.dto;


import java.util.UUID;

public record UserAuthDTO(
        UUID id,
        String email,
        String name,
        String password,
        boolean active
) {

}
