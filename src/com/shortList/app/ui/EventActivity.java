package com.shortList.app.ui;

import com.shortList.app.control.PaymentManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class EventActivity extends Activity {

	private static final String LOG_TAG = "EventActivity";

	protected Button newPaymentButton;
	protected TextView suggestedPerson;
	protected Button showPaymentsButton;
	
	protected PaymentManager pm ;

	private Button accountEventButton;

	private void showCalculations(){
		Toast toast =  Toast.makeText(this, "aaa", Toast.LENGTH_LONG); 
		toast.show();
	} 

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event);

		accountEventButton = (Button) findViewById(R.id.account_event);
		accountEventButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {  
				showCalculations();
			}
		});
		
		  
		showPaymentsButton = (Button) findViewById(R.id.show_payments_btn);
		showPaymentsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { 
				startActivity(new Intent(getApplicationContext(),
						PaymentsListActivity.class));
			}
		});
		
		
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
		
		suggestedPerson = (TextView) findViewById(R.id.suggested_person);
		if (pm.getSuggestedPerson() != null)
			suggestedPerson.setText(pm.getSuggestedPerson().getName());
		else
			suggestedPerson.setText(R.string.anyone);
		Log.d(LOG_TAG, "start");
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
