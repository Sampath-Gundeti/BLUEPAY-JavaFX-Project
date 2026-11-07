package com.bluepay.services;

import com.bluepay.database.DatabaseConnection;
import com.bluepay.models.*;
import com.bluepay.utils.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class PaymentService {
    private User user(){User u=SessionManager.getInstance().getUser();if(u==null)throw new IllegalStateException("Session expired. Please log in.");return u;}
    public double balance(){try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement("SELECT wallet_balance FROM payment_settings WHERE user_id=?")){p.setLong(1,user().id());try(ResultSet r=p.executeQuery()){return r.next()?r.getDouble(1):0;}}catch(SQLException e){throw new RuntimeException(e);}}
    public double dailySpent(){String day=LocalDate.now().toString();try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement("SELECT COALESCE(SUM(amount),0) FROM transactions WHERE user_id=? AND status='SUCCESS' AND substr(created_at,1,10)=?")){p.setLong(1,user().id());p.setString(2,day);try(ResultSet r=p.executeQuery()){return r.next()?r.getDouble(1):0;}}catch(SQLException e){throw new RuntimeException(e);}}
    public Transaction pay(String recipient,String upi,double amount,String note,String category,String pin){
        User u=user();if(recipient==null||recipient.isBlank())throw new IllegalArgumentException("Recipient name is required.");
        if(upi==null||!upi.matches("[A-Za-z0-9._-]+@[A-Za-z0-9.-]+"))throw new IllegalArgumentException("Enter a valid demo UPI ID.");
        if(!Double.isFinite(amount)||amount<=0||amount>10000)throw new IllegalArgumentException("Amount must be between ₹1 and ₹10,000.");
        String id="BP"+UUID.randomUUID().toString().replace("-","").substring(0,12).toUpperCase();
        String now=LocalDateTime.now().withNano(0).toString();
        try(Connection c=DatabaseConnection.getConnection()){
            c.setAutoCommit(false);
            try{
                String pinHash,salt;double bal,limit;
                try(PreparedStatement p=c.prepareStatement("SELECT wallet_balance,daily_limit,demo_upi_pin_hash,demo_upi_pin_salt FROM payment_settings WHERE user_id=?")){p.setLong(1,u.id());try(ResultSet r=p.executeQuery()){if(!r.next())throw new SQLException("Payment settings missing");bal=r.getDouble(1);limit=r.getDouble(2);pinHash=r.getString(3);salt=r.getString(4);}}
                if(!SecurityUtil.matches(pin,salt,pinHash))throw new IllegalArgumentException("Incorrect demo UPI PIN.");
                double spent;try(PreparedStatement p=c.prepareStatement("SELECT COALESCE(SUM(amount),0) FROM transactions WHERE user_id=? AND status='SUCCESS' AND substr(created_at,1,10)=?")){p.setLong(1,u.id());p.setString(2,LocalDate.now().toString());try(ResultSet r=p.executeQuery()){r.next();spent=r.getDouble(1);}}
                if(amount>bal)throw new IllegalArgumentException("Insufficient demo wallet balance.");
                if(spent+amount>limit)throw new IllegalArgumentException("Daily transaction limit exceeded.");
                try(PreparedStatement p=c.prepareStatement("UPDATE payment_settings SET wallet_balance=wallet_balance-? WHERE user_id=? AND wallet_balance>=?")){p.setDouble(1,amount);p.setLong(2,u.id());p.setDouble(3,amount);if(p.executeUpdate()!=1)throw new IllegalArgumentException("Insufficient balance.");}
                try(PreparedStatement p=c.prepareStatement("INSERT INTO transactions(user_id,txn_id,recipient,upi_id,amount,description,category,status,created_at) VALUES(?,?,?,?,?,?,?,?,?)")){
                    p.setLong(1,u.id());p.setString(2,id);p.setString(3,recipient);p.setString(4,upi);p.setDouble(5,amount);p.setString(6,note==null?"":note);p.setString(7,category==null?"TRANSFER":category);p.setString(8,"SUCCESS");p.setString(9,now);p.executeUpdate();}
                try(PreparedStatement p=c.prepareStatement("INSERT INTO notifications(user_id,title,message,created_at) VALUES(?,?,?,?)")){p.setLong(1,u.id());p.setString(2,"Payment successful");p.setString(3,"You paid ₹"+amount+" to "+recipient);p.setString(4,now);p.executeUpdate();}
                c.commit();return new Transaction(id,recipient,upi,amount,note,category,"SUCCESS",now);
            }catch(Exception e){try{c.rollback();}catch(SQLException ignored){} if(e instanceof RuntimeException re)throw re; throw new RuntimeException(e);}finally{c.setAutoCommit(true);}
        }catch(SQLException e){throw new RuntimeException("Payment could not be saved: "+e.getMessage(),e);}
    }
    public List<Transaction> history(String query,String status){
        List<Transaction> out=new ArrayList<>();String sql="SELECT txn_id,recipient,upi_id,amount,description,category,status,created_at FROM transactions WHERE user_id=? AND (?='ALL' OR status=?) AND (lower(recipient) LIKE ? OR lower(upi_id) LIKE ? OR lower(txn_id) LIKE ?) ORDER BY id DESC";
        try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement(sql)){String q="%"+(query==null?"":query.toLowerCase())+"%";p.setLong(1,user().id());p.setString(2,status==null?"ALL":status);p.setString(3,status==null?"ALL":status);p.setString(4,q);p.setString(5,q);p.setString(6,q);try(ResultSet r=p.executeQuery()){while(r.next())out.add(new Transaction(r.getString(1),r.getString(2),r.getString(3),r.getDouble(4),r.getString(5),r.getString(6),r.getString(7),r.getString(8)));}}catch(SQLException e){throw new RuntimeException(e);}return out;
    }
    public void addDemoFunds(double amount){if(amount<=0||amount>100000)throw new IllegalArgumentException("Enter an amount from ₹1 to ₹1,00,000.");try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement("UPDATE payment_settings SET wallet_balance=wallet_balance+? WHERE user_id=?")){p.setDouble(1,amount);p.setLong(2,user().id());p.executeUpdate();}catch(SQLException e){throw new RuntimeException(e);}}
    public void updatePin(String pin){if(pin==null||!pin.matches("\\d{4}"))throw new IllegalArgumentException("Demo PIN must be exactly 4 digits.");String salt=SecurityUtil.newSalt();try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement("UPDATE payment_settings SET demo_upi_pin_hash=?,demo_upi_pin_salt=? WHERE user_id=?")){p.setString(1,SecurityUtil.hash(pin,salt));p.setString(2,salt);p.setLong(3,user().id());p.executeUpdate();}catch(SQLException e){throw new RuntimeException(e);}}
}