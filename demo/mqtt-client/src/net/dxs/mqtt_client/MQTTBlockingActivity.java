package net.dxs.mqtt_client;

import net.dxs.mqtt_client.mqtt.MQTTBlocking;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MQTTBlockingActivity extends Activity implements OnClickListener {

	public static final int HANDLER_SEND = 1000;
	public static final int HANDLER_RECEIVE = 1001;

	private EditText mEt_send;
	private TextView mTv_msg;
	private Button mBtn_send;
	private MQTTBlocking mMqtt;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_RECEIVE:
				mTv_msg.append(String.valueOf(msg.obj) + "\n");
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mqtt);
		init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mMqtt != null) {
			mMqtt.disconnect();
			mMqtt = null;
		}
	}

	private void init() {
		initView();
		initData();
	}

	private void initView() {
		mEt_send = (EditText) findViewById(R.id.et_send);
		mTv_msg = (TextView) findViewById(R.id.tv_msg);
		mBtn_send = (Button) findViewById(R.id.btn_send);
		mBtn_send.setOnClickListener(this);
	}

	private void initData() {
		mMqtt = new MQTTBlocking(handler);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:
			sendMsg();
			break;

		default:
			break;
		}
	}

	private void sendMsg() {
		String msg = mEt_send.getText().toString().trim();
		if (!TextUtils.isEmpty(msg)) {
			if (mMqtt != null) {
				mMqtt.send(msg);
				mTv_msg.append(msg + "\n");
			}
		} else {
			Toast.makeText(this, "发送内容为空", Toast.LENGTH_SHORT).show();
		}
	}
}
