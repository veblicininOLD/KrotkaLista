package com.shortList.app.ui;

import java.util.Date;
import java.util.List;

import com.shortList.app.control.PaymentManager;
import com.shortList.app.db.DBAdapter;
import com.shortList.app.model.Payment;
import com.shortList.app.model.Person;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
		
				Payment payment = createAndSaveNewPayment();
				//showDialog(0);
				finish();
			}
	
		});
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		   if ((keyCode == KeyEvent.KEYCODE_BACK)) {
		        Log.d(LOG_TAG, "Back button pressed: " + keyCode + ", " + event);		        		     
		   }
		return super.onKeyDown(keyCode, event);      
		       
	}
	
	private Payment createAndSaveNewPayment() {
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
		PaymentManager pm = PaymentManager.getInstance(); 
		pm.addPayment(p);
		
		// TODO: opening and closing db in a main activity?	
		DBAdapter db = new DBAdapter(getApplicationContext());
		db.savePayment(p, pm.getActiveEvent() );
		db.close();
		
		return p;
	}
	/** test stuff **/
	protected CharSequence[] _options = { "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune" };
	protected boolean[] _selections =  new boolean[ _options.length ];
	
	@Override
	protected Dialog onCreateDialog( int id ) 
	{
		return 
		new AlertDialog.Builder( this )
        	.setTitle( "Planets" )
        	.setMultiChoiceItems( _options, _selections, new DialogSelectionClickHandler() )
        	.setPositiveButton( "OK", new DialogButtonClickHandler() )
        	.create();
	}
	
	
	public class DialogSelectionClickHandler implements DialogInterface.OnMultiChoiceClickListener
	{
		public void onClick( DialogInterface dialog, int clicked, boolean selected )
		{
			Log.i( "ME", _options[ clicked ] + " selected: " + selected );
		}
	}
	

	public class DialogButtonClickHandler implements DialogInterface.OnClickListener
	{
		public void onClick( DialogInterface dialog, int clicked )
		{
			switch( clicked )
			{
				case DialogInterface.BUTTON_POSITIVE:
					printSelectedPlanets();
					break;
			}
		}
	}
	
	protected void printSelectedPlanets(){
		for( int i = 0; i < _options.length; i++ ){
			Log.i( "ME", _options[ i ] + " selected: " + _selections[i] );
		}
	}
}
