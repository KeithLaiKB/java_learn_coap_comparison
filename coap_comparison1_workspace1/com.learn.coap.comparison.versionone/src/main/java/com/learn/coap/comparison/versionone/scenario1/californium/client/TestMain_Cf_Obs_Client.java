package com.learn.coap.comparison.versionone.scenario1.californium.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

/**
 * @author laipl
 *
 */
public class TestMain_Cf_Obs_Client {
	private static final int EXPECTED_NUMBER_OF_MESSAGES = 30;
	private int numberOfMessages = 0;

	public static void main(final String[] args) {
		new TestMain_Cf_Obs_Client().run();
	}

	private void run() {
		final CoapClient client = new CoapClient("coap://localhost:5656/Resource1");

		try {
			final CoapHandler myObserveHandler = new CoapHandler() {
				@Override
				public void onLoad(final CoapResponse response) {
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
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
			client.shutdown();
			try {
				Thread.sleep(10000);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}