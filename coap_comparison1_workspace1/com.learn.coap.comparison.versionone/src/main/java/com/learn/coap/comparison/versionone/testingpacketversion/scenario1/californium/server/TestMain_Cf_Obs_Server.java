package com.learn.coap.comparison.versionone.testingpacketversion.scenario1.californium.server;

import org.eclipse.californium.core.CoapServer;

/**
 * 
 * @author laipl
 *
 */
public class TestMain_Cf_Obs_Server {
	public static void main(String[] args) {
		CoapServer server = new CoapServer(5683);

		Cf_ObserverResource myobResc1 = new Cf_ObserverResource("Resource1");		//new resource
		myobResc1.setStatusUpdateMaxTimes(50);
		server.add(myobResc1);
		
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
	}
}