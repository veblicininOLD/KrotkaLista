package com.shortList.app.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.shortList.app.control.PaymentManager;
import com.shortList.app.db.DBAdapter;
import com.shortList.app.model.Payment;
import com.shortList.app.model.Person;

public class PaymentActivity extends Activity {
		
	private static final String LOG_TAG = "PaymentActivity";
	PaymentManager pm;
	
	protected EditText amount;
	protected DatePicker date;
	protected Spinner payerField;
	protected List<String> debtors;
	protected String[] options;
	protected boolean[] selections ;
	protected EditText descriptionField;
	private Button debtorsButton;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		pm = PaymentManager.getInstance();		
		options = pm.getParticipantNames(); 
		selections =  new boolean[ options.length ];		
		
		debtors =  new ArrayList<String>();
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.new_payment); 

	      amount =  (EditText) findViewById(R.id.new_payment_how_much);
	      date =  (DatePicker) findViewById(R.id.new_payment_when);
	      payerField =  (Spinner) findViewById(R.id.new_payment_who);
	      descriptionField =  (EditText) findViewById(R.id.new_payment_for_what);

	      
	      String[] items =  PaymentManager.getInstance().getParticipantNames(); 

	      ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	              android.R.layout.simple_spinner_item, items);
	      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	      payerField.setAdapter(adapter);
	    
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
	    
	    debtorsButton = (Button) findViewById(R.id.new_payment_debtors);
	    debtorsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { 
				Log.d(LOG_TAG, "click");
				showDialog(0);				
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
		 
		Person payer = pm.findPersonByName(((String) payerField.getSelectedItem())); 
		String description = descriptionField.getText().toString();		
	
		for(boolean b : selections){
			
		}
		
		List<Person> paymentDebtors = pm.getPersonsFromNames(debtors);
		if (paymentDebtors.size() == 0){
			Toast toast =  Toast.makeText(getApplicationContext(), R.string.warning_no_debtor, Toast.LENGTH_LONG); 
			toast.show();
			return null;
		}
		
		Log.d(LOG_TAG, String.format( "creating a new payment (cash: %f)", cash));
	
		
		
		Payment p = new Payment(cash, date, payer, paymentDebtors, description);
		
		pm.addPayment(p);
		
		// TODO: opening and closing db in a main activity?	
		DBAdapter db = new DBAdapter(getApplicationContext());
		db.savePayment(p, pm.getActiveEvent() );
		db.close();
		
		return p;
	}

	
	@Override
	protected Dialog onCreateDialog( int id ) 
	{		
		return 
		new AlertDialog.Builder( this )
        	.setTitle( R.string.debtors )
        	.setMultiChoiceItems( options, selections, new DialogSelectionClickHandler() )
        	.setPositiveButton( R.string.form_ok, new DialogButtonClickHandler() )
        	.create();
	}
	
	
	public class DialogSelectionClickHandler implements DialogInterface.OnMultiChoiceClickListener
	{
		public void onClick( DialogInterface dialog, int clicked, boolean selected )
		{
			Log.d( LOG_TAG, options[ clicked ] + " selected: " + selected );
		}
	}
	
	private void addDebtors(){
		debtors.clear();
		for( int i = 0; i < options.length; i++ ){
			Log.d( LOG_TAG, options[ i ] + " selected: " + selections[i] );
			if (selections[i] == true){
				debtors.add(options[i]);
			}
		}
	}

	public class DialogButtonClickHandler implements DialogInterface.OnClickListener{
		public void onClick( DialogInterface dialog, int clicked ){
			switch( clicked ){
				case DialogInterface.BUTTON_POSITIVE:
					addDebtors();
					break;
			}
		}  
	}
	
 
}
