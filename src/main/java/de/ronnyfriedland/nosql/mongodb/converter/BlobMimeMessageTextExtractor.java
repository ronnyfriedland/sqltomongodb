package de.ronnyfriedland.nosql.mongodb.converter;

import java.io.IOException;
import java.io.InputStream;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlobMimeMessageTextExtractor implements Converter<InputStream, String> {

    private static final Logger LOG = LoggerFactory.getLogger(BlobMimeMessageTextExtractor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String convert(final InputStream source) {
        try {
            MimeMessage mimeMessage = new MimeMessage(Session.getInstance(System.getProperties()), source);

            StringBuilder c = new StringBuilder();
            if (mimeMessage.getContentType().matches("text/.*")) {
                c.append(mimeMessage.getContent());
            }

            Object content = mimeMessage.getContent();
            if (content instanceof Multipart) {
                Multipart multiPart = (Multipart) content;
                int count = multiPart.getCount();
                for (int i = 0; i < count; i++) {
                    BodyPart part = multiPart.getBodyPart(i);
                    if (part.getContentType().matches("text/.*")) {
                        c.append((String) part.getContent());
                    }
                }
            }

            return c.toString();
        } catch (MessagingException | IOException e) {
            LOG.error("Error extracting text content of mimemessage", e);
            return null;
        }
    }

}
