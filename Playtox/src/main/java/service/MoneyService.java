package service;

import model.Account;

public interface MoneyService {

    void transferMoney(Account from, Account to, int amount);

}
