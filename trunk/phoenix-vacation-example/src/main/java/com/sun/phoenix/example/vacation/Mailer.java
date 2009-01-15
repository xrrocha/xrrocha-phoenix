package com.sun.phoenix.example.vacation;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mailer {
    private String host = "127.0.0.1";
    private int port = 25;
    private String user;
    private String password;

    public void sendMail(String from, String subject, String body, String... to) {
        if (true) {
            System.out.println(from + " sends '" + subject + "' to " + to[0] + "\n" + body);
            return;
        }
        try {
            final Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.host", port);
            if (user != null) {
                props.put("mail.user", user);
                if (password != null) {
                    props.put("mail.password", password);
                }
            }
            final Session session = Session.getInstance(props, null);
            final Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            final InternetAddress[] addresses = new InternetAddress[to.length];
            for (int i = 0; i < to.length; i++) {
                addresses[i] = new InternetAddress(to[i]);
            }
            message.setRecipients(Message.RecipientType.TO, addresses);
            message.setSubject(subject);
            message.setContent(body, "text/plain");

            Transport.send(message);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error sending mail: " + e, e);
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
