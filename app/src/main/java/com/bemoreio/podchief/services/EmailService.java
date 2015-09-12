package com.bemoreio.podchief.services;

import com.bemoreio.podchief.mail.EmailSender;
import com.joanzapata.android.asyncservice.api.annotation.AsyncService;

/**
 * Created by Cody on 9/9/15.
 */
@AsyncService
public class EmailService {
    public Boolean send(String toEmail, String attachment) {

        EmailSender emailSender = new EmailSender();
        return emailSender.sendMailWithAttachment(toEmail, attachment);
    }
}
