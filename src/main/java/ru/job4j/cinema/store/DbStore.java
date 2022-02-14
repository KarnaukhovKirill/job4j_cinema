package ru.job4j.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.DefaultAccounts;
import ru.job4j.cinema.model.Ticket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

public class DbStore {
    public static final Logger LOG = LoggerFactory.getLogger(DbStore.class.getName());
    private final BasicDataSource pool = new BasicDataSource();

    public static DbStore instOf() {
        return Lazy.INST;
    }

    private DbStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(DbStore.class.getClassLoader()).getResourceAsStream("db.properties")
                )
        )) {
            cfg.load(io);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final DbStore INST = new DbStore();
    }


    public void saveAccount(Account account) {
        if (account.getId() == 0) {
            create(account);
        } else {
            update(account);
        }
    }

    private void create(Account account) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("INSERT INTO account(username, email, phone) VALUES (?, ?, ?)",
            PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPhone());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    account.setId(id.getInt("id"));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void update(Account account) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("UPDATE account SET username = ?, email = ?, phone = ? "
                                                                                                   + "WHERE id = ?",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPhone());
            ps.setInt(4, account.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public boolean deleteAccount(int id) {
        boolean rsl = false;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM account WHERE id = ?")) {
            ps.setInt(1, id);
            rsl = ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return rsl;
    }

    public void delAllAccounts() {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM account where id != ?")) {
            ps.setInt(1, DefaultAccounts.ADMIN.getId());
            ps.execute();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public Collection<Account> findAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM account")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    accounts.add(new Account(it.getInt("id"),
                            it.getString("username"),
                            it.getString("email"),
                            it.getString("phone")));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return accounts;
    }

    public Account findAccountById(int id) {
        try (Connection cn = pool.getConnection();
        PreparedStatement ps = cn.prepareStatement("SELECT * FROM account WHERE id = ?")
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return new Account(it.getInt("id"),
                            it.getString("username"),
                            it.getString("email"),
                            it.getString("phone"));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public Account findByEmail(String email) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM account WHERE email = ?")
        ) {
            ps.setString(1, email);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return new Account(it.getInt("id"),
                            it.getString("username"),
                            it.getString("email"),
                            it.getString("phone"));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public Account findAccountByPhone(String phone) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM account WHERE phone = ?")
        ) {
            ps.setString(1, phone);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return new Account(it.getInt("id"),
                            it.getString("username"),
                            it.getString("email"),
                            it.getString("phone"));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public boolean saveTicket(Ticket ticket) throws SQLIntegrityConstraintViolationException {
        boolean rsl;
        if (ticket.getId() == 0) {
            rsl = create(ticket);
        } else {
            rsl = update(ticket);
        }
        return rsl;
    }

    private boolean create(Ticket ticket) throws SQLIntegrityConstraintViolationException {
        boolean rsl = false;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO ticket(session_id, row_ticket, cell_ticket, available, account_id)"
                             + " VALUES (?, ?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ticket.getSessionId());
            ps.setInt(2, ticket.getRow());
            ps.setInt(3, ticket.getCell());
            ps.setBoolean(4, ticket.isAvailable());
            ps.setInt(5, ticket.getAccountId());
            rsl = ps.executeUpdate() > 0;
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    ticket.setId(id.getInt("id"));
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw e;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return rsl;
    }

    private boolean update(Ticket ticket) {
        boolean rsl = false;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("UPDATE ticket "
                             + "SET session_id = ?, row_ticket = ?, cell_ticket = ?, available = ?, account_id = ? "
                             + "WHERE id = ?",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ticket.getSessionId());
            ps.setInt(2, ticket.getRow());
            ps.setInt(3, ticket.getCell());
            ps.setBoolean(4, ticket.isAvailable());
            ps.setInt(5, ticket.getAccountId());
            ps.setInt(6, ticket.getId());
            rsl = ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return rsl;
    }

    public boolean deleteTicket(int id) {
        boolean rsl = false;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM ticket WHERE id = ?")) {
            ps.setInt(1, id);
            rsl = ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return rsl;
    }

    public void delAllTickets() {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM ticket")) {
            ps.execute();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public Collection<Ticket> findAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM ticket "
                                            + "ORDER BY row_ticket, cell_ticket")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    tickets.add(new Ticket(
                            it.getInt("id"),
                            it.getInt("session_id"),
                            it.getInt("row_ticket"),
                            it.getInt("cell_ticket"),
                            it.getBoolean("available"),
                            it.getInt("account_id")
                    ));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return tickets;
    }

    public Ticket findTicketById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM ticket WHERE id = ?")
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return new Ticket(it.getInt("id"),
                            it.getInt("session_id"),
                            it.getInt("row_ticket"),
                            it.getInt("cell_ticket"),
                            it.getBoolean("available"),
                            it.getInt("account_id"));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public void wipeDb() {
        try (Connection cn = pool.getConnection();
             PreparedStatement statement = cn.prepareStatement(
                "drop table if exists account cascade; "
                        + "create table if not exists account(id SERIAL PRIMARY KEY,\n"
                        + "  username varchar not null,\n"
                        + "  email varchar not null unique,\n"
                        + "  phone varchar not null unique); "

                        + "drop table if exists ticket;"
                        + "CREATE TABLE IF NOT EXISTS ticket (\n"
                        + "id SERIAL PRIMARY KEY,\n"
                        + "session_id int not null,\n"
                        + "row_ticket int,\n"
                        + "cell_ticket int,\n"
                        + "available boolean default true,\n"
                        + "account_id int not null,\n"
                        + "constraint account_id_fk foreign key (account_id) references account(id),"
                        + "constraint unique_ticket UNIQUE (session_id, row_ticket, cell_ticket));"
        )) {
            statement.execute();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
