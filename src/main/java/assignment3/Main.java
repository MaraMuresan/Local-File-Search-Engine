package assignment3;

import assignment3.commands.CommandHandler;
import assignment3.domains.Account;
import assignment3.domains.OrderBook;
import assignment3.events.Event;
import assignment3.events.OrderPlaced;
import assignment3.store.EventStore;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        EventStore store = new EventStore();
        CommandHandler handler = new CommandHandler(store);

        try {
            handler.depositFunds("mara", 1000);
            handler.depositFunds("ema", 0);
            handler.depositFunds("ioana", 500);
            handler.depositFunds("raluca", 200);
            handler.depositFunds("diana", 300);

            handler.placeOrder("s1", "ema", OrderPlaced.Type.SELL, 5, 90);
            handler.placeOrder("s2", "ioana", OrderPlaced.Type.SELL, 3, 100);
            handler.placeOrder("s3", "raluca", OrderPlaced.Type.SELL, 1, 100);

            handler.placeOrder("b1", "mara", OrderPlaced.Type.BUY, 5, 90);
            handler.placeOrder("b2", "mara", OrderPlaced.Type.BUY, 2, 100);
            handler.placeOrder("b3", "diana", OrderPlaced.Type.BUY, 1, 100);

            handler.executeTrade("b1", "s1", 5, 90, "mara", "ema");

            handler.cancelOrder("s2");

            handler.cancelOrder("b2");

            //handler.executeTrade("b3", "s3", 1, 100, "diana", "raluca");

            //try ordering with a duplicate id
            handler.placeOrder("b3", "mara", OrderPlaced.Type.BUY, 1, 200);

        } catch (IllegalStateException | IllegalArgumentException ex) {
            System.out.println("Command failed: " + ex.getMessage());
        }

        try {
            //try ordering with not enough money
            handler.placeOrder("b10", "diana", OrderPlaced.Type.BUY, 1, 600);
        } catch (IllegalStateException ex) {
            System.out.println("Order failed: " + ex.getMessage());
        }

        try {
            //try cancelling a non-existent order
            handler.cancelOrder("nonexistent_order");
        } catch (IllegalStateException ex) {
            System.out.println("Cancel failed: " + ex.getMessage());
        }

        try {
            //try executing a trade with inactive orders
            handler.executeTrade("b1", "s1", 5, 90, "mara", "ema");
        } catch (IllegalStateException ex) {
            System.out.println("Trade failed: " + ex.getMessage());
        }

        List<Event> events = store.getAllEvents();

        System.out.println("\nAll Initial Events:\n");
        for (Event event : events) {
            System.out.println(event.getClass().getSimpleName());
        }

        Account account = Account.replay(events);
        OrderBook orderBook = OrderBook.replay(events);

        System.out.println("\nUser Balances:\n");
        for (String userId : new String[]{"mara", "ema", "ioana", "raluca", "diana"}) {
            System.out.println(userId + ": $" + account.getBalance(userId));
        }

        System.out.println("\nActive Orders:\n");
        if (orderBook.getActiveOrders().isEmpty()) {
            System.out.println("(none)");
        } else {
            orderBook.getActiveOrders().forEach(order ->
                    System.out.println(order.orderId + ": " + order.type + " " + order.quantity + " items of $" + order.price + " each")
            );
        }
    }
}
