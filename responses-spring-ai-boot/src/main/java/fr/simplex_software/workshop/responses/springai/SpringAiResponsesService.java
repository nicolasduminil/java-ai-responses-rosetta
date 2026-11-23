package fr.simplex_software.workshop.responses.springai;

import fr.simplex_software.workshop.responses.api.*;
import org.springframework.ai.chat.client.*;
import org.springframework.ai.openai.responses.*;
import org.springframework.stereotype.*;

/**
 * Spring AI 2.1 always sends {@code store: false} and has no option for {@code previous_response_id}, so
 * {@link Question#previousResponseId()} is ignored here: the conversation cannot continue on the server.
 */
@Service
public class SpringAiResponsesService implements ResponsesService
{
  private final ChatClient chatClient;

  public SpringAiResponsesService(ChatClient.Builder chatClientBuilder, OrderTools orderTools)
  {
    this.chatClient = chatClientBuilder.defaultTools(orderTools).build();
  }

  @Override
  public Answer chat(Question question)
  {
    var prompt = chatClient.prompt().user(question.question());
    if (question.webSearch())
      prompt = prompt.options(OpenAiResponsesChatOptions.builder().hostedTools(HostedTool.WebSearch.of()));
    var response = prompt.call().chatResponse();
    return new Answer(response.getResult().getOutput().getText(), response.getMetadata().getId());
  }
}
