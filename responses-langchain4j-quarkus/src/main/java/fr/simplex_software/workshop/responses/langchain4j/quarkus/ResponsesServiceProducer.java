package fr.simplex_software.workshop.responses.langchain4j.quarkus;

import dev.langchain4j.model.chat.*;
import fr.simplex_software.workshop.responses.api.*;
import fr.simplex_software.workshop.responses.langchain4j.*;
import jakarta.enterprise.context.*;
import jakarta.enterprise.inject.*;

@ApplicationScoped
public class ResponsesServiceProducer
{
  @Produces
  @ApplicationScoped
  ResponsesService responsesService(ChatModel chatModel)
  {
    return new LangChain4jResponsesService(chatModel);
  }
}
