package assignment3.domains;

import assignment3.events.*;
import java.util.*;

public class OrderBook {
    private final Map<String, OrderPlaced> activeOrders = new HashMap<>();

    public static OrderBook replay(List<Event> events) {
        OrderBook book = new OrderBook();

        for (Event event : events) {
            book.apply(event);
        }

        return book;
    }

    private void apply(Event event) {
        if (event instanceof OrderPlaced placed) {
            activeOrders.put(placed.orderId, placed);
        } else if (event instanceof OrderCancelled cancelled) {
            activeOrders.remove(cancelled.orderId);
        } else if (event instanceof TradeExecuted trade) {
            activeOrders.remove(trade.buyOrderId);
            activeOrders.remove(trade.sellOrderId);
        }
    }

    public OrderPlaced getOrder(String orderId) {
        return activeOrders.get(orderId);
    }

    public Collection<OrderPlaced> getActiveOrders() {
        return activeOrders.values();
    }

    public boolean isOrderActive(String orderId) {
        return activeOrders.containsKey(orderId);
    }
}
