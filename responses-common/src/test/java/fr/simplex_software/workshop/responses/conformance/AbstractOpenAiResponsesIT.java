package fr.simplex_software.workshop.responses.conformance;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;

import static org.hamcrest.Matchers.*;

public abstract class AbstractOpenAiResponsesIT extends AbstractResponsesIT
{
  private static final String KEY = "OPENAI_API_KEY";

  protected abstract boolean supportsServerState();

  @Test
  @EnabledIfEnvironmentVariable(named = KEY, matches = ".+")
  @DisplayName("recalls an earlier turn through previous_response_id")
  protected void recallsAnEarlierTurnThroughPreviousResponseId()
  {
    Assumptions.assumeTrue(supportsServerState(), "this stack cannot send previous_response_id");
    var first = chat(question("My name is Aurora. Remember it."))
      .statusCode(200)
      .extract().path("responseId");

    chat(question("What is my name? Answer with the name only.", (String) first, false))
      .statusCode(200)
      .body("answer", containsStringIgnoringCase("aurora"));
  }

  @Test
  @EnabledIfEnvironmentVariable(named = KEY, matches = ".+")
  @DisplayName("searches the web with the hosted web_search tool")
  protected void searchesTheWebWithTheHostedTool()
  {
    chat(question("Search the web: what is the domain name of the Open Responses specification website?",
      null, true))
      .statusCode(200)
      .body("answer", containsStringIgnoringCase("openresponses.org"));
  }
}
