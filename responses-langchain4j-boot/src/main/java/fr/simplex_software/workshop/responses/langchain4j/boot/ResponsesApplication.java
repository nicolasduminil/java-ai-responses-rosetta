package fr.simplex_software.workshop.responses.langchain4j.boot;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.context.properties.*;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ResponsesApplication
{
  public static void main(String[] args)
  {
    SpringApplication.run(ResponsesApplication.class, args);
  }
}
