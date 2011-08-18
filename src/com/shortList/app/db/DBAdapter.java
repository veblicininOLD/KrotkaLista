package com.shortList.app.db;
  
import static com.shortList.app.db.Constants.*;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.shortList.app.model.Event;
import com.shortList.app.model.Payment;
import com.shortList.app.model.Person;

public class DBAdapter extends SQLiteOpenHelper {

	private static final String LOG_TAG = "DBAdapter";

	private SQLiteDatabase db;

	public DBAdapter(Context ctx) { 
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION); 
		db = getWritableDatabase();
	//	clear();
		db.execSQL(CREATE_TABLE_EVENT);
		db.execSQL(CREATE_TABLE_PERSON);
		db.execSQL(CREATE_TABLE_PAYMENT);
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(LOG_TAG, "onCreate");
		db.execSQL(CREATE_TABLE_EVENT);
		db.execSQL(CREATE_TABLE_PERSON);
		db.execSQL(CREATE_TABLE_PAYMENT);
	}
	
	protected void clear() {
		db.execSQL("drop table " + DATABASE_TABLE_EVENT); 
		db.execSQL("drop table " + DATABASE_TABLE_PERSON);
		db.execSQL("drop table " + DATABASE_TABLE_PAYMENT);
//		db.execSQL("drop table " + DATABASE_TABLE_DEBTOR); 
//		db.execSQL("drop table " + DATABASE_TABLE_EVENT_PAYMENTS);
//		db.execSQL("drop table " + DATABASE_TABLE_PARTICIPANTS);
	}
		
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE_DEBTOR);
		db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE_EVENT);
		db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE_EVENT_PAYMENTS);
		db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE_PARTICIPANTS);
		db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE_PAYMENT);
		db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE_PERSON);
		onCreate(db);
	}

	/**
	 * 
	 * @param event
	 * @return
	 */
	public List<Person> getParticipants(long eventId) {
		ArrayList<Person> list = new ArrayList<Person>(); 
		Cursor personCursor = db.query(true, DATABASE_TABLE_PERSON, new String[] {
				KEY_ROWID,  KEY_USER_NAME, KEY_EVENT_ID  }, 
				KEY_EVENT_ID + "=" + eventId, null, null, null, null, null);
		Person person;
		while (personCursor.moveToNext()) {
			person = getPersonFromCursor(personCursor);
			list.add(person);
		}
		personCursor.close();				
		return list;
	}



	private Person getPersonFromCursor(Cursor personCursor) {
		long id = personCursor.getLong(0);
		String name = personCursor.getString(1);
		Person p = new Person(id, name);
		return p;
	}


	/**
	 * Save persons from a given event
	 * @param event for which persons are saved
	 * @return a id of last saved person, if an error occours than -1 is returned
	 */
	public long saveParticipants(Event event) {		 
		long retCode = 0L;
		for(Person p : event.getPersons()){
			retCode = (saveParticipant(p, event) <0 )? -1 : retCode;
		}
		return retCode;
	}


	private long saveParticipant(Person p, Event event) {		
		long retCode = -1;
		ContentValues initialValues = new ContentValues(); 
		initialValues.put(KEY_EVENT_ID, event.getId());
	 
		initialValues.put(KEY_USER_NAME, p.getName()); 	 		 		 
		retCode = db.insert(DATABASE_TABLE_PARTICIPANTS, null, initialValues);	
		Log.d(LOG_TAG, String.format(
				"Saving Participant (%s); DBCode: %d", p.getName(), retCode));
		return retCode;
	}

	/**
	 * load list of saved events in database
	 * @return list of all events
	 */
	public ArrayList<Event> load(){
		ArrayList<Event> list = new ArrayList<Event>(); 
		Cursor eventCursor = db.query(true, DATABASE_TABLE_EVENT, new String[] {
				KEY_ROWID,    }, 
				null, null, null, null, null, null);
		Event event;
		while (eventCursor.moveToNext()) {
			event = getEventFromCursor(eventCursor);
			list.add(event);
		}
		eventCursor.close();
		Log.d(LOG_TAG, "********** loading events, size[" + list.size()+ "] ******** ");
		return list; 
	}

 

	private Event getEventFromCursor(Cursor eventCursor) {
		Event e = null;
		long id = eventCursor.getLong(0);  
		List<Person> persons = getParticipants(id);
		List<Payment> payments = null;
		e  = new Event(id, payments, persons);
		//Log.d(LOG_TAG, String.format("Event loaded: %s", name));
 		return e;
	}
	
	@Override
	public synchronized void close() {		
		super.close();
	}


	public long createNewEvent() {
		// TODO Auto-generated method stub
		return 0;
	}
 

	public long savePayment(Payment payment, Event event) {
		if (event == null){
			Log.e(LOG_TAG, "No active event!");
			return -1;
		} else if (payment.getPayer() == null){
			Log.e(LOG_TAG, "No payer!");
			return -1;
		}
			
		
		long retCode = -1;
		ContentValues initialValues = new ContentValues(); 
		initialValues.put(KEY_EVENT_ID, event.getId());
	 
		initialValues.put(KEY_PAYMENT_CASH, payment.getCashAmount());
		initialValues.put(KEY_PAYMENT_DATE, payment.getDate().getTime());
		initialValues.put(KEY_PAYMENT_DESCRIPTION, payment.getDescription());		
		initialValues.put(KEY_PAYMENT_PAYER, payment.getPayer().getId());
		
		//TODO
		// save debtors
		
		retCode = db.insert(DATABASE_TABLE_PAYMENT, null, initialValues);	
		Log.d(LOG_TAG, String.format(
				"Saving Payment %d (%s); DBCode: %d", payment.getCashAmount(), payment.getDescription(), retCode));
		return retCode;
		
	}
}
