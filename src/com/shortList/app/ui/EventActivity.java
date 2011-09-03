package com.shortList.app.ui;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shortList.app.control.PaymentManager;
import com.shortList.app.db.DBAdapter;
import com.shortList.app.model.Person;

public class EventActivity extends Activity {

	private static final String LOG_TAG = "EventActivity";

	protected Button newPaymentButton;
	protected TextView suggestedPerson;
	protected Button showPaymentsButton;
	
	protected PaymentManager pm ;

	private Button accountEventButton;

	private void showCalculations(){
		Map<Person, Float> saldos = pm.summarize();
		StringBuilder sb = new StringBuilder();
		for(Person p : saldos.keySet())
			sb.append( p.getName() + " " + saldos.get(p) + "\n" );
		
		
		Toast toast =  Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG); 
		toast.show();
	} 

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event);
		
		pm = PaymentManager.getInstance(); 
		pm.init(getApplicationContext());

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
				if (pm.getActiveEvent().getPersons().size() <= 0){
					Toast toast =  Toast.makeText(getApplicationContext(), R.string.warning_no_participant, Toast.LENGTH_LONG); 
					toast.show();
				}else				
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
		suggestedPerson = (TextView) findViewById(R.id.suggested_person);
		updateSuggestedPayer();		
	}
	
	@Override
	protected void onStart() {
		updateSuggestedPayer();
		super.onStart();
	}
	
	private void updateSuggestedPayer(){
		if (pm.getSuggestedPerson() != null)
			suggestedPerson.setText(pm.getSuggestedPerson().getName());
		else
			suggestedPerson.setText(R.string.anyone); 
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		save();
		return super.onKeyDown(keyCode, event);
	}
	
//	private void save(){
//		DBAdapter db = new DBAdapter(this);
//		db.save(pm.getActiveEvent());
// 		db.close();
//	}
	
}
