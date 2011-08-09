package com.shortList.app.ui;
 
import com.shortList.app.db.Constants;
import com.shortList.app.db.DBAdapter;

import android.R;
import android.app.Activity;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;

public class EventListActivity  extends ListActivity {
    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//    }
	
	private DBAdapter myDB = null; 
	private SimpleCursorAdapter adapter;
	
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

         myDB = new DBAdapter(this);
        Cursor c = myDB.getEvents();
        startManagingCursor(c);    

          adapter = new SimpleCursorAdapter(this,
                android.R.layout.activity_list_item, c, 
                new String[] { Constants.KEY_EVENT_NAME },
                new int[] { android.R.id.text1 });

        adapter.setCursorToStringConverter(new CursorToStringConverter() {
            public CharSequence convertToString(Cursor theCursor) {
                String number = theCursor.getString(1); 
                return number ;
            }
        }); 

        this.setListAdapter(adapter);
    }    
}