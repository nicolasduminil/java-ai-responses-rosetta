package fr.simplex_software.workshop.responses.springai;

import fr.simplex_software.workshop.responses.conformance.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.web.server.*;

import java.net.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringAiOpenAiResponsesIT extends AbstractOpenAiResponsesIT
{
  @LocalServerPort
  private int port;

  @Override
  protected boolean supportsServerState()
  {
    return false;
  }

  @Override
  protected URI baseUri()
  {
    return URI.create("http://localhost:" + port);
  }
}
