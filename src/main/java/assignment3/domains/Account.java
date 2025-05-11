package assignment3.domains;

import assignment3.events.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Account {
    private final Map<String, Double> balances = new HashMap<>();

    public static Account replay(List<Event> events) {
        Account account = new Account();

        for (Event event : events) {
            account.apply(event);
        }

        return account;
    }

    private void apply(Event event) {
        if (event instanceof FundsCredited fc) {
            balances.put(fc.userId, getBalance(fc.userId) + fc.amount);
        } else if (event instanceof FundsDebited fd) {
            balances.put(fd.userId, getBalance(fd.userId) - fd.amount);
        }
    }

    public double getBalance(String userId) {
        return balances.getOrDefault(userId, 0.0);
    }
}
