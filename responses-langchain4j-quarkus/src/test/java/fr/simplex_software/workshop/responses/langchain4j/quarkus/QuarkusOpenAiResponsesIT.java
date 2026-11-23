package fr.simplex_software.workshop.responses.langchain4j.quarkus;

import fr.simplex_software.workshop.responses.conformance.*;
import io.quarkus.test.common.http.*;
import io.quarkus.test.junit.*;

import java.net.*;

@QuarkusTest
class QuarkusOpenAiResponsesIT extends AbstractOpenAiResponsesIT
{
  @TestHTTPResource
  URI applicationUri;

  @Override
  protected boolean supportsServerState()
  {
    return true;
  }

  @Override
  protected URI baseUri()
  {
    return applicationUri;
  }
}
