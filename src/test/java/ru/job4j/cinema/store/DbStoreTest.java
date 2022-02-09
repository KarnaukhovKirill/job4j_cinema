package ru.job4j.cinema.store;

import org.junit.*;
import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.Ticket;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class DbStoreTest {
    private static DbStore store;


    @BeforeClass
    public static void init() {
        store = DbStore.instOf();
    }

    @After
    public void wipeDb() throws SQLException {
        store.wipeDb();
    }

    @Test
    public void whenAddAccount() {
        Account account = new Account(0, "username", "email@gmail.com", "+7900000000");
        store.delAllAccounts();
        Collection<Account> rsl = store.findAllAccounts();
        assertTrue(rsl.size() == 0);
        store.saveAccount(account);
        rsl = store.findAllAccounts();
        assertTrue(rsl.size() == 1);
    }

    @Test
    public void whenUpdateAccount() {
        Account account = new Account(0, "Tony Ca***sh", "brilliant@cash.tommy", "79083333333");
        store.saveAccount(account);
        account.setUsername("Tony Cash");
        store.saveAccount(account);
        assertThat(store.findAccountById(account.getId()).getUsername(), is("Tony Cash"));
    }

    @Test
    public void whenDelAccount() {
        Account account = new Account(0, "Darth Vader", "black@star.com", "+79062004455");
        store.saveAccount(account);
        assertTrue(store.deleteAccount(account.getId()));
    }

    @Test
    public void whenFindAccountById() {
        Account account = new Account(0, "Cruella", "blackAndWhite@crue.com", "+78005567890");
        store.saveAccount(account);
        assertThat(store.findAccountById(account.getId()), is(account));
    }

    @Test
    public void whenFindAccountByEmail() {
        Account account = new Account(0, "Mahmood", "soldi@mahmood.it", "+79001234567");
        store.saveAccount(account);
        assertThat(store.findByEmail(account.getEmail()), is(account));
    }

    @Test
    public void whenFindAccountByPhone() {
        Account account = new Account(0, "Sansara", "sansara@job.ru", "123");
        store.saveAccount(account);
        assertThat(store.findAccountByPhone("123").getUsername(), is("Sansara"));
    }

    @Test
    public void whenDelAllAccounts() {
        store.delAllAccounts();
        Account firstAcc = new Account(0, "Bob Marley", "chill@heaven.chu", "79000000000");
        Account secondAcc = new Account(0, "Johny", "johny@mail.com", "+79061233232");
        store.saveAccount(firstAcc);
        store.saveAccount(secondAcc);
        List<Account> expected = List.of(firstAcc, secondAcc);
        assertThat(store.findAllAccounts(), is(expected));
    }

    @Test
    public void whenSaveTicket() {
        Account account = new Account(0, "new account", "mail", "1");
        store.saveAccount(account);
        Ticket ticket = new Ticket(0,1, 1, 1, true, account.getId());
        store.saveTicket(ticket);
        var rsl = store.findAllTickets();
        assertThat(store.findTicketById(ticket.getId()), is(ticket));
    }

    @Test
    public void whenUpdateTicket() {
        Account account = new Account(0, "new account", "mail", "1");
        store.saveAccount(account);
        Ticket ticket = new Ticket(0,1, 1, 2, true, account.getId());
        store.saveTicket(ticket);
        ticket.setAvailable(false);
        store.saveTicket(ticket);
        assertThat(store.findTicketById(ticket.getId()).isAvailable(), is(false));
    }

    @Test
    public void whenDeleteTicket() {
        Account account = new Account(0, "new account", "mail", "1");
        store.saveAccount(account);
        Ticket ticket = new Ticket(0,1, 1, 3, true, account.getId());
        store.saveTicket(ticket);
        assertTrue(store.deleteTicket(ticket.getId()));
    }

    @Test
    public void whenDeleteAll() {
        Account account = new Account(0, "new account", "mail", "1");
        store.saveAccount(account);
        Ticket ticket2 = new Ticket(0,1, 1, 6, true, account.getId());
        Ticket ticket = new Ticket(0,1, 1, 5, true, account.getId());
        store.saveTicket(ticket);
        store.saveTicket(ticket2);
        assertThat(store.findAllTickets().size(), is(2));
        store.delAllTickets();
        assertThat(store.findAllTickets().size(), is(0));
    }

    @Test
    public void testFindAll() {
        Account account = new Account(0, "new account", "mail", "1");
        store.saveAccount(account);
        Ticket ticket = new Ticket(0,0, 1, 7, true, account.getId());
        Ticket ticket1 = new Ticket(0,1, 2, 1, true, account.getId());
        store.saveTicket(ticket);
        store.saveTicket(ticket1);
        var excepted = List.of(ticket, ticket1);
        var rsl = store.findAllTickets();
        assertThat(rsl, is(excepted));
    }

    @Test
    public void testFindById() {
        Account account = new Account(0, "new account", "mail", "1");
        store.saveAccount(account);
        Ticket ticket = new Ticket(0,1, 2, 2, true, account.getId());
        store.saveTicket(ticket);
        assertThat(store.findTicketById(ticket.getId()), is(ticket));
    }

}