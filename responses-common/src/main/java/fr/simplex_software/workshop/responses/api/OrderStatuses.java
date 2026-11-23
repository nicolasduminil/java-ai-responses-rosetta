package fr.simplex_software.workshop.responses.api;

import java.util.*;

public final class OrderStatuses
{
  private static final Map<String, String> STATUSES = Map.of("42", "QX7731");

  private OrderStatuses()
  {
  }

  public static String statusOf(String orderId)
  {
    return STATUSES.getOrDefault(orderId.strip(), "UNKNOWN");
  }
}
