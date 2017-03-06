package de.ronnyfriedland.nosql.mongodb.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.mail.BodyPart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BlobMimeMessageTextExtractor.class)
public class BlobMimeMessageTextExtractorTest {

    @Autowired
    private BlobMimeMessageTextExtractor subject;

    @Test
    public void testNullValue() {
        Assert.assertNull(subject.convert(null));
    }

    @Test
    public void testValidTextPlain() throws Exception {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(System.getProperties()));
        mimeMessage.setText("Hello world");
        mimeMessage.saveChanges();

        byte[] messageContent;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            mimeMessage.writeTo(bos);
            messageContent = bos.toByteArray();
        }

        Assert.assertEquals("Hello world", subject.convert(new ByteArrayInputStream(messageContent)));
    }

    @Test
    public void testValidTextMultipart() throws Exception {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(System.getProperties()));
        BodyPart part = new MimeBodyPart();
        part.setText("Hello world");
        mimeMessage.setContent(new MimeMultipart(part));
        mimeMessage.saveChanges();

        byte[] messageContent;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            mimeMessage.writeTo(bos);
            messageContent = bos.toByteArray();
        }

        Assert.assertEquals("Hello world", subject.convert(new ByteArrayInputStream(messageContent)));
    }

}
