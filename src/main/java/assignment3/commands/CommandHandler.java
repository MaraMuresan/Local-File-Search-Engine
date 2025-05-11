package assignment3.commands;

import assignment3.domains.Account;
import assignment3.domains.OrderBook;
import assignment3.events.*;
import assignment3.store.EventStore;

public class CommandHandler {
    private final EventStore eventStore;

    public CommandHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public void placeOrder(String orderId, String userId, OrderPlaced.Type type, int quantity, double price) {

        if (quantity <= 0 || price <= 0) {
            throw new IllegalArgumentException("Quantity and price must be greater than zero");
        }

        OrderBook orderBook = OrderBook.replay(eventStore.getAllEvents());
        if (orderBook.isOrderActive(orderId)) {
            throw new IllegalStateException("Order ID is already in use and active");
        }

        if (type == OrderPlaced.Type.BUY) {
            double cost = quantity * price;

            Account account = Account.replay(eventStore.getAllEvents());
            double balance = account.getBalance(userId);
            if (balance < cost) {
                throw new IllegalStateException("Insufficient funds to place BUY order");
            }

            withdrawFunds(userId, cost);
        }

        eventStore.append(new OrderPlaced(orderId, userId, type, quantity, price));
    }

    public void cancelOrder(String orderId) {

        OrderBook orderBook = OrderBook.replay(eventStore.getAllEvents());

        if (!orderBook.isOrderActive(orderId)) {
            throw new IllegalStateException("Cannot cancel: order does not exist");
        }

        eventStore.append(new OrderCancelled(orderId));
    }

    public void executeTrade(String buyOrderId, String sellOrderId, int quantity, double price, String buyerId, String sellerId) {

        OrderBook orderBook = OrderBook.replay(eventStore.getAllEvents());

        if (!orderBook.isOrderActive(buyOrderId) || !orderBook.isOrderActive(sellOrderId)) {
            throw new IllegalStateException("Cannot execute trade: one or both orders are no longer active");
        }

        eventStore.append(new TradeExecuted(buyOrderId, sellOrderId, quantity, price));

        double amount = quantity * price;
        depositFunds(sellerId, amount);
    }

    public void withdrawFunds(String userId, double amount) {
        eventStore.append(new FundsDebited(userId, amount));
    }

    public void depositFunds(String userId, double amount) {
        eventStore.append(new FundsCredited(userId, amount));
    }
}
