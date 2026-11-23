package fr.simplex_software.workshop.responses.springai;

import fr.simplex_software.workshop.responses.conformance.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.web.server.*;
import org.springframework.test.context.*;

import java.net.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringAiLocalResponsesIT extends AbstractLocalResponsesIT
{
  @LocalServerPort
  private int port;

  @DynamicPropertySource
  static void ollama(DynamicPropertyRegistry registry)
  {
    registry.add("spring.ai.openai.responses.base-url", OllamaTestSupport::openAiBaseUrl);
    registry.add("spring.ai.openai.responses.model", OllamaTestSupport::model);
    registry.add("spring.ai.openai.api-key", () -> "ollama");
  }

  @Override
  protected URI baseUri()
  {
    return URI.create("http://localhost:" + port);
  }
}
