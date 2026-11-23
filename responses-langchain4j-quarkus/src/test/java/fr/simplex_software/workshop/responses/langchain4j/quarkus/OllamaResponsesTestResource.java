package fr.simplex_software.workshop.responses.langchain4j.quarkus;

import fr.simplex_software.workshop.responses.conformance.*;
import io.quarkus.test.common.*;

import java.util.*;

public class OllamaResponsesTestResource implements QuarkusTestResourceLifecycleManager
{
  @Override
  public Map<String, String> start()
  {
    return Map.of(
      "quarkus.langchain4j.openai.base-url", OllamaTestSupport.openAiBaseUrl(),
      "quarkus.langchain4j.openai.chat-model.model-name", OllamaTestSupport.model(),
      "quarkus.langchain4j.openai.api-key", "ollama");
  }

  @Override
  public void stop()
  {
  }
}
