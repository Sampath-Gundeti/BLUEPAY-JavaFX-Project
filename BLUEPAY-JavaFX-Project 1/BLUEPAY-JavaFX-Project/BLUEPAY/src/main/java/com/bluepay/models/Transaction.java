package com.bluepay.models;
public record Transaction(String txnId, String recipient, String upiId, double amount,
                          String description, String category, String status, String createdAt) {}