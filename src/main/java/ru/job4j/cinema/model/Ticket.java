package ru.job4j.cinema.model;

import java.util.Objects;

public class Ticket {
    private int id;
    private int session_id;
    private int row;
    private int cell;
    private boolean available;
    private int accountId;

    public Ticket(int id, int session_id, int row, int cell, boolean available, int accountId) {
        this.id = id;
        this.session_id = session_id;
        this.row = row;
        this.cell = cell;
        this.available = available;
        this.accountId = accountId;
    }

    public Ticket(int session_id, int row, int cell, boolean available, int accountId) {
        this.session_id = session_id;
        this.row = row;
        this.cell = cell;
        this.available = available;
        this.accountId = accountId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSession_id() {
        return session_id;
    }

    public void setSession_id(int session_id) {
        this.session_id = session_id;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCell() {
        return cell;
    }

    public void setCell(int cell) {
        this.cell = cell;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Ticket ticket = (Ticket) o;
        return id == ticket.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
