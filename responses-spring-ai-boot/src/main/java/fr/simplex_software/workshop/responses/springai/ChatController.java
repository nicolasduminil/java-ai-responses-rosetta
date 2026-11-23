package fr.simplex_software.workshop.responses.springai;

import fr.simplex_software.workshop.responses.api.*;
import jakarta.validation.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class ChatController
{
  private final ResponsesService responsesService;

  public ChatController(ResponsesService responsesService)
  {
    this.responsesService = responsesService;
  }

  @PostMapping("/chat")
  public Answer chat(@Valid @RequestBody Question question)
  {
    return responsesService.chat(question);
  }
}
