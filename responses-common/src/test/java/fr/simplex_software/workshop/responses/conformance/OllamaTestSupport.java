package fr.simplex_software.workshop.responses.conformance;

import org.testcontainers.ollama.*;
import org.testcontainers.utility.*;

public final class OllamaTestSupport
{
  private static final String IMAGE = System.getProperty("ollama.image", "ollama/ollama:latest");
  private static final String MODEL = System.getProperty("ollama.model", "qwen2.5:0.5b");

  private static final OllamaContainer OLLAMA = startWithModel();

  private OllamaTestSupport()
  {
  }

  public static String openAiBaseUrl()
  {
    return OLLAMA.getEndpoint() + "/v1";
  }

  public static String model()
  {
    return MODEL;
  }

  private static OllamaContainer startWithModel()
  {
    var ollama = new OllamaContainer(DockerImageName.parse(IMAGE));
    ollama.start();
    try
    {
      var pull = ollama.execInContainer("ollama", "pull", MODEL);
      if (pull.getExitCode() != 0)
        throw new IllegalStateException("Pulling model " + MODEL + " failed: " + pull.getStderr());
    }
    catch (Exception e)
    {
      throw new IllegalStateException("Could not pull model " + MODEL, e);
    }
    return ollama;
  }
}
