package com.learn.coap.comparison.versionone.scenario1.californium.server;

import org.eclipse.californium.core.CoapServer;

/**
 * 
 * @author laipl
 *
 */
public class TestMain_Cf_Obs_Server {
	public static void main(final String[] args) {
		CoapServer server = new CoapServer(5656);

		Cf_ObserverResource myobResc1 = new Cf_ObserverResource("hello_observer");
		myobResc1.setStatusUpdateMaxTimes(35);
		server.add(myobResc1);
		myobResc1.startResource();

		/*
		final Cf_ObserverResource myobResc2 = new Cf_ObserverResource("Resource2");
		myobResc2.setStatusUpdateMaxTimes(30);
		server.add(myobResc2);
		myobResc2.startResource();
*/
		server.start();
		/*
		while(myobResc1.getStatusUpdate()>=1) {
			try {
				Thread.sleep(300);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

		while (!myobResc1.isDone() || !myobResc2.isDone()) {
			try {
				Thread.sleep(200);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}*/
		while (!myobResc1.isMyDone()) {
			try {
				Thread.sleep(200);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

		myobResc1.stopResource();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// destory 可以结束程序, 但是stop不可以
		server.destroy();
	}
}