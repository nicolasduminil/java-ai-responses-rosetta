package fr.simplex_software.workshop.responses.conformance;

import org.junit.jupiter.api.*;

import static org.hamcrest.Matchers.*;

public abstract class AbstractLocalResponsesIT extends AbstractResponsesIT
{
  @Test
  @DisplayName("answers a factual question and returns a response id")
  protected void answersAFactualQuestion()
  {
    chat(question("What is the capital of France? Answer with the city name only."))
      .statusCode(200)
      .body("answer", containsStringIgnoringCase("paris"))
      .body("responseId", not(emptyOrNullString()));
  }

  @Test
  @DisplayName("calls a local function tool")
  protected void callsALocalFunctionTool()
  {
    chat(question("What is the status of order 42? Use the orderStatus tool and quote the status code exactly."))
      .statusCode(200)
      .body("answer", containsStringIgnoringCase("QX7731"));
  }

  @Test
  @DisplayName("keeps no conversation state on a stateless backend")
  protected void keepsNoConversationStateOnAStatelessBackend()
  {
    var first = chat(question("My name is Aurora. Remember it."))
      .statusCode(200)
      .extract().path("responseId");

    chat(question("What is my name? Answer with the name only.", (String) first, false))
      .statusCode(200)
      .body("answer", not(containsStringIgnoringCase("aurora")));
  }

  @Test
  @DisplayName("rejects a blank question")
  protected void rejectsABlankQuestion()
  {
    chat(question("   ")).statusCode(400);
  }

  @Test
  @DisplayName("rejects a malformed payload")
  protected void rejectsAMalformedPayload()
  {
    chat("{ this is not json").statusCode(400);
  }
}
