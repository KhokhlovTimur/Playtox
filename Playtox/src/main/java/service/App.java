package service;

import model.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App implements ExecutingService {

    private final MoneyService moneyService;
    private final int threadsCount;
    private final int accountsCount;
    private final int maxTransferAmount;

    public App(MoneyService moneyService, int threadsCount, int accountsCount, int maxTransferAmount) {
        this.moneyService = moneyService;
        this.threadsCount = threadsCount;
        this.accountsCount = accountsCount;
        this.maxTransferAmount = maxTransferAmount;
    }

    @Override
    public void start() {
        ExecutorService executorService = Executors.newFixedThreadPool(threadsCount);
        List<Account> accounts = prepareData(accountsCount);

        int accNum = 0;
        for (int i = 0; i < threadsCount; i++, accNum += 2) {
            int finalAccNum = accNum;

            executorService.submit(() -> {
                        Random random = new Random();
                        try {
                            while (true) {
                                int sleepTime = random.nextInt(1001) + 1000;
                                Thread.sleep(sleepTime);
                                moneyService.transferMoney(accounts.get(finalAccNum), accounts.get(finalAccNum + 1),
                                        random.nextInt(maxTransferAmount) + 1);
                            }
                        } catch (InterruptedException ignored) {

                        }
                    }
            );
        }
    }

    private List<Account> prepareData(int accountsCount) {
        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < accountsCount; i++) {
            accounts.add(Account.builder()
                    .id("acc" + i)
                    .money(10000)
                    .build());
        }
        return accounts;
    }

    private static class TransferRunnable implements Runnable {
        @Override
        public void run() {

        }
    }

}

