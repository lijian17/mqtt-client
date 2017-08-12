package net.dxs.mqtt_client.mqtt;

import java.net.URISyntaxException;

import net.dxs.mqtt_client.MQTTFutureActivity;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
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
public class MQTTCallback {
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
	private CallbackConnection connection;
	
	public MQTTCallback(Handler handler) {
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

			//----------------------------------------------------------------
			connection = mqtt.callbackConnection();
			connection.listener(new Listener() {
				
				@Override
				public void onPublish(UTF8Buffer topic, Buffer payload, Runnable ack) {
					Log.d(TAG, "Listener-onPublish-topic=" + topic + "-payload=" + new String(payload.toByteArray()));
					
					android.os.Message msg = handler.obtainMessage();
					msg.what = MQTTFutureActivity.HANDLER_RECEIVE;
					msg.obj = "Title:" + topic + "-context:" + new String(payload.toByteArray());
					handler.sendMessage(msg);
					
					// 您现在可以从主题处理收到的消息。
			        // 一旦进程执行ack runnable。
			        ack.run();
				}
				
				@Override
				public void onFailure(Throwable value) {
					Log.d(TAG, "Listener-onFailure");
//					connection.close(null); // 发生连接失败。	
					connection.disconnect(null);
				}
				
				@Override
				public void onDisconnected() {
					Log.d(TAG, "Listener-onDisconnected");
				}
				
				@Override
				public void onConnected() {
					Log.d(TAG, "Listener-onConnected");
				}
			});
			
			connection.connect(new Callback<Void>() {
				
				/**
				 * 一旦我们连接..
				 * 
				 * @param v
				 */
				@Override
				public void onSuccess(Void v) {
					// 订阅主题
					connection.subscribe(topics, new Callback<byte[]>() {
						
						@Override
						public void onSuccess(byte[] qoses) {
							// subcribe 请求的结果.			
							Log.d(TAG, "connect-onSuccess-subscribe-onSuccess-qoses=" + new String(qoses));
						}
						
						@Override
						public void onFailure(Throwable value) {
//							connection.close(null); // 订阅失败
							connection.disconnect(null);
						}
					});
				}
				
				@Override
				public void onFailure(Throwable value) {
					Log.d(TAG, "connect-onFailure");
//					result.failure(value); // 如果我们无法连接到服务器。					
				}
			});
			
		} catch (URISyntaxException e) {
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
		if (connection != null) {
			String topic = "mqtt_aaa";
			connection.publish(topic, msg.getBytes(), QoS.AT_LEAST_ONCE, false, new Callback<Void>() {

				@Override
				public void onFailure(Throwable value) {
					Log.d(TAG, "send-publish-onFailure");
//					connection.close(null); // 发布失败.
					connection.disconnect(null);
				}

				@Override
				public void onSuccess(Void v) {
					Log.d(TAG, "send-publish-onSuccess");
					// 发布操作成功完成.					
				}
			});
		} else {
			Log.d(TAG, "connection is null");
		}
	}
	
	public void disconnect(){
		// 断开..
        connection.disconnect(new Callback<Void>() {
            public void onSuccess(Void v) {
              // 一旦连接断开连接就调用.
            }
            public void onFailure(Throwable value) {
              // 断开连接永远不会失败.
            }
        });
		connection = null;
	}
}
