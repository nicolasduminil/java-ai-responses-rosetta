package fr.simplex_software.workshop.responses.langchain4j.boot;

import fr.simplex_software.workshop.responses.conformance.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.web.server.*;
import org.springframework.test.context.*;

import java.net.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LangChain4jLocalResponsesIT extends AbstractLocalResponsesIT
{
  @LocalServerPort
  private int port;

  @DynamicPropertySource
  static void ollama(DynamicPropertyRegistry registry)
  {
    registry.add("responses.openai.base-url", OllamaTestSupport::openAiBaseUrl);
    registry.add("responses.openai.model", OllamaTestSupport::model);
    registry.add("responses.openai.api-key", () -> "ollama");
  }

  @Override
  protected URI baseUri()
  {
    return URI.create("http://localhost:" + port);
  }
}
