package com.learn.coap.comparison.versionone.scenario2.javacoap.client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.mbed.coap.client.CoapClient;
import com.mbed.coap.client.CoapClientBuilder;
import com.mbed.coap.client.ObservationListener;
import com.mbed.coap.exception.CoapException;
import com.mbed.coap.packet.CoapPacket;
import com.mbed.coap.packet.MediaTypes;
import com.mbed.coap.server.CoapServer;
import com.mbed.coap.transport.InMemoryCoapTransport;
import com.mbed.coap.transport.javassl.CoapSerializer;
import com.mbed.coap.transport.javassl.SSLSocketClientTransport;
public class TestMain_RequestObserverOne_Modified {

	
	public static void main(String[] args) {


		String 	myuri1_hostaddr   				= "192.168.239.137";
		int 	myuri1_port 	  				= 5684;
		String 	myuri1_path   					= "/Resource1";
		

		
		//public String serverCaCrt_file					="server_cert.crt";
		String serverCaCrt_file					="s_cacert.crt";
		//public String serverCaCrt_file					="s_cacert.pem";
		//public String serverCaCrt_file_dir				="/mycerts/my_own/samecn";	//从这里就可以看出, 我如果用不正确的证书会出问题的
		String serverCaCrt_file_dir				="/mycerts/javacoap/client/other_own";
		String serverCaCrt_file_loc = null;
		
		
		
		//--------------------------------------
        
		String myusr_path = System.getProperty("user.dir");

		
		serverCaCrt_file_loc 							= 	myusr_path	+ serverCaCrt_file_dir		+"/" + 	serverCaCrt_file;
	         
        
        
        X509Certificate serverCaCrt = null;


        //////////////////// file->FileInputStream->BufferedInputStream->X509Certificate //////////////////////////////////////
        // ref: https://gist.github.com/erickok/7692592
        
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
				// System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
			} finally {
				caInput.close();
			}
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		
		// Create a KeyStore containing our trusted CAs
		String keyStoreType = KeyStore.getDefaultType();
		KeyStore keyStore=null;
		TrustManagerFactory tmf = null;
		try {
			// Create a KeyStore containing our trusted CAs
			keyStoreType = KeyStore.getDefaultType();
			keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(null, null);
			keyStore.setCertificateEntry("ca", ca);

			// Create a TrustManager that trusts the CAs in our KeyStore
			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
			tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
			tmf.init(keyStore);
		} catch (KeyStoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/*
		//try add尝试
		KeyManagerFactory kmf=null;
        try {
			kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(keyStore, "cksOneAdmin".toCharArray());
		} catch (NoSuchAlgorithmException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		
		
		// finally, create SSL socket factory
		SSLContext context=null;
		SSLSocketFactory mysocketFactory=null;
		try {
			//context = SSLContext.getInstance("SSL");
			//ref: https://datatracker.ietf.org/doc/html/rfc6347
			// This document updates
			// DTLS 1.0 to work with TLS version 1.2.
			context = SSLContext.getInstance("TLSv1.3");
			
			//context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			//context.init(null,tmf.getTrustManagers(), new java.security.SecureRandom());
			//context.init(null,tmf.getTrustManagers(), null);
			//
			//
			context.init(null, tmf.getTrustManagers(), new java.security.SecureRandom());
			//context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new java.security.SecureRandom());
			//
			
			
			
			
		} catch (NoSuchAlgorithmException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mysocketFactory = context.getSocketFactory();
				
        
		
		//--------------------------------------------------
		
		
		//java-coap/coap-core/src/test/java/com/mbed/coap/transport/javassl/SSLSocketClientTransportTest.java
        /*
         InetSocketAddress serverAdr = new InetSocketAddress("localhost", srv.getLocalSocketAddress().getPort());
        CoapClient client = CoapClientBuilder.clientFor(serverAdr,
                CoapServer.builder().transport(new SSLSocketClientTransport(serverAdr, clientSslContext.getSocketFactory(), CoapSerializer.UDP, false)).build().start()
        );*/
		
		/*
		//ref: java-coap/coap-core/src/test/java/protocolTests/ObservationTest.java
		InetSocketAddress inetSocketAddr = new InetSocketAddress(myuri1_hostaddr,myuri1_port);
		CoapClient client=null;
		try {
			client = CoapClientBuilder.newBuilder(inetSocketAddr).build();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		InetSocketAddress inetSocketAddr = new InetSocketAddress(myuri1_hostaddr,myuri1_port);
		//InetSocketAddress inetSocketAddr = new InetSocketAddress(myuri1_hostaddr,0);
		CoapClient client = null;
		try {
			client = CoapClientBuilder.clientFor(inetSocketAddr,
			        CoapServer.builder().transport(new SSLSocketClientTransport(inetSocketAddr, context.getSocketFactory(), CoapSerializer.UDP, false)).build().start()
			);
		} catch (IllegalStateException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//CoapClient client2 = new CoapClient(port1);
		/*
		CoapClient client = CoapClientBuilder.newBuilder(InMemoryCoapTransport.createAddress(5683))
                .transport(new InMemoryCoapTransport())
                .timeout(1000).build();
		*/

		
		CompletableFuture<CoapPacket> resp = null;
		try {
			resp = client.resource(myuri1_path).observe(new MyObservationListener());
			//
			if(resp != null) {
				//用来获取 第一次得到的数据
				System.out.println(resp.get().getPayloadString().toString());
			}
			//
		} catch (CoapException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		//---------------------------------------------
		// 因为 异步，是要等待回传的，等待是需要时间的，
		// 所以 我不能让程序那么快结束
		// 所以 我让你输入回车再结束，也就是说 你不输入回车，那么这个总main函数没走完
		// 从而 有时间 让client等到 传回来的 数据
		// 不然的话 在等待的过程中，总函数已经运行完了, 所以里面的这些变量啊 线程啊 也有可能没有了？
        System.out.println("enter to exit!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
				br.readLine(); 
		} 
		catch (IOException e) { }
		System.out.println("CANCELLATIONING");
		//resp.proactiveCancel();
		//resp.cancel(true);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("CANCELLATION FINISHED");
		client.close();
	
	}
	
	/**
	 * ObservationListener
	 * ref: java-coap/coap-core/src/test/java/protocolTests/ObservationTest.java
	 * 
	 * @author laipl
	 *
	 */
    public static class MyObservationListener implements ObservationListener {

        @Override
        public void onObservation(CoapPacket obsPacket) throws CoapException {
        	String mediaTypesTmp = MediaTypes.contentFormatToString(obsPacket.headers().getContentFormat());
            System.out.println("ADD!!!!!!!"+obsPacket.getPayloadString()+","+obsPacket.headers().getContentFormat()+mediaTypesTmp);
            
        }

        @Override
        public void onTermination(CoapPacket obsPacket) throws CoapException {
        	System.out.println("term!!!!!!!"+obsPacket.getPayloadString());
        }
    }
}

