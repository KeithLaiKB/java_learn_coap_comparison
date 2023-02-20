package com.learn.coap.comparison.versionone.scenario2.javacoap.server;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.mbed.coap.exception.CoapException;
import com.mbed.coap.observe.SimpleObservableResource;
import com.mbed.coap.packet.BlockSize;
import com.mbed.coap.packet.Code;
import com.mbed.coap.server.CoapServer;
import com.mbed.coap.server.CoapServerBuilder;
import com.mbed.coap.transmission.SingleTimeout;
import com.mbed.coap.transport.InMemoryCoapTransport;
import com.mbed.coap.transport.javassl.CoapSerializer;

public class TestMain_JavaCoap_Obs_Server {
	
	
	
	
	public static void main(String[] args) {
		/*
		// ref:java-coap/coap-core/src/test/java/protocolTests/ObservationTest.java 
		CoapServer server = CoapServer.builder().transport(5683).build();			// create server
		JavaCoap_ObserverResource myobResc1 = new JavaCoap_ObserverResource(server);	// create resource
		myobResc1.setStatusUpdateMaxTimes(35);						// 因为我们想独立的设置次数, 而不想更改构造函数, 所以后面需要独立出来一个startMyResource 
		server.addRequestHandler("/Resource1", myobResc1);								// add resoucre
		
		
		myobResc1.startResource();
		try {
			server.start();																// start server
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		*/
		
		
		
		
		
		
		
		
		//
		//String 	myuri1_hostaddr   				= "localhost";
		//int 	myuri1_port 	  				= 5656;
		String 	myuri1_hostaddr   				= "192.168.239.137";
		//int 	myuri1_port 	  				= 5684;
		String 	myuri1_path   					= "/Resource1";
		
		
		String serverCaCrt_file					="s_cacert.crt";
		String serverCaCrt_file_dir				="/mycerts/javacoap/server/my_own/myca";
		String serverCaCrt_file_loc = null;
		
		String serverKey_file					="server_cert.jks";
		String serverKey_file_dir				="/mycerts/javacoap/server/my_own/mycerts";
		String serverKey_file_loc = null;
		
		String serverCrt_file					="server_cert.crt";
		String serverCrt_file_dir				="/mycerts/javacoap/server/my_own/mycerts";
		String serverCrt_file_loc = null;
		
		
		//--------------------------------------
		String myusr_path = System.getProperty("user.dir");
		serverCaCrt_file_loc 							= 	myusr_path	+ serverCaCrt_file_dir		+"/" + 	serverCaCrt_file;

		serverKey_file_loc								= 	myusr_path	+ serverKey_file_dir		+"/" + 	serverKey_file;

		serverCrt_file_loc								= 	myusr_path	+ serverCrt_file_dir		+"/" + 	serverCrt_file;

        
        X509Certificate serverCaCrt = null;


        //////////////////// file->FileInputStream->BufferedInputStream->X509Certificate //////////////////////////////////////
        FileInputStream fis= null;
        CertificateFactory cf = null;
        Certificate ca=null;
		try {
			cf = CertificateFactory.getInstance("X.509");
			// From https://www.washington.edu/itconnect/security/ca/load-der.crt
			fis = new FileInputStream(serverCaCrt_file_loc);
			InputStream caInput = new BufferedInputStream(fis);
			
			try {
				ca = cf.generateCertificate(caInput);
			} finally {
				caInput.close();
			}
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
        InputStream ksInputStream = null;
		try {
			fis = new FileInputStream(serverKey_file_loc);
			ksInputStream = new BufferedInputStream(fis);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		
		// Create a KeyStore containing our trusted CAs
		KeyStore keyStore=null;
		TrustManagerFactory tmf = null;
		try {
			// Create a KeyStore containing our trusted CAs
			keyStore = KeyStore.getInstance("JKS");
			
			keyStore.load(ksInputStream, "SksOneAdmin".toCharArray());
			keyStore.setCertificateEntry("ca", ca);

			// Create a TrustManager that trusts the CAs in our KeyStore
			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
			tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
			tmf.init(keyStore);
		} catch (KeyStoreException e1) {
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//try add尝试
		KeyManagerFactory kmf=null;
        try {
			kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(keyStore, "SksOneAdmin".toCharArray());
		} catch (NoSuchAlgorithmException e3) {
			e3.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
        
		
		
		
		// finally, create SSL socket factory
		SSLContext context=null;
		SSLSocketFactory mysocketFactory=null;
		try {
			//ref: https://datatracker.ietf.org/doc/html/rfc6347
			context = SSLContext.getInstance("TLSv1.3");
			
			context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new java.security.SecureRandom());
			
		} catch (NoSuchAlgorithmException e2) {
			e2.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		mysocketFactory = context.getSocketFactory();
				
        

		//ref: java-coap/coap-core/src/test/java/com/mbed/coap/transport/javassl/SingleConnectionSSLSocketServerTransport.java
		SingleConnectionSocketServerTransport serverTransport1=null;
		try {
			SSLServerSocket sslserversocket = (SSLServerSocket) context.getServerSocketFactory().createServerSocket(5684);
			sslserversocket.setEnabledCipherSuites(context.getServerSocketFactory().getSupportedCipherSuites());
			
			serverTransport1 = new SingleConnectionSocketServerTransport(sslserversocket, CoapSerializer.UDP);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		CoapServer server = CoapServer.builder().transport(serverTransport1).build();			// create server
		JavaCoap_ObserverResource myobResc1 = new JavaCoap_ObserverResource(server);	// create resource
		myobResc1.setStatusUpdateMaxTimes(35);						// 因为我们想独立的设置次数, 而不想更改构造函数, 所以后面需要独立出来一个startMyResource 
		server.addRequestHandler("/Resource1", myobResc1);		
		

		myobResc1.startResource();
		try {
			server.start();																// start server
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		
		
		
		while (!myobResc1.isMyDone()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		
		// 因为我们的resource用了 timer,
		// 所以我们 destroy 了server以后 , resource还是在运行的
		// in my opinion, we should apply a standard process
		// so we need to stop the resource
		myobResc1.stopResource();
		//
		// 再让Main函数 运行一段时间, 我们可以发现resource没有输出了, 也就意味着 确实结束了
		// 其实 这后面的可以不用, 只是用来判断resource是否结束了,
		// 如果resource 没关掉, 就可以 在这段时间内 发现有resource的输出
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// destroy server
		// because the resource use the timer
		server.stop();
	}

}
