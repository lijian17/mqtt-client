package net.dxs.mqtt_client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	private void init() {
		initView();
		initData();
	}

	private void initView() {
		((Button) findViewById(R.id.btn_blocking)).setOnClickListener(this);
		((Button) findViewById(R.id.btn_future)).setOnClickListener(this);
		((Button) findViewById(R.id.btn_callback)).setOnClickListener(this);
	}

	private void initData() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_blocking:
			startActivity(new Intent(this, MQTTBlockingActivity.class));
			break;
			
		case R.id.btn_future:
			startActivity(new Intent(this, MQTTFutureActivity.class));
			break;
			
		case R.id.btn_callback:
			startActivity(new Intent(this, MQTTCallbackActivity.class));
			break;

		default:
			break;
		}
	}
}
