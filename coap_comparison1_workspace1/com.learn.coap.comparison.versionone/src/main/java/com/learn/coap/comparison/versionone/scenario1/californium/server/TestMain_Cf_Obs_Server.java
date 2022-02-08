package com.learn.coap.comparison.versionone.scenario1.californium.server;

import java.util.Date;
import java.util.Map;

import org.eclipse.californium.core.CoapServer;

import com.learn.coap.comparison.versionone.IMyRunMainTest;
import com.learn.coap.comparison.versionone.myutil.MyTimeUtil;



/**
 * 
 * 
 * <p>
 * 							description:																</br>	
 * &emsp;						MWE means minimal working example										</br>
 * &emsp;						MWE 意思就是  简化的例子														</br>
 * &emsp;						for testing the observer												</br>
 * 																										</br>
 * 
 * </p>
 *
 *
 * @author laipl
 *
 */
public class TestMain_Cf_Obs_Server{
	static Cf_ObserverResource myobResc1 = null;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int statusUpdateMaxTimes = 30;
		
	    String resourceName     = "hello_observer";			// resourceName 	vs topic
	    String brokerAddress  	= "127.0.0.1";				// broker address
	    int serverPort			= 5656;						// server port 		vs broker port
	    String clientId     	= "JavaSample_sender";		// client Id
	    String content     	 	= "你好";

		CoapServer server = new CoapServer(serverPort);							// create server
		myobResc1 = new Cf_ObserverResource(resourceName);	// create resource
		myobResc1.setStatusUpdateMaxTimes(statusUpdateMaxTimes);				// 因为我们想独立的设置次数, 而不想更改构造函数, 所以后面需要独立出来一个startMyResource 
		//------------------------operate server-------------------------------------
		server.add(myobResc1);													// add resource
		
		//myobResc1.startMyResource();											// start resource												
		server.start(); 														// start server

		while(myobResc1.resourceFinished==false) {
			try {
				Thread.sleep(200);						// 停留一段时间 让server继续运行, 这里用 sleep 是为了减少loop的时间
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//---------------------------------------------------------------------------
		// 因为我们的resource用了 timer,
		// 所以我们 destroy 了server以后 , resource还是在运行的
		// in my opinion, we should apply a standard process
		// so we need to stop the resource
		myobResc1.stopMyResource();
		//
		// 再让Main函数 运行一段时间, 我们可以发现resource没有输出了, 也就意味着 确实结束了
		// 其实 这后面的可以不用, 只是用来判断resource是否结束了,
		// 如果resource 没关掉, 就可以 在这段时间内 发现有resource的输出
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// destroy server, because the resource use the timer
		server.destroy();
		//server.stop();
	}



	
	public static void startResource() {
		myobResc1.startMyResource();
	}


}