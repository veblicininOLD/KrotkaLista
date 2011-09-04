package com.shortList.app.ui;

import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.shortList.app.control.PaymentManager;
import com.shortList.app.db.DBAdapter;
import com.shortList.app.model.Payment;
import com.shortList.app.model.Person;

public class PaymentsListActivity extends ListActivity {

   // private static final String LOG_TAG = "PaymentsListActivity";
	private static final int ACTIONS = 0;
	private static final int CONFIRMATION = 1; 
    protected PaymentManager pm;
	private long choosenPayment;
	protected List<Payment> payments;

    public void onCreate(Bundle icicle) {
    	pm = PaymentManager.getInstance();
        super.onCreate(icicle); 
        loadData();
    }    
    
    private void loadData() {
        payments =  pm.getActiveEvent().getPayments();         
		this.setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, getNames(payments)));
	}
    
    protected String[] getNames(List<Payment> payments){
    	String[] names = new String[payments.size()];
    	Arrays.sort(payments.toArray());
    	
    	int i = 0;
    	for(Payment p : payments){
    		names[i++] = p.getPayer().getName() + " " + p.getCashAmount() + "zł" ;
    	}
    	return names;
    }
    
	private void showInfo() {
		Payment p  = payments.get((int) choosenPayment);
		StringBuilder sb = new StringBuilder(); //TODO
		sb.append(p.getPayer().getName());
		sb.append("\n==============\n");
		sb.append(p.getCashAmount() + "zł\n");
		sb.append(p.getDescription() + "\n");
		sb.append(p.getDate() + "\n");
		sb.append("\n==============\n");
		for(Person per : p.getDebtors())
			sb.append(per.getName()+ " ");
		
		Toast toast =  Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG); 
		toast.show();
	}
    
	private void deleteItem() {
		//TODO check, ask
		Payment paymentToDelete = payments.get((int) choosenPayment);
		pm.deletePayment(paymentToDelete);
		DBAdapter db = new DBAdapter(this);
		db.deletePayment(paymentToDelete, pm.getActiveEvent());
		db.close();
		loadData();
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
			builder.setTitle(R.string.dialog_choose_action);
			builder.setItems(possibleActions, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {				
					if (item == 0){
						showInfo();						
					}else if (item == 1){
						deleteItem();
					}											
//					dialog.dismiss();
//					startActivity(iStats);
			    }


			    
			});
			dialog = builder.create();
			break;	
		case CONFIRMATION:
			builder.setTitle(R.string.dialog_confirmation);
			builder.setItems(possibleActions, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
					//Intent iStats = null;
//					if (item == 0)
//						 
//					dialog.dismiss();
//					startActivity(iStats);
			    }
			});
			dialog = builder.create();
			break;	 		
		default:
			dialog = null;
		}
		return dialog;
	}
    
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	choosenPayment = id;
    	showDialog(ACTIONS);    	
     	super.onListItemClick(l, v, position, id);
    }
    
	 @Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		AlertDialog d = (AlertDialog) dialog;	
		switch (id) {
		case ACTIONS:
			d.setTitle(R.string.dialog_choose_action);
			break;
		case CONFIRMATION:
			d.setTitle(R.string.dialog_confirmation);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//MenuInflater inflater = getMenuInflater();
	//	inflater.inflate(R.menu.participant_menu, menu);
		return true;
	}
}
