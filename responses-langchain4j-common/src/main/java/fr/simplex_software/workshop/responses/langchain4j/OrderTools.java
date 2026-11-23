package fr.simplex_software.workshop.responses.langchain4j;

import dev.langchain4j.agent.tool.*;
import fr.simplex_software.workshop.responses.api.*;

public class OrderTools
{
  @Tool(name = "orderStatus", value = "Returns the status code of an order")
  public String orderStatus(@P("the order id") String orderId)
  {
    return OrderStatuses.statusOf(orderId);
  }
}
