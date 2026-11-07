package com.bluepay.utils;
import com.bluepay.models.User;
public final class SessionManager {
    private static final SessionManager INSTANCE = new SessionManager();
    private User user;
    private SessionManager() {}
    public static SessionManager getInstance() { return INSTANCE; }
    public void setUser(User user) { this.user = user; }
    public User getUser() { return user; }
    public boolean isAuthenticated() { return user != null; }
    public void clear() { user = null; }
}