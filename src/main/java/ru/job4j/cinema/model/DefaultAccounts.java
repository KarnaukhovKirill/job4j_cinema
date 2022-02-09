package ru.job4j.cinema.model;

public enum DefaultAccounts {

    ADMIN(0);

    private final int id;

    DefaultAccounts(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

