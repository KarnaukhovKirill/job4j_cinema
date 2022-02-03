package ru.job4j.cinema.store;

import ru.job4j.cinema.model.Account;

import java.util.Collection;

public interface Store {
    void save(Account account);
    boolean delAccount(int id);
    void delAllAccount();
    Collection<Account> findAllAccount();
    Account findAccountById(int id);
    Account findAccountByEmail(String email);
}
