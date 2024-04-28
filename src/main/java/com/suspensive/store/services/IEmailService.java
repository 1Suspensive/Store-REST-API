package com.suspensive.store.services;

public interface IEmailService {
    void sendEmail(String toUser, String subject, String message);
}
