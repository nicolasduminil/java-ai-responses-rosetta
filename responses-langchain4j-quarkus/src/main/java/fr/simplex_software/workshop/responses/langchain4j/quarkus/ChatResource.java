package fr.simplex_software.workshop.responses.langchain4j.quarkus;

import fr.simplex_software.workshop.responses.api.*;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@Path("/chat")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChatResource
{
  private final ResponsesService responsesService;

  public ChatResource(ResponsesService responsesService)
  {
    this.responsesService = responsesService;
  }

  @POST
  public Answer chat(@Valid Question question)
  {
    return responsesService.chat(question);
  }
}
