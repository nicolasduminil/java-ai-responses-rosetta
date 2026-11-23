package fr.simplex_software.workshop.responses.langchain4j.quarkus;

import fr.simplex_software.workshop.responses.conformance.*;
import io.quarkus.test.common.*;
import io.quarkus.test.common.http.*;
import io.quarkus.test.junit.*;

import java.net.*;

@QuarkusTest
@WithTestResource(OllamaResponsesTestResource.class)
class QuarkusLocalResponsesIT extends AbstractLocalResponsesIT
{
  @TestHTTPResource
  URI applicationUri;

  @Override
  protected URI baseUri()
  {
    return applicationUri;
  }
}
