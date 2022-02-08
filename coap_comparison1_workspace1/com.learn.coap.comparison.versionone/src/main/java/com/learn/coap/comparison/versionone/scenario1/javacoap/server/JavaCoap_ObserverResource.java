package com.learn.coap.comparison.versionone.scenario1.javacoap.server;

import java.util.Timer;
import java.util.TimerTask;


import com.mbed.coap.CoapConstants;
import com.mbed.coap.exception.CoapCodeException;
import com.mbed.coap.exception.CoapException;
import com.mbed.coap.observe.AbstractObservableResource;
import com.mbed.coap.observe.NotificationDeliveryListener;
import com.mbed.coap.packet.Code;
import com.mbed.coap.packet.MediaTypes;
import com.mbed.coap.server.CoapExchange;
import com.mbed.coap.server.CoapServer;

public class JavaCoap_ObserverResource extends AbstractObservableResource{

	private int statusUpdate					= 0;
	private int statusUpdateMaxTimes			= 30;
	//
	private Timer timer 						= null;
	private MyTimerTaskForUpdate myUpdateTask1 	= null;
	//
	private String content     	 				= "hello_my_world";
	//
	public boolean resourceFinished 			= false;
    
	
	public JavaCoap_ObserverResource(CoapServer coapServer) {
		super(coapServer);
		// TODO Auto-generated constructor stub
		this.setConNotifications(false);					// configure the notification type to NONs, 如果不写这个默认的是 CON
		//
		timer = new Timer();								// schedule a periodic update task, otherwise let events call changed()
		myUpdateTask1 = new MyTimerTaskForUpdate();
		//timer.schedule(myUpdateTask1,0, 500);	//500ms
	}
	
	
	@Override
	public void get(CoapExchange exchange) throws CoapCodeException {
		//为了显示 好看
		/*
		System.out.println("--------------------------------------------------------------------");
		System.out.println("--------- server side get method start -----------------------------");
		exchange.setResponseBody(content+":"+statusUpdate);
        exchange.getResponseHeaders().setContentFormat(MediaTypes.CT_TEXT_PLAIN);
        exchange.setResponseCode(Code.C205_CONTENT);
        exchange.sendResponse();
		System.out.println("--------- server side get method end -------------------------------");
		System.out.println("--------------------------------------------------------------------");
		*/
		
		
		exchange.setResponseBody(content+":"+statusUpdate);
        exchange.getResponseHeaders().setContentFormat(MediaTypes.CT_TEXT_PLAIN);
        exchange.setResponseCode(Code.C205_CONTENT);
        exchange.sendResponse();
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
			System.out.println("UpdateTask-------name:"+JavaCoap_ObserverResource.this.getClass().getName());
			// 为了保持 与Mqtt 测量的方式 相同, 当信息更新次数>statusUpdateMaxTimes-1时, 不再发送信息给 client
			if(statusUpdate<=statusUpdateMaxTimes-1) {
				statusUpdate = statusUpdate+1;
				//System.out.println(content+":"+statusUpdate);
				try {
					notifyChange(new String(content+":"+statusUpdate).getBytes(CoapConstants.DEFAULT_CHARSET),MediaTypes.CT_TEXT_PLAIN);
				} catch (CoapException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
			else {
				resourceFinished = true;
			}
			// 类比于 mqtt 它每一次信息自己更新
		}
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
	public int stopMyResource(){
		this.timer.cancel();
		return 1;
	}

}

