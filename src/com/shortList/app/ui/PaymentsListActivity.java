package com.shortList.app.ui;

import java.util.Arrays;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;

import com.shortList.app.control.PaymentManager;
import com.shortList.app.model.Event;
import com.shortList.app.model.Payment;

public class PaymentsListActivity extends ListActivity {

    private static final String LOG_TAG = "PaymentsListActivity"; 
    protected PaymentManager pm;

    public void onCreate(Bundle icicle) {
    	pm = PaymentManager.getInstance();
        super.onCreate(icicle); 
        List<Payment> payments =  pm.getActiveEvent().getPayments();         
		this.setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, getNames(payments))); 
    }    
    
    protected String[] getNames(List<Payment> payments){
    	String[] names = new String[payments.size()];
    	Arrays.sort(payments.toArray());
    	
    	int i = 0;
    	for(Payment p : payments){
    		names[i++] = p.getPayer().getName() + " " + p.getCashAmount() ;
    	}
    	
    	return names;
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	//	inflater.inflate(R.menu.participant_menu, menu);
		return true;
	}
}
