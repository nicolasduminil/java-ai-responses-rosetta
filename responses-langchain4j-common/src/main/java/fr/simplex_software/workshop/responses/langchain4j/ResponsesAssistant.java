package fr.simplex_software.workshop.responses.langchain4j;

import dev.langchain4j.model.chat.request.*;
import dev.langchain4j.service.*;

interface ResponsesAssistant
{
  Result<String> chat(@UserMessage String question, ChatRequestParameters parameters);
}
