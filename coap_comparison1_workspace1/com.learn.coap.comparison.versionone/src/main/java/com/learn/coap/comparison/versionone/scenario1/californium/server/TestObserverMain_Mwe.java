package com.learn.coap.comparison.versionone.scenario1.californium.server;

import java.util.Date;

import org.eclipse.californium.core.CoapServer;



/**
 *
 * @author laipl
 *
 */
public class TestObserverMain_Mwe  {
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	    String resourceName     = "hello_observer";			// resourceName 	vs topic
	    int serverPort			= 5656;						// server port 		vs broker port
	    //
		CoapServer server = new CoapServer(serverPort);
		Cf_ObserverResource myobResc1 = new Cf_ObserverResource(resourceName);
		myobResc1.setStatusUpdateMaxTimes(35);
		//------------------------operate server-------------------------------------
		server.add(myobResc1);
		myobResc1.startResource();
		server.start(); // does all the magic
		
		//----------------------------- give some time to run ------------------------
		// 因为它和main是不同线程的, 所以我要让我的main 等到 resource发布了我所需要测量的 数据报个数 
		// 才去stop resource
		// 然后
		// 才去destroy我们的server
		while(!myobResc1.isMyDone()) {
			// 停留一段时间 让server继续运行, 这里用 sleep 是为了减少loop的时间
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//---------------------------------------------------------------------------
		myobResc1.stopResource();

		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// destroy server
		// because the resource use the timer
		server.destroy();
	}

	
	


}