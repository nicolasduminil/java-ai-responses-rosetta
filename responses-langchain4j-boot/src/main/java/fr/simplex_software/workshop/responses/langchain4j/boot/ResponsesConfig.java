package fr.simplex_software.workshop.responses.langchain4j.boot;

import dev.langchain4j.model.chat.*;
import dev.langchain4j.model.openai.*;
import fr.simplex_software.workshop.responses.api.*;
import fr.simplex_software.workshop.responses.langchain4j.*;
import org.springframework.context.annotation.*;

@Configuration
public class ResponsesConfig
{
  @Bean
  ChatModel responsesChatModel(ResponsesModelProperties properties)
  {
    return OpenAiResponsesChatModel.builder()
      .baseUrl(properties.baseUrl())
      .apiKey(properties.apiKey())
      .modelName(properties.model())
      .temperature(properties.temperature())
      .build();
  }

  @Bean
  ResponsesService responsesService(ChatModel responsesChatModel)
  {
    return new LangChain4jResponsesService(responsesChatModel);
  }
}
