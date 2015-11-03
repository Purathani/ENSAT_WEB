/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MailBean;

/**
 *
 * @author Anthony Stell
 */
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Level;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.event.*;
import java.net.*;
import java.util.*;

public class MailBean {

    //private static final Logger logger = Logger.getLogger(MailBean.class);
    private static final Logger logger = Logger.getLogger("/home/astell/mailbean.log");

    public MailBean() {
    }

    public void sendMail(HttpServletRequest request, HttpServletResponse response, String ensatId) throws IOException {

        //Configure the logger
        logger.setLevel(Level.DEBUG);

        //logger.info("---- MAILBEAN BEGINS ----");
        PrintWriter out = response.getWriter();

        try {

            Properties props = new Properties();

            //props.put("mail.smtp.host", "smtp.unimelb.edu.au");
            //props.put("mail.smtp.host", "mail-relay.gla.ac.uk");
            props.put("mail.smtp.host", "localhost");

            Session session1 = Session.getDefaultInstance(props, null);
            String s1 = "ensat-registry@unimelb.edu.au"; //sender (from)
            //String s1 = "ensat-registry@gla.ac.uk";
            String s2 = "w.arlt@bham.ac.uk d.m.oneil@bham.ac.uk i.bancos@bham.ac.uk"; //recipient (to) 
            String s5 = "astell@unimelb.edu.au felix.beuschlein@med.uni-muenchen.de "; //cc 
            String s3 = "EURINE-ACT Patient added to ENSAT Registry"; //subject
            String s4 = "Patient " + ensatId + " has been added to the ENSAT registry and flagged to be part of the EURINE-ACT Study."; //Text

            Message message = new MimeMessage(session1);

            message.setFrom(new InternetAddress(s1));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(s2, false));
            message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(s5, false));

            message.setSubject(s3);
            message.setText(s4);

            Transport.send(message);

            logger.info("EURINE-ACT study email message has been sent (" + ensatId + ")");
            logger.info("Email message object reference: " + message.toString() + ")");

        } catch (Exception ex) {
            logger.info("Error sending mail..." + ex);
        }

        //logger.info("---- MAILBEAN ENDS ----");
    }

    public void sendMail(HttpServletRequest request, HttpServletResponse response, String ensatId, String[] studies, String[] studyAddresses, String[] studyLabels) throws IOException {

        //Configure the logger
        logger.setLevel(Level.DEBUG);

        int studyNum = studies.length;
        PrintWriter out = response.getWriter();

        try {

            for (int i = 0; i < studyNum; i++) {

                String studyLabel = studyLabels[i];
                String studyAddressesIn = studyAddresses[i];
                studyAddressesIn = studyAddressesIn.trim();

                boolean sendMail = !studyAddressesIn.equals("");
                if (sendMail) {

                    logger.debug("Patient " + ensatId + " has been tagged for inclusion in " + studyLabel + " study");

                    Properties props = new Properties();

                    props.put("mail.smtp.host", "localhost");

                    Session session1 = Session.getDefaultInstance(props, null);
                    String s1 = "ensat-registry@unimelb.edu.au"; //sender (from)
                    //String s2 = "w.arlt@bham.ac.uk d.m.oneil@bham.ac.uk i.bancos@bham.ac.uk"; //recipient (to) 
                    String s2 = "" + studyAddressesIn + "";
                    String s5 = "astell@unimelb.edu.au felix.beuschlein@med.uni-muenchen.de "; //cc 
                    //String s3 = "EURINE-ACT Patient added to ENSAT Registry"; //subject
                    String s3 = "" + studyLabel + " Patient added to ENSAT Registry";
                    String s4 = "Patient " + ensatId + " has been added to the ENSAT registry and flagged to be part of the " + studyLabel + " Study."; //Text

                    Message message = new MimeMessage(session1);

                    message.setFrom(new InternetAddress(s1));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(s2, false));
                    message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(s5, false));

                    message.setSubject(s3);
                    message.setText(s4);

                    Transport.send(message);

                    logger.info("EURINE-ACT study email message has been sent (" + ensatId + ")");
                    logger.info("Email message object reference: " + message.toString() + ")");
                }

            }

        } catch (Exception ex) {
            logger.info("Error sending mail..." + ex);
        }
    }

    public void sendMetabolomicsEmail(HttpServletRequest request, HttpServletResponse response, String ensatId, String piEmail) throws IOException {

        //Configure the logger
        logger.setLevel(Level.DEBUG);

        PrintWriter out = response.getWriter();

        try {

            Properties props = new Properties();
            props.put("mail.smtp.host", "localhost");

            Session session1 = Session.getDefaultInstance(props, null);
            String s1 = "ensat-registry@unimelb.edu.au"; //sender (from)
            String s2 = "" + piEmail; //recipient (to) 
            String s5 = "astell@unimelb.edu.au i.bancos@bham.ac.uk w.arlt@bham.ac.uk"; //cc 
            String s3 = "Metabolomics form added to patient " + ensatId + ""; //subject

            //Email text
            String s4 = "This is a notification that your patient " + ensatId + " has had a new steroid metabolomics result report added to the ENSAT registry.";
            s4 += "Please review the steroid metabolomics form and associated comment.";
            s4 += "Do not hesitate to contact Irina Bancos (i.bancos@bham.ac.uk) and Wiebke Arlt (w.arlt@bham.ac.uk) with any questions.";

            Message message = new MimeMessage(session1);

            message.setFrom(new InternetAddress(s1));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(s2, false));
            message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(s5, false));

            message.setSubject(s3);
            message.setText(s4);

            Transport.send(message);

            logger.info("Metabolomics form update message has been sent (" + ensatId + ")");
            logger.info("Email message object reference: " + message.toString() + ")");

        } catch (Exception ex) {
            logger.info("Error sending mail..." + ex);
        }
    }

}
