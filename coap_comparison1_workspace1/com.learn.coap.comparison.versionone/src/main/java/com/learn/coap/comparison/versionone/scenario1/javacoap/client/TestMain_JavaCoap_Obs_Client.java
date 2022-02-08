package com.learn.coap.comparison.versionone.scenario1.javacoap.client;


import java.io.IOException;

import java.net.InetSocketAddress;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.learn.coap.comparison.versionone.scenario1.californium.client.TestMain_Cf_Obs_Client;
import com.mbed.coap.client.CoapClient;
import com.mbed.coap.client.CoapClientBuilder;
import com.mbed.coap.client.ObservationListener;
import com.mbed.coap.exception.CoapException;
import com.mbed.coap.packet.CoapPacket;


public class TestMain_JavaCoap_Obs_Client {
	private int receivedMessageNum 					= 0;
	private int expectedReceivedMessageNum			= 30;
	
	private String clientSeq = null;
	
	
	public static void main(String[] args) {
		//new TestMain_JavaCoap_Obs_Client(args[0]).run();
		new TestMain_JavaCoap_Obs_Client().run();
    }
	
	public void run() {
		
		//String 	myuri1_hostaddr   				= "135.0.237.84";
		String 	myuri1_hostaddr   				= "localhost";
		int 	myuri1_port 	  				= 5656;
		String 	myuri1_path   					= "/hello_observer";

		// create client
		InetSocketAddress inetSocketAddr = new InetSocketAddress(myuri1_hostaddr,myuri1_port);
		CoapClient client=null;
		try {
			client = CoapClientBuilder.newBuilder(inetSocketAddr).build();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// observe
		CompletableFuture<CoapPacket> resp = null;
		try {
			resp = client.resource(myuri1_path).observe(new MyObservationListener());
			if(resp != null) {
				//用来获取 第一次得到的数据
				System.out.println(resp.get().getPayloadString().toString());
				receivedMessageNum = receivedMessageNum +1;
			}
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
		// 停留一段时间 让server继续运行
        while(receivedMessageNum < expectedReceivedMessageNum) {
        	try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        //
		client.close();
	}
	
	public void setClientSeq(String clientSeq) {
		this.clientSeq=clientSeq ;
	}
	public String getClientSeq() {
		return this.clientSeq;
	}
	
	/**
	 * ObservationListener
	 * ref: java-coap/coap-core/src/test/java/protocolTests/ObservationTest.java
	 * 
	 * @author laipl
	 *
	 */
    public class MyObservationListener implements ObservationListener {

        @Override
        public void onObservation(CoapPacket obsPacket) throws CoapException {
            System.out.println(obsPacket.getPayloadString());
            receivedMessageNum = receivedMessageNum +1;
        }

        @Override
        public void onTermination(CoapPacket obsPacket) throws CoapException {
        	System.out.println("term!!!!!!!"+obsPacket.getPayloadString());
        }
    }
}

