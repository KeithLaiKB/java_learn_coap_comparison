package com.learn.coap.comparison.versionone.scenario1.californium.server;

import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.observe.ObserveRelation;
import org.eclipse.californium.core.observe.ObserveRelationContainer;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ResourceAttributes;
import org.eclipse.californium.core.server.resources.ResourceObserver;






public class Cf_ObserverResource  extends CoapResource {

		private Type messageType        	= Type.NON;		// messageType 	vs qos

		private int statusUpdate			=0;
		private int statusUpdateMaxTimes	=30;
		//
		private MyTimerTaskForUpdate myUpdateTask1 	= null;
		private Timer timer = null; 
		//
		private String content     	 				= "hello_my_world";
		//
	    public boolean resourceFinished 			= false;
	    //
		//
		public Cf_ObserverResource(String name) {
			super(name);
			//
			//----------------------------------------
			this.setObservable(true); 				// enable observing
			this.setObserveType(messageType); 			// configure the notification type to CONs, 如果不写这个默认的是 NON
			// 涉及到 https://tools.ietf.org/html/rfc6690#section-4 	(这讲了Linkformat 这么做的概念)
			// 和  https://tools.ietf.org/html/rfc6690#section-4.1
			// 其实就是设置好 application/link-format 
			this.getAttributes().setObservable(); // mark observable in the Link-Format (可以查 californium 的类LinkFormat)	
			//
			//----------------------------------------
			//
			// schedule a periodic update task, otherwise let events call changed()
			timer = new Timer();
			myUpdateTask1 = new MyTimerTaskForUpdate();
			//timer.schedule(myUpdateTask1,0, 500);		//500ms
		}
		


		/**
		 * 这里面 每一次changed 代表, 要去通知所有的client
		 * 则会调用handelGet
		 * 
		 * @author laipl
		 *
		 */
		private class MyTimerTaskForUpdate extends TimerTask {
			@Override
			public void run() {
				//System.out.println("UpdateTask-------name:"+Cf_ObserverResource.this.getName());
				//
				// .. periodic update of the resource
				// 为了保持 与Mqtt 测量的方式 相同, 当信息更新次数>statusUpdateMaxTimes-1时, 不再发送信息给 client
				if(statusUpdate<=statusUpdateMaxTimes-1) {
					//
					statusUpdate = statusUpdate+1;
					changed(); // notify all observers
				}
				else {
					resourceFinished = true;
				}
				// 类比于 mqtt 它每一次信息自己更新
			}
		}
		//
		//
		//--------------------------------------------------------------------------------
		//------------------------ handle get/ delete / put / post------------------------ 
		//
		//
		@Override
		public void handleGET(CoapExchange exchange) {
			//为了显示 好看
			/*
			System.out.println("--------------------------------------------------------------------");
			System.out.println("--------- server side get method start -----------------------------");
			//exchange.respond(content+":"+statusUpdate);
			exchange.respond( ResponseCode.CONTENT, content+":"+statusUpdate, MediaTypeRegistry.TEXT_PLAIN); // 如果不写ResponseCode也可以, 它默认 ResponseCode.CONTENT, 如果不写 MediaTypeRegistry 也可以, 它默认是 MediaTypeRegistry.TEXT_PLAIN 
			System.out.println("--------- server side get method end -------------------------------");
			System.out.println("--------------------------------------------------------------------");
			*/
			
			
			exchange.respond( ResponseCode.CONTENT, content+":"+statusUpdate, MediaTypeRegistry.TEXT_PLAIN); // 如果不写ResponseCode也可以, 它默认 ResponseCode.CONTENT, 如果不写 MediaTypeRegistry 也可以, 它默认是 MediaTypeRegistry.TEXT_PLAIN 
		}

		//--------------------------------------------------------------------------------
		//----------------------------------  my method ----------------------------------
		public void setStatusUpdateMaxTimes(int statusUpdateMaxTimes) {
			this.statusUpdateMaxTimes = statusUpdateMaxTimes;
		}
		
		//start resource timer
		public int startMyResource(){
			timer.schedule(myUpdateTask1,0, 500);
			return 1;
		}
		
		//把timer 停止了, 如果只是server.destory 是不会把这个 resource的 Timer结束的
		//所以我需要 自己设置一个方法来停止这个timer
		public int stopMyResource(){
			this.timer.cancel();
			return 1;
		}

	}