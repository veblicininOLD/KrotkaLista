package com.shortList.app.ui;
 

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
 
 

public class ParticipantsActivity extends  ListActivity  {

    private static final String LOG_TAG = "ParticipantsActivity"; 

	//protected DBAdapter db; 
	protected SimpleCursorAdapter adapter;
	protected PaymentManager pm = PaymentManager.getInstance();
	
	protected final int CREATE_NEW_PARTICIPANT = 1;  
	
	public void refresh(){
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, pm.getParticipantNames()));
	}

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Event event = null; 
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

 		String[] names = pm.getParticipantNames(); 
		this.setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, names));			
		
      //  this.setListAdapter(adapter);  
    }     
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.participant_menu, menu);
		return true;
	}
	
	
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		AlertDialog d = (AlertDialog) dialog;	 
		switch(id){
			case CREATE_NEW_PARTICIPANT: 
		} 
		// TODO Auto-generated method stub
		//super.onPrepareDialog(id, dialog);
	}
	
	
	
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
                    	pm.addParticipant(name.getText().toString());
                		save(name.getText().toString(), pm.getActiveEvent());
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
		default:
	        dialog = null;
		}
		return dialog;
	}
	
	private void save(String personName, Event event){
		DBAdapter db = new DBAdapter(this);
		db.saveParticipant(personName, event);
 		db.close();
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
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		//TODO przed usunieciem sprawdzic czy sa dla tej osoby wpisy
		
		
//		Log.d(LOG_TAG, "clicked:" +  ((SQLiteCursor)adapter.getItem(position)).getString(DBAdapter.KEY_URL_POSITION) + " id:" + id + " position: " + position);
//		super.onListItemClick(l, v, position, id);		
//		Bundle bun = new Bundle(); 				
//		bun.putInt(DBAdapter.KEY_ROWID, position);
//		bun.putString(DBAdapter.KEY_URL, ((SQLiteCursor)adapter.getItem(position)).getString(DBAdapter.KEY_URL_POSITION) );						
//		Intent intent = new Intent();
//		intent.putExtras(bun);
//        setResult(RESULT_OK, intent);
//        finish();
	}
 
}
