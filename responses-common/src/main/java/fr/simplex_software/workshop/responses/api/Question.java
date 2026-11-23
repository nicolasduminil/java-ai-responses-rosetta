package fr.simplex_software.workshop.responses.api;

import jakarta.validation.constraints.*;

public record Question(@NotBlank String question, String previousResponseId, boolean webSearch)
{
}
