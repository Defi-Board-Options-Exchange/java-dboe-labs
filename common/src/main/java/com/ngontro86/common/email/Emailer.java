package com.ngontro86.common.email;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Collection;
import java.util.Properties;


public class Emailer {
    private Logger logger = LogManager.getLogger(Emailer.class);

    private String host, port, username, password;

    private Session session;
    private Transport transport;

    void establishSession() throws Exception {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", this.host);
        props.put("mail.smtp.port", this.port);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.user", this.username);
        props.put("mail.smtp.password", this.password);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtps.debug", "true");
        props.put("mail.smtp.ssl.trust", this.host);
        this.session = Session.getDefaultInstance(props, null);
    }

    void openTransport() throws Exception {
        transport = session.getTransport("smtp");
        transport.connect(host, username, password);
    }

    public void sendMessage(Collection<String> recipientList, String subject, String messageBody) {
        try {
            establishSession();
            MimeMessage message = new MimeMessage(this.session);
            message.setFrom(new InternetAddress(this.username));
            for (String to : recipientList) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            }
            message.setSubject(subject);
            message.setText(messageBody);
            message.setContent(messageBody, "text/html");
            openTransport();
            transport.sendMessage(message, message.getAllRecipients());
        } catch (Exception e) {
            logger.error("sendMessage got exception: ", e);
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    logger.error("Close Transport got exception: ", e);
                }
            }
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
