package fr.simplex_software.workshop.responses.langchain4j.boot;

import org.springframework.boot.context.properties.*;

@ConfigurationProperties("responses.openai")
public record ResponsesModelProperties(String baseUrl, String apiKey, String model, Double temperature)
{
}
