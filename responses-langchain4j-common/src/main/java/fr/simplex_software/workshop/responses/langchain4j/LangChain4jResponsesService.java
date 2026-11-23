package fr.simplex_software.workshop.responses.langchain4j;

import dev.langchain4j.model.chat.*;
import dev.langchain4j.model.openai.*;
import dev.langchain4j.service.*;
import fr.simplex_software.workshop.responses.api.*;

import java.util.*;

public class LangChain4jResponsesService implements ResponsesService
{
  private static final List<Map<String, Object>> WEB_SEARCH = List.of(Map.of("type", "web_search"));
  private static final int MAX_TOOL_ROUNDS = 5;

  private final ResponsesAssistant assistant;

  public LangChain4jResponsesService(ChatModel chatModel)
  {
    this.assistant = AiServices.builder(ResponsesAssistant.class)
      .chatModel(chatModel)
      .tools(new OrderTools())
      .maxToolCallingRoundTrips(MAX_TOOL_ROUNDS)
      .build();
  }

  @Override
  public Answer chat(Question question)
  {
    var parameters = OpenAiResponsesChatRequestParameters.builder()
      .previousResponseId(question.previousResponseId());
    if (question.webSearch())
      parameters.serverTools(WEB_SEARCH);
    var result = assistant.chat(question.question(), parameters.build());
    return new Answer(result.content(), result.finalResponse().id());
  }
}
