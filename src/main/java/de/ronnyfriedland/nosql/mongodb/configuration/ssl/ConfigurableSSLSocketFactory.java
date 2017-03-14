package de.ronnyfriedland.nosql.mongodb.configuration.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.annotation.PostConstruct;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigurableSSLSocketFactory extends SSLSocketFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurableSSLSocketFactory.class);

    @Value("#{systemProperties['javax.net.ssl.keyStore']}")
    private String keyStorePath;

    @Value("#{systemProperties['javax.net.ssl.keyStorePassword']}")
    private char[] keystorePassword;

    @Value("#{systemProperties['javax.net.ssl.trustStore']}")
    private String truststorePath;

    @Value("#{systemProperties['javax.net.ssl.trustStorePassword']}")
    private char[] truststorePassword;

    private String[] tlsProtocolVersions;
    private String[] cipherSuites;

    private SSLSocketFactory delegate;

    @PostConstruct
    private void createSocketFactory() throws GeneralSecurityException, IOException {
        try {
            // load keystore
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(keyStorePath), keystorePassword);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keystore, keystorePassword);

            // load truststore
            KeyStore truststore = KeyStore.getInstance("JKS");
            keystore.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(truststorePath),
                    truststorePassword);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(truststore);

            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), SecureRandom.getInstance("SHA1PRNG"));

            delegate = sslcontext.getSocketFactory();
        } catch (Exception e) {
            LOG.warn("Error creating custom ssl context - using default one.", e);
            delegate = SSLContext.getDefault().getSocketFactory();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Socket createSocket() throws IOException {
        return configureSocket(delegate.createSocket());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Socket createSocket(final Socket arg0, final String arg1, final int arg2, final boolean arg3)
            throws IOException {
        return configureSocket(delegate.createSocket(arg0, arg1, arg2, arg3));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getDefaultCipherSuites() {
        return tlsProtocolVersions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getSupportedCipherSuites() {
        return cipherSuites;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Socket createSocket(final String arg0, final int arg1) throws IOException, UnknownHostException {
        return configureSocket(delegate.createSocket(arg0, arg1));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Socket createSocket(final InetAddress arg0, final int arg1) throws IOException {
        return configureSocket(delegate.createSocket(arg0, arg1));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Socket createSocket(final String arg0, final int arg1, final InetAddress arg2, final int arg3)
            throws IOException, UnknownHostException {
        return configureSocket(delegate.createSocket(arg0, arg1, arg2, arg3));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Socket createSocket(final InetAddress arg0, final int arg1, final InetAddress arg2, final int arg3)
            throws IOException {
        return configureSocket(delegate.createSocket(arg0, arg1, arg2, arg3));
    }

    private Socket configureSocket(final Socket socket) {
        if (socket instanceof SSLSocket) {
            SSLSocket sslSocket = ((SSLSocket) socket);
            if (null == tlsProtocolVersions) {
                tlsProtocolVersions = sslSocket.getEnabledProtocols();
                LOG.warn("Using default tls protocol version : "
                        + ToStringBuilder.reflectionToString(tlsProtocolVersions));
            }
            if (null == cipherSuites) {
                cipherSuites = sslSocket.getEnabledCipherSuites();
                LOG.warn("Using default cipherSuites : " + ToStringBuilder.reflectionToString(cipherSuites));
            }
            sslSocket.setEnabledProtocols(tlsProtocolVersions);
            sslSocket.setEnabledCipherSuites(cipherSuites);
        }
        return socket;
    }

    public void setTlsProtocolVersions(final String[] tlsProtocolVersions) {
        this.tlsProtocolVersions = tlsProtocolVersions;
    }

    public void setCipherSuites(final String[] cipherSuites) {
        this.cipherSuites = cipherSuites;
    }
}