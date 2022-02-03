package ru.job4j.cinema.store;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.job4j.cinema.model.Account;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class DbStoreTest {
    private static Store store;

    @BeforeClass
    public static void init() {
        store = DbStore.instOf();
    }

    @Test
    public void whenAddAccount() {
        Account account = new Account(0, "username", "email@gmail.com", "+7900000000");
        store.delAllAccount();
        Collection<Account> rsl = store.findAllAccount();
        assertTrue(rsl.size() == 0);
        store.save(account);
        rsl = store.findAllAccount();
        assertTrue(rsl.size() == 1);
    }

    @Test
    public void whenUpdateAccount() {
        Account account = new Account(0, "Tony Ca***sh", "brilliant@cash.tommy", "79083333333");
        store.save(account);
        account.setUsername("Tony Cash");
        store.save(account);
        assertThat(store.findAccountById(account.getId()).getUsername(), is("Tony Cash"));
    }

    @Test
    public void whenDelAccount() {
        Account account = new Account(0, "Darth Vader", "black@star.com", "+79062004455");
        store.save(account);
        assertTrue(store.delAccount(account.getId()));
    }

    @Test
    public void whenFindAccountById() {
        Account account = new Account(0, "Cruella", "blackAndWhite@crue.com", "+78005567890");
        store.save(account);
        assertThat(store.findAccountById(account.getId()), is(account));
    }

    @Test
    public void whenFindAccountByEmail() {
        Account account = new Account(0, "Mahmood", "soldi@mahmood.it", "+79001234567");
        store.save(account);
        assertThat(store.findAccountByEmail(account.getEmail()), is(account));
    }

    @Test
    public void whenDelAllAccounts() {
        store.delAllAccount();
        Account firstAcc = new Account(0, "Bob Marley", "chill@heaven.chu", "79000000000");
        Account secondAcc = new Account(0, "Johny", "johny@mail.com", "+79061233232");
        store.save(firstAcc);
        store.save(secondAcc);
        List<Account> expected = List.of(firstAcc, secondAcc);
        assertThat(store.findAllAccount(), is(expected));
    }
}