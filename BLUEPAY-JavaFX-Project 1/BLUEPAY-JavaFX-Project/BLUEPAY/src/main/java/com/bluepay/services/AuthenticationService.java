package com.bluepay.services;

import com.bluepay.database.DatabaseConnection;
import com.bluepay.models.User;
import com.bluepay.utils.*;
import java.sql.*;

public class AuthenticationService {
    public void validateRegistration(String name, String mobile, String email, String password, String confirm) {
        name=name.trim(); mobile=mobile.trim(); email=email.trim();
        if (name.length()<2) throw new IllegalArgumentException("Enter your full name.");
        if (!mobile.matches("\\d{10}")) throw new IllegalArgumentException("Mobile number must contain 10 digits.");
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) throw new IllegalArgumentException("Enter a valid email.");
        if (password.length()<8) throw new IllegalArgumentException("Password must contain at least 8 characters.");
        if (!password.equals(confirm)) throw new IllegalArgumentException("Passwords do not match.");
        try (Connection c=DatabaseConnection.getConnection();
             PreparedStatement p=c.prepareStatement("SELECT 1 FROM users WHERE mobile=?")) {
            p.setString(1,mobile);
            try(ResultSet r=p.executeQuery()) {
                if(r.next()) throw new IllegalArgumentException("This mobile number is already registered. Please go back to login or use a different number.");
            }
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public String register(String name, String mobile, String email, String password, String confirm) {
        validateRegistration(name,mobile,email,password,confirm);
        name=name.trim(); mobile=mobile.trim(); email=email.trim();
        String salt=SecurityUtil.newSalt(), pinSalt=SecurityUtil.newSalt();
        try (Connection c=DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement p=c.prepareStatement("INSERT INTO users(full_name,mobile,email,password_hash,password_salt,upi_pin_hash,upi_pin_salt) VALUES(?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                p.setString(1,name);p.setString(2,mobile);p.setString(3,email);p.setString(4,SecurityUtil.hash(password,salt));p.setString(5,salt);
                p.setString(6,SecurityUtil.hash("1234",pinSalt));p.setString(7,pinSalt);p.executeUpdate();
                long id; try(ResultSet rs=p.getGeneratedKeys()){if(!rs.next())throw new SQLException("User ID unavailable");id=rs.getLong(1);}
                try(PreparedStatement a=c.prepareStatement("INSERT INTO accounts(user_id,bank_name,masked_number,holder_name,primary_account) VALUES(?,?,?,?,1)")){
                    a.setLong(1,id);a.setString(2,"BLUEPAY Demo Bank");a.setString(3,"XXXX XXXX 4821");a.setString(4,name);a.executeUpdate();}
                try(PreparedStatement s=c.prepareStatement("INSERT INTO payment_settings(user_id,daily_limit,wallet_balance,demo_upi_pin_hash,demo_upi_pin_salt) VALUES(?,10000,25000,?,?)")){
                    s.setLong(1,id);s.setString(2,SecurityUtil.hash("1234",pinSalt));s.setString(3,pinSalt);s.executeUpdate();}
                c.commit(); return "Registration successful. Demo UPI PIN: 1234";
            } catch(SQLException | RuntimeException e){
                try{c.rollback();}catch(SQLException rollbackError){e.addSuppressed(rollbackError);}
                throw e;
            } finally{c.setAutoCommit(true);}
        } catch(SQLException e){
            if(e.getMessage()!=null&&e.getMessage().toUpperCase().contains("UNIQUE"))
                throw new IllegalArgumentException("This mobile number is already registered. Please go back to login or use a different number.");
            throw new RuntimeException(e);
        }
    }
    public User login(String mobile,String password) {
        try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement("SELECT * FROM users WHERE mobile=?")){
            p.setString(1,mobile.trim());try(ResultSet r=p.executeQuery()){
                if(!r.next()||!SecurityUtil.matches(password,r.getString("password_salt"),r.getString("password_hash")))throw new IllegalArgumentException("Invalid mobile number or password.");
                User u=new User(r.getLong("id"),r.getString("full_name"),r.getString("mobile"),r.getString("email"));
                SessionManager.getInstance().setUser(u);return u;
            }
        }catch(SQLException e){throw new RuntimeException(e);}
    }
    public void changePassword(String oldPassword,String newPassword){
        User u=requireUser();if(newPassword.length()<8)throw new IllegalArgumentException("New password must be at least 8 characters.");
        try(Connection c=DatabaseConnection.getConnection();PreparedStatement q=c.prepareStatement("SELECT password_hash,password_salt FROM users WHERE id=?")){
            q.setLong(1,u.id());try(ResultSet r=q.executeQuery()){if(!r.next()||!SecurityUtil.matches(oldPassword,r.getString(2),r.getString(1)))throw new IllegalArgumentException("Current password is incorrect.");}
            String salt=SecurityUtil.newSalt();try(PreparedStatement p=c.prepareStatement("UPDATE users SET password_hash=?,password_salt=? WHERE id=?")){p.setString(1,SecurityUtil.hash(newPassword,salt));p.setString(2,salt);p.setLong(3,u.id());p.executeUpdate();}
        }catch(SQLException e){throw new RuntimeException(e);}
    }
    private User requireUser(){User u=SessionManager.getInstance().getUser();if(u==null)throw new IllegalStateException("Please log in.");return u;}
}