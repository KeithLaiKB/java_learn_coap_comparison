package com.learn.coap.comparison.versionone.scenario1.californium.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

/**
 * @author laipl
 *
 */
public class TestMain_Cf_Obs_Client {
	private int EXPECTED_NUMBER_OF_MESSAGES = 30;
	private int numberOfMessages = 0;

	public static void main(String[] args) {
		new TestMain_Cf_Obs_Client().run();
	}

	private void run() {
		CoapClient client = new CoapClient("coap://localhost:5656/Resource1");
		
		CoapHandler  myObserveHandler = new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				System.out.println(response.getResponseText());
				numberOfMessages = numberOfMessages + 1;
			}

			@Override
			public void onError() {
			}
		};	
		
		client.observe(myObserveHandler);
		
		while (numberOfMessages < EXPECTED_NUMBER_OF_MESSAGES) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		client.shutdown();
	}
}