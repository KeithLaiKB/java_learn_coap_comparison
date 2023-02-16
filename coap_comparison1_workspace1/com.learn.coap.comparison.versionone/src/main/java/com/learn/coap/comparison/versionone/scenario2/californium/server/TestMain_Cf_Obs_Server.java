package com.learn.coap.comparison.versionone.scenario2.californium.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.util.Date;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.Configuration.DefinitionsProvider;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.x509.SingleCertificateProvider;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author laipl
 *
 */
public class TestMain_Cf_Obs_Server {
	public static void main(String[] args) {

		int DEFAULT_PORT = 5684;
		//final Logger LOG = LoggerFactory.getLogger(TestMain_Cf_Obs_Server.class.getName());
		
		final String KEY_STORE_LOCATION = "mycerts/californium/server/my_own/mykeystore.jks";
		final char[] KEY_STORE_PASSWORD = "SksOneAdmin".toCharArray();
		final String TRUST_STORE_LOCATION = "mycerts/californium/server/my_own/mykeystore_truststore.jks";
		final char[] TRUST_STORE_PASSWORD = "StsOneAdmin".toCharArray();
		
		
		DTLSConnector dtlsConnector;
		
		try {	
			DefinitionsProvider DEFAULTS = new DefinitionsProvider() {

				@Override
				public void applyDefinitions(Configuration config) {
					config.set(DtlsConfig.DTLS_CONNECTION_ID_LENGTH, 6);
					config.set(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY, false);
				}

			};

			String myusr_path = System.getProperty("user.dir");
			
			SslContextUtil.Credentials serverCredentials = SslContextUtil.loadCredentials(myusr_path + "\\" + KEY_STORE_LOCATION, "mykeystorealias", KEY_STORE_PASSWORD, KEY_STORE_PASSWORD);
			Certificate[] trustedCertificates = SslContextUtil.loadTrustedCertificates(myusr_path + "\\" + TRUST_STORE_LOCATION, "mytruststorealias", TRUST_STORE_PASSWORD);
			
			Configuration configuration = Configuration.createWithFile(Configuration.DEFAULT_FILE, "DTLS example server", DEFAULTS);
			DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder(configuration);
		
			builder.setAddress(new InetSocketAddress(DEFAULT_PORT));
			
			builder.setCertificateIdentityProvider(new SingleCertificateProvider(serverCredentials.getPrivateKey(), serverCredentials.getCertificateChain(), CertificateType.RAW_PUBLIC_KEY));
			
			builder.setAdvancedCertificateVerifier(StaticNewAdvancedCertificateVerifier.builder()
					.setTrustedCertificates(trustedCertificates).setTrustAllRPKs().build());
			dtlsConnector = new DTLSConnector(builder.build());
		
			
			
			CoapEndpoint.Builder coapBuilder = new CoapEndpoint.Builder()
					.setConfiguration(configuration)
					.setConnector(dtlsConnector);
			CoapServer server = new CoapServer();
			Cf_ObserverResource myobResc1 = new Cf_ObserverResource("Resource1");
			myobResc1.setStatusUpdateMaxTimes(35);
			//--------------------------------------------------------------------------
			server.addEndpoint(coapBuilder.build());
			server.add(myobResc1);
			server.start(); // does all the magic
			//
			myobResc1.startResource();
			server.start();

			while (!myobResc1.isMyDone()) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		
			myobResc1.stopResource();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// destory 可以结束程序, 但是stop不可以
			//server.destroy();
			//server.stop();
			server.destroy();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (GeneralSecurityException e1) {
			e1.printStackTrace();
		}
	}
}