package fr.simplex_software.workshop.responses.conformance;

import io.restassured.http.*;
import io.restassured.response.*;

import java.net.*;

import static io.restassured.RestAssured.*;

public abstract class AbstractResponsesIT
{
  protected abstract URI baseUri();

  protected ValidatableResponse chat(String body)
  {
    return given().baseUri(baseUri().toString()).contentType(ContentType.JSON).body(body)
      .when().post("/chat")
      .then();
  }

  protected static String question(String question)
  {
    return question(question, null, false);
  }

  protected static String question(String question, String previousResponseId, boolean webSearch)
  {
    var previous = previousResponseId == null ? "null" : "\"" + previousResponseId + "\"";
    return """
      {"question": "%s", "previousResponseId": %s, "webSearch": %s}
      """.formatted(question, previous, webSearch);
  }
}
