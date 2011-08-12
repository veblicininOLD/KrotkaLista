package com.shortList.app.ui;

import java.util.Date;
import java.util.List;

import com.shortList.app.control.PaymentManager;
import com.shortList.app.model.Payment;
import com.shortList.app.model.Person;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

public class PaymentActivity extends Activity {
		
	private static final String LOG_TAG = "PaymentActivity";
	
	protected EditText amount;
	protected DatePicker date;
	protected Spinner payer;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.new_payment); 

	      amount =  (EditText) findViewById(R.id.new_payment_how_much);
	      date =  (DatePicker) findViewById(R.id.new_payment_when);
	      payer =  (Spinner) findViewById(R.id.new_payment_who);
	      
	      String[] items =  PaymentManager.getInstance().getParticipantNames(); 

	      ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	              android.R.layout.simple_spinner_item, items);
	      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	      payer.setAdapter(adapter);
	    
	    Button confirmButton = (Button) findViewById(R.id.form_confirm_new_payment);
	    confirmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent i = new Intent(getApplicationContext(),
//						PaymentActivity.class); 
//				startActivityForResult(i, 0); 
				Payment payment = createNewPayment();
				PaymentManager.getInstance().addPayment(payment);
			}
	
		});
	}
	
	private Payment createNewPayment() {
		Float cash;
		try{
			cash = new Float(amount.getText().toString());
		} catch (Exception e){
			Log.e(LOG_TAG, "Number format exception in cash amount");
			cash = 0.0f;
		}
		Date date = new Date(1000L);
		Person payer = null;
		List<Person> debtors = null;
		String description = null;
		
		Log.d(LOG_TAG, String.format( "creating a new payment (cash: %f)", cash));
		
		Payment p = new Payment(cash, date, payer, debtors, description);
		return p;
	}
}
