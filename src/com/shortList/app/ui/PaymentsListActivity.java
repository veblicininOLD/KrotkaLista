package com.shortList.app.ui;

import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.shortList.app.control.PaymentManager;
import com.shortList.app.model.Payment;

public class PaymentsListActivity extends ListActivity {

    private static final String LOG_TAG = "PaymentsListActivity";
	private static final int ACTIONS = 0;
	private static final int CONFIRMATION = 1; 
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
	protected Dialog onCreateDialog( int id ) 
	{		
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String[] possibleActions = new String[]{
				this.getString(R.string.dialog_show_details),
				this.getString(R.string.dialog_remove) 
		};
		
		switch(id){
		case ACTIONS:				
			builder.setTitle(R.string.dialog_choose_actoin);
			builder.setItems(possibleActions, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
					Intent iStats = null;
//					if (item == 0)
//						 
//					dialog.dismiss();
//					startActivity(iStats);
			    }
			});
			dialog = builder.create();
			break;	
		case CONFIRMATION:
			break;			
		default:
			dialog = null;
		}
		return dialog;
	}
    
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	showDialog(ACTIONS, id);    	
     	super.onListItemClick(l, v, position, id);
    }
    
	private void showDialog(int actionId, long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	//	inflater.inflate(R.menu.participant_menu, menu);
		return true;
	}
}
