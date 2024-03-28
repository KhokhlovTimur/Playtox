package service;

import lombok.extern.slf4j.Slf4j;
import model.Account;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class MoneyTransferService implements MoneyService {

    private final AtomicInteger transactionsCount = new AtomicInteger(0);
    private final Map<String, Lock> accounts = new ConcurrentHashMap<>();

    @Override
    public void transferMoney(Account from, Account to, int amount) {
        accounts.putIfAbsent(from.getId(), new ReentrantLock());
        accounts.putIfAbsent(to.getId(), new ReentrantLock());

        checkShutdownCondition();

        Lock lockTo = accounts.get(to.getId());
        Lock lockFrom = accounts.get(from.getId());

        try {
            lockTo.lock();
            lockFrom.lock();

            if (from.getMoney() >= amount) {

                from.setMoney(from.getMoney() - amount);
                to.setMoney(to.getMoney() + amount);
                transactionsCount.incrementAndGet();

                log.info(String.format("Money %s from account %s (%s) has been sent to account %s (%s) (transaction %s)",
                        amount, from.getId(), from.getMoney(), to.getId(), to.getMoney(), transactionsCount.get()));
            } else {
                log.error(String.format("There are not enough money in the account with id %s, money - %s, amount = %s",
                        from.getId(), from.getMoney(), amount));
            }
        } finally {
            lockTo.unlock();
            lockFrom.unlock();
        }
    }

    private void checkShutdownCondition() {
        if (transactionsCount.get() >= 30) {
            log.info("30 transactions completed");
            System.exit(0);
        }
    }

}
