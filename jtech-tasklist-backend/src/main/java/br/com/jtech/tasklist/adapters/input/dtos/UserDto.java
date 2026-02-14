package br.com.jtech.tasklist.adapters.input.dtos;

public record UserDto(
    String name,
    String email,
    String password
) {
}
