package br.com.jtech.tasklist.adapters.input.protocols.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequest(

        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
        String title,

        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        String description,

        @NotBlank(message = "List name is required")
        @Size(min = 1, max = 100, message = "List name must be between 1 and 100 characters")
        String listName,

        Boolean completed

) {}
