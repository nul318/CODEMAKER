package com.example.codemaker;


import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class notification_test extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.noti_test);

		CharSequence s = "전달 받은 값은 ";

		int id = 0;
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			s = "error";
		} else {
			id = extras.getInt("notificationId");
		}
		TextView t = (TextView) findViewById(R.id.test);
		s = s + " " + id;
		t.setText(s);
		NotificationManager nm =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		//노티피케이션 제거
		nm.cancel(id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
