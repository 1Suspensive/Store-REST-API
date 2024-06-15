package com.suspensive.store.services.interfaces;

public interface IEmailService {
    void sendEmail(String toUser, String subject, String message);
    void validateEmail(String email) throws Exception;
}
