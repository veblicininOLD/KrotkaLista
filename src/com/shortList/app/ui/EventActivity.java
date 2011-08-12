package com.shortList.app.ui;

import com.shortList.app.control.PaymentManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class EventActivity extends Activity {

	protected Button newPaymentButton;
	protected PaymentManager pm ;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event);

		newPaymentButton = (Button) findViewById(R.id.new_payment);
		newPaymentButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						PaymentActivity.class);
				// Bundle b = new Bundle();
				startActivityForResult(i, 0);
			}
		});
		pm = PaymentManager.getInstance(); 
		pm.init(getApplicationContext());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.event_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.participants:
				Intent myIntent = new Intent(getApplicationContext(),
						ParticipantsActivity.class);
				startActivity(myIntent); // TODO
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}
