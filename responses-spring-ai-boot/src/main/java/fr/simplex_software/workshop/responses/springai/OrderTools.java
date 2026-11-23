package fr.simplex_software.workshop.responses.springai;

import fr.simplex_software.workshop.responses.api.*;
import org.springframework.ai.tool.annotation.*;
import org.springframework.stereotype.*;

@Component
public class OrderTools
{
  @Tool(name = "orderStatus", description = "Returns the status code of an order")
  public String orderStatus(@ToolParam(description = "the order id") String orderId)
  {
    return OrderStatuses.statusOf(orderId);
  }
}
