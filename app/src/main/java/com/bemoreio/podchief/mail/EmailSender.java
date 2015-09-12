package com.bemoreio.podchief.mail;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailSender extends javax.mail.Authenticator {

    private static final String TAG = "com.witopia.securemyemail.mail.PgpMailSender";

//    private EmailSettings emailSettings;

    private String mailhost;// = "smtp.gmail.com";
    private String user;
    private String password;
    private Session session;

    static {
        Security.addProvider(new JSSEProvider());
    }

    public EmailSender()
    {
        this.user = "cdillon@bemoreio.com";
        this.password = "monkey5$";
        this.mailhost = GmailSettings.OUTGOING_SERVER_HOST;

        Properties props = new Properties();

        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);

        session = Session.getInstance(props, this);
    }

    private void setSecurityType(Properties props)
    {
        props.put("mail.smtp.timeout", "5000");

        //User needs to supply username/password
        props.put("mail.smtp.auth", "true");

        //Connection should issue "STARTTLS" command on handshake
        props.put("mail.smtp.starttls.enable", "true");

        //Connection is assumed to be SSL
        props.setProperty("mail.smtp.ssl.enable", "true");
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

//    public void testConnection() throws javax.mail.NoSuchProviderException, MessagingException
//    {
//        Transport transport = session.getTransport("smtps");
//
//        transport.connect(this.emailSettings.getOutgoingServerHost(), this.emailSettings.getOutgoingServerPort(), this.emailSettings.getOutgoingUsername(), password);
//        transport.close();
//    }

    public Message getMessageWithAttachment(String toEmail, String filepath) throws UnsupportedEncodingException, MessagingException
    {
        MimeMessage message = new MimeMessage(session);

        InternetAddress from = new InternetAddress();
        from.setAddress(user);

        String personal = "POD Chief";
        from.setPersonal(personal);

        message.setFrom(from);
        message.setSender(from);
        message.setSubject("POD Chief");

        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();

        // Fill the message
        messageBodyPart.setText("This is message body");

        // Create a multipar message
        Multipart multipart = new MimeMultipart();

        // Set text message part
        multipart.addBodyPart(messageBodyPart);

        // Part two is attachment
        messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filepath);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filepath);
        multipart.addBodyPart(messageBodyPart);


        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822");

        // Send the complete message parts
        message.setContent(multipart);

        Address[] toRecipients = new Address[] { new InternetAddress(toEmail)};
        message.setRecipients(Message.RecipientType.TO, toRecipients);

        return message;
    }

    public Boolean sendMailWithAttachment(String toEmail, String filePath)
    {
        Boolean result = true;

        try {
            Message message = this.getMessageWithAttachment(toEmail, filePath);
            Transport.send(message);
        }
        catch (Exception e) {
            result = false;
            e.printStackTrace();
            Log.e("PODChief", "Error Send Email: " + e.getMessage());
        }

        return result;
    }

    public class ByteArrayDataSource implements DataSource
    {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;

        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException
        {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}
