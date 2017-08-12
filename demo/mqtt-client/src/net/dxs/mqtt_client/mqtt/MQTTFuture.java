package net.dxs.mqtt_client.mqtt;

import java.net.URISyntaxException;

import net.dxs.mqtt_client.MQTTFutureActivity;

import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import android.os.Handler;
import android.util.Log;

/**
 * 采用Future模式 订阅主题
 * 
 * @author lijian
 * @date 2017-8-6 上午11:05:41
 */
public class MQTTFuture {
	private static final String TAG = "MQTTFuture";
	
//	private final static String CONNECTION_STRING = "tcp://192.168.1.104:1883";
	private final static String CONNECTION_STRING = "tcp://192.168.1.101:61613";
	private final static short KEEP_ALIVE = 30;// 低耗网络，但是又需要及时获取数据，心跳30s
	private final static String CLIENT_ID = "client";

	public static Topic[] topics = { new Topic("mqtt_aaa", QoS.EXACTLY_ONCE),
			new Topic("mqtt_bbb", QoS.AT_LEAST_ONCE),
			new Topic("mqtt_ccc", QoS.AT_MOST_ONCE) };

	public final static long RECONNECTION_ATTEMPT_MAX = 6;
	public final static long RECONNECTION_DELAY = 2000;

	public final static int SEND_BUFFER_SIZE = 2 * 1024 * 1024;// 发送最大缓冲为2M

	private Handler handler;
	private MQTT mqtt;
	private FutureConnection connection;
	
	public MQTTFuture(Handler handler) {
		this.handler = handler;
		new Thread(new Runnable() {
			@Override
			public void run() {
				init();
			}
		}).start();
	}

	private void init() {
		mqtt = new MQTT();
		try {
			// 设置mqtt broker的ip和端口
			mqtt.setHost(CONNECTION_STRING);
			// 连接前清空会话信息
			mqtt.setCleanSession(true);
			// 设置重新连接的次数
			mqtt.setReconnectAttemptsMax(6);
			// 设置重连的间隔时间
			mqtt.setReconnectDelay(2000);
			// 设置心跳时间
			mqtt.setKeepAlive(KEEP_ALIVE);
			// 设置缓冲的大小
			mqtt.setSendBufferSize(SEND_BUFFER_SIZE);
			// 设置客户端id
			mqtt.setClientId(CLIENT_ID);
			
			mqtt.setUserName("admin");
			mqtt.setPassword("password");

			connection = mqtt.futureConnection();
			connection.connect();
			connection.subscribe(topics);
			while (true) {
				Future<Message> futrueMessage = connection.receive();
				Message message = futrueMessage.await();
				
				android.os.Message msg = handler.obtainMessage();
				msg.what = MQTTFutureActivity.HANDLER_RECEIVE;
				msg.obj = "Title:" + message.getTopic() + "-context:" + new String(message.getPayload());
				handler.sendMessage(msg);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}
	
	/**
	 * 发送
	 * 
	 * @param msg 要发送的内容
	 */
	public void send(String msg){
		if (connection != null && connection.isConnected()) {
			String topic = "mqtt_aaa";
			connection.publish(topic, msg.getBytes(), QoS.AT_LEAST_ONCE, false);
		} else {
			Log.d(TAG, "connection is null");
		}
	}
	
	public void disconnect(){
		connection.disconnect();
		connection = null;
	}
}
