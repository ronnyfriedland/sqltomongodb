package de.ronnyfriedland.nosql.mongodb.configuration.ssl;

import java.net.Socket;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = de.ronnyfriedland.nosql.mongodb.configuration.ssl.ConfigurableSSLSocketFactory.class)
public class ConfigurableSSLSocketFactoryTest {

    @Autowired
    private ConfigurableSSLSocketFactory subject;

    @Test
    public void testWithSslSettings() throws Exception {
        subject.setTlsProtocolVersions(new String[] { "TLSv1.2" });
        subject.setCipherSuites(new String[] { "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384" });

        Socket socket = subject.createSocket();
        Assert.assertNotNull(socket);
        Assert.assertTrue(socket instanceof SSLSocket);

        SSLSocket sslSocket = (SSLSocket) socket;
        Assert.assertArrayEquals(new String[] { "TLSv1.2" }, sslSocket.getEnabledProtocols());
        Assert.assertArrayEquals(new String[] { "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384" },
                sslSocket.getEnabledCipherSuites());
    }

    @Test
    public void testWithoutSslSettings() throws Exception {
        subject.setTlsProtocolVersions(null);
        subject.setCipherSuites(null);

        Socket socket = subject.createSocket();
        Assert.assertNotNull(socket);
        Assert.assertTrue(socket instanceof SSLSocket);

        SSLSocket sslSocket = (SSLSocket) socket;
        Assert.assertArrayEquals(SSLContext.getDefault().getDefaultSSLParameters().getProtocols(),
                sslSocket.getEnabledProtocols());
        Assert.assertArrayEquals(SSLContext.getDefault().getDefaultSSLParameters().getCipherSuites(),
                sslSocket.getEnabledCipherSuites());
    }

}
