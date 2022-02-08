package com.learn.coap.comparison.versionone.scenario1.californium.client;


import java.util.Scanner;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;

/**
 * 
 * 
 * <p>
 * 							description:																			</br>	
 * &emsp;						client to observe																	</br>
 * &emsp;						in this demo you don't have too much selection operation such as delete				</br>
 * &emsp;						before shutting down, it does not use cancel subscription here						</br>
 * 
 * 							ref:																					</br>	
 * &emsp;						californium/demo-apps/cf-plugtest-client/src/main/java/org/eclipse/californium/plugtests/PlugtestClient.java  	</br>	
 *  																												</br>
 * 
 * </p>
 * @author laipl
 *
 */
public class TestMain_Cf_Obs_Client {
	private int receivedMessageNum 					= 0;
	private int expectedReceivedMessageNum			= 30;

	public static void main(String[] args) {
		new TestMain_Cf_Obs_Client().run();
    }

	private void run() {
		//
    	String myuri1 	     					= "coap://localhost:5656/hello_observer";
    	CoapObserveRelation coapObRelation1		= null;
    	CoapHandler myObserveHandler 			= null;
    	//
    	// new client
    	CoapClient client = new CoapClient(myuri1);
        // set handler
        try {
			//
        	// set handler for observer method, because observe method needs asynchronous operation
			myObserveHandler = new CoapHandler() {

	            @Override
	            public void onLoad(CoapResponse response) {
	            	System.out.println(response.getResponseText());
	            	receivedMessageNum = receivedMessageNum +1;
	            }

	            @Override
	            public void onError() {
	            }
	        };
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // observe
        coapObRelation1 = client.observe(myObserveHandler);
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
		//------------------- close --------------------------
		// cancel subscription
		// coapObRelation1.proactiveCancel();
		// shutdown client
        client.shutdown();
	}

}