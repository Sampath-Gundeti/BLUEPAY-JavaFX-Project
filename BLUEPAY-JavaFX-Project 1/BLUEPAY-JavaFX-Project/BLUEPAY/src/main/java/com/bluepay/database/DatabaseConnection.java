package com.bluepay.database;

import java.nio.file.*;
import java.sql.*;

public final class DatabaseConnection {
    private static String url;
    private DatabaseConnection() {}
    public static void initialize() {
        try {
            Path dir = Path.of(System.getProperty("user.home"), ".bluepay");
            Files.createDirectories(dir);
            url = "jdbc:sqlite:" + dir.resolve("bluepay.db");
            try (Connection c = getConnection(); Statement s = c.createStatement()) {
                s.execute("PRAGMA foreign_keys=ON");
                s.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users(
                     id INTEGER PRIMARY KEY AUTOINCREMENT, full_name TEXT NOT NULL, mobile TEXT UNIQUE NOT NULL,
                     email TEXT NOT NULL, password_hash TEXT NOT NULL, password_salt TEXT NOT NULL,
                     upi_pin_hash TEXT NOT NULL, upi_pin_salt TEXT NOT NULL, created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP)
                    """);
                s.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS accounts(
                     id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, bank_name TEXT NOT NULL,
                     masked_number TEXT NOT NULL, holder_name TEXT NOT NULL, primary_account INTEGER DEFAULT 0,
                     FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)
                    """);
                s.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS transactions(
                     id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, txn_id TEXT UNIQUE NOT NULL,
                     recipient TEXT NOT NULL, upi_id TEXT NOT NULL, amount REAL NOT NULL, description TEXT,
                     category TEXT NOT NULL, status TEXT NOT NULL, created_at TEXT NOT NULL,
                     FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)
                    """);
                s.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS contacts(
                     id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, name TEXT NOT NULL,
                     upi_id TEXT NOT NULL, mobile TEXT, FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)
                    """);
                s.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS notifications(
                     id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, title TEXT NOT NULL,
                     message TEXT NOT NULL, created_at TEXT NOT NULL, is_read INTEGER DEFAULT 0,
                     FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)
                    """);
                s.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS payment_settings(
                     user_id INTEGER PRIMARY KEY, daily_limit REAL NOT NULL DEFAULT 10000,
                     wallet_balance REAL NOT NULL DEFAULT 25000, demo_upi_pin_hash TEXT NOT NULL,
                     demo_upi_pin_salt TEXT NOT NULL, notifications_enabled INTEGER DEFAULT 1,
                     FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)
                    """);
            }
        } catch (Exception e) { throw new RuntimeException("Could not initialize BLUEPAY database", e); }
    }
    public static Connection getConnection() throws SQLException {
        if (url == null) throw new SQLException("Database has not been initialized.");
        Connection c = DriverManager.getConnection(url);
        try (Statement s = c.createStatement()) { s.execute("PRAGMA foreign_keys=ON"); }
        return c;
    }
    public static void close() { }
}