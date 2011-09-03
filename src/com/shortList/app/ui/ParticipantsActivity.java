package com.shortList.app.ui;
 

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.shortList.app.control.PaymentManager;
import com.shortList.app.db.DBAdapter;
import com.shortList.app.model.Event;
import com.shortList.app.model.Payment;
import com.shortList.app.model.Person;
 
 

public class ParticipantsActivity extends  ListActivity  {

    private static final String LOG_TAG = "ParticipantsActivity";


	//protected DBAdapter db; 
	protected SimpleCursorAdapter adapter;
	protected long choosenParticipant;
	protected PaymentManager pm = PaymentManager.getInstance();
	protected List<Person> payments;

	
	protected static final int CREATE_NEW_PARTICIPANT = 10;  
	protected static final int DELETE_PARTICIPANT = 20; 
	
	public void refresh(){
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, pm.getParticipantNames()));
	}

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        loadData();
      //  Event event = null; 
//        Cursor c = db.getParticipants(event);
//        startManagingCursor(c);   
//          adapter = new SimpleCursorAdapter(this,
//                android.R.layout.activity_list_item, c, 
//                new String[] { KEY_USER_NAME },
//                new int[] { android.R.id.text1 });
//
//        adapter.setCursorToStringConverter(new CursorToStringConverter() {
//            public CharSequence convertToString(Cursor theCursor) {
//                String number = theCursor.getString(KEY_USER_NAME_POSITION); 
//                return number ;
//            }
//        }); 
	//	setContentView(R.layout.participants); 
      //  this.setListAdapter(adapter);  
    }     
    
    protected void loadData(){
 		String[] names = pm.getParticipantNames(); 
		this.setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, names));
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.participant_menu, menu);
		return true;
	}
	
 
	
//	@Override
//	protected void onPrepareDialog(int id, Dialog dialog) {
//		AlertDialog d = (AlertDialog) dialog;	 
//		switch(id){
//			case CREATE_NEW_PARTICIPANT:
//			case DELETE_PARTICIPANT:
//		} 
//		// TODO Auto-generated method stub
//		super.onPrepareDialog(id, dialog);
//	}
	
	
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		Dialog dialog = null;
		 
		switch(id){
		case CREATE_NEW_PARTICIPANT:
			LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(R.layout.alert_dialog_new_participant, null);
            final EditText name = (EditText) textEntryView.findViewById(R.id.name_of_new_participant);
            return new AlertDialog.Builder(this)
             //   .setIconAttribute(android.R.attr.alertDialogIcon) 
            .setTitle(R.string.participant_add_new_title)
                .setView(textEntryView)
                .setPositiveButton(R.string.form_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {                        
                    	pm.addParticipant(new Person(save(name.getText().toString(), pm.getActiveEvent()), name.getText().toString()));                                    		
                    	Log.d(LOG_TAG, String.format("Name of new participant: %s", name.getText().toString()));
                    	refresh();                	
                    }
                })
                .setNegativeButton(R.string.form_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /* User clicked cancel so do some stuff */
                    }
                })
                .create();
		case DELETE_PARTICIPANT:
        //    final EditText name_delete = (EditText) textEntryView.findViewById(R.id.name_of_new_participant);
			 // new AlertDialog.Builder(this);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			 builder.setMessage(R.string.dialog_ask_for_participant_deletion)
		       .setCancelable(false)
		       .setPositiveButton(R.string.form_ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   if (check()){
		        		   delete();
		        		   loadData();
		        	   }
		           }
		       })
		       .setNegativeButton(R.string.form_cancel, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
			 return builder.create();
		default:
	        dialog = null;
		}
		return dialog;
	}
	
	protected void delete() {
		//TODO check, ask
		String nameOfParticipantToDelete = pm.getParticipantNames()[(int)choosenParticipant];
 		pm.deletePerson(nameOfParticipantToDelete);
		DBAdapter db = new DBAdapter(this);
		db.deletePerson(nameOfParticipantToDelete, pm.getActiveEvent());
		db.close();
		loadData();
	}

	protected boolean check() {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * Saving a participant to an event
	 * @param personName
	 * @param event
	 * @return
	 */
	private long save(String personName, Event event){
		DBAdapter db = new DBAdapter(this);
		long dbCode = db.saveParticipant(personName, event);
 		db.close();
 		return dbCode;
	}
    
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.participants:
				showDialog(CREATE_NEW_PARTICIPANT);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		   if ((keyCode == KeyEvent.KEYCODE_BACK)) {
// 		        db.saveParticipants(pm.getActiveEvent());
		   }
		return super.onKeyDown(keyCode, event);      
		       
	}
	
	
	/**
	 * action after clicking a participant
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		choosenParticipant = id;
		showDialog(DELETE_PARTICIPANT); 
	}
 
}
