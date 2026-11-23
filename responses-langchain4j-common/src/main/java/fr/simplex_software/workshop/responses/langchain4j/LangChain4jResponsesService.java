package fr.simplex_software.workshop.responses.langchain4j;

import dev.langchain4j.agent.tool.*;
import dev.langchain4j.data.message.*;
import dev.langchain4j.model.chat.*;
import dev.langchain4j.model.chat.request.*;
import dev.langchain4j.model.openai.*;
import dev.langchain4j.service.tool.*;
import fr.simplex_software.workshop.responses.api.*;

import java.util.*;

/**
 * Talks to the Responses chat model directly rather than through an AI service: the Responses specific request
 * parameters ({@code previous_response_id}, hosted server tools) travel in {@link OpenAiResponsesChatRequestParameters},
 * which an AI service does not let a caller set per invocation.
 */
public class LangChain4jResponsesService implements ResponsesService
{
  private static final List<Map<String, Object>> WEB_SEARCH = List.of(Map.of("type", "web_search"));
  private static final int MAX_TOOL_ROUNDS = 5;

  private final ChatModel chatModel;
  private final OrderTools orderTools = new OrderTools();
  private final List<ToolSpecification> toolSpecifications = ToolSpecifications.toolSpecificationsFrom(OrderTools.class);

  public LangChain4jResponsesService(ChatModel chatModel)
  {
    this.chatModel = chatModel;
  }

  @Override
  public Answer chat(Question question)
  {
    List<ChatMessage> messages = new ArrayList<>(List.of(UserMessage.from(question.question())));
    var response = chatModel.chat(request(messages, question));
    for (int round = 0; response.aiMessage().hasToolExecutionRequests(); round++)
    {
      if (round == MAX_TOOL_ROUNDS)
        throw new IllegalStateException("The model kept requesting tools after " + MAX_TOOL_ROUNDS + " rounds");
      messages.add(response.aiMessage());
      for (var toolCall : response.aiMessage().toolExecutionRequests())
        messages.add(ToolExecutionResultMessage.from(toolCall,
          new DefaultToolExecutor(orderTools, toolCall).execute(toolCall, null)));
      response = chatModel.chat(request(messages, question));
    }
    return new Answer(response.aiMessage().text(), response.id());
  }

  private ChatRequest request(List<ChatMessage> messages, Question question)
  {
    var parameters = OpenAiResponsesChatRequestParameters.builder()
      .toolSpecifications(toolSpecifications)
      .previousResponseId(question.previousResponseId());
    if (question.webSearch())
      parameters.serverTools(WEB_SEARCH);
    return ChatRequest.builder().messages(messages).parameters(parameters.build()).build();
  }
}
