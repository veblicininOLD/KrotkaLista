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
//		clear(); 
		db.execSQL(CREATE_TABLE_EVENT);
		db.execSQL(CREATE_TABLE_PERSON);
		db.execSQL(CREATE_TABLE_PAYMENT);
		db.execSQL(CREATE_TABLE_DEBTOR);
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(LOG_TAG, "onCreate");
		db.execSQL(CREATE_TABLE_EVENT);
		db.execSQL(CREATE_TABLE_PERSON);
		db.execSQL(CREATE_TABLE_PAYMENT);
		db.execSQL(CREATE_TABLE_DEBTOR);
	}
	
	protected void clear() {
		db.execSQL("drop table " + DATABASE_TABLE_EVENT); 
		db.execSQL("drop table " + DATABASE_TABLE_PERSON);
		db.execSQL("drop table " + DATABASE_TABLE_PAYMENT);
		db.execSQL("drop table " + DATABASE_TABLE_DEBTOR); 
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
		db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE_PAYMENT);
		db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE_PERSON);
		onCreate(db);
	}

	/**
	 * 
	 * @param event
	 * @return
	 */
	public List<Person> loadParticipants(long eventId) {
		ArrayList<Person> list = new ArrayList<Person>(); 
		Cursor personCursor = db.query(true, DATABASE_TABLE_PERSON, new String[] {
				KEY_ROWID,  KEY_USER_NAME, KEY_EVENT_ID  }, 
				KEY_EVENT_ID + "=" + eventId, null, null, null, null, null);
		Person person;
		Log.d(LOG_TAG, personCursor.getCount() + " ");
		while (personCursor.moveToNext()) {
			person = getPersonFromCursor(personCursor);
			list.add(person);
			Log.d(LOG_TAG, String.format("Loaded Person %s %d", person.getName(), person.getId()));
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
			retCode = (saveParticipant(p, event) < 0 )? -1 : retCode;
		}
		return retCode;
	}


	private long saveParticipant(Person p, Event event) {		
		long retCode = 0L;
		long tmp;
		ContentValues initialValues = new ContentValues(); 
		initialValues.put(KEY_EVENT_ID, event.getId());	 
		initialValues.put(KEY_USER_NAME, p.getName()); 	
		tmp = db.insert(DATABASE_TABLE_PERSON, null, initialValues);
		retCode = ( tmp < 0) ? -1 : retCode;	
		Log.d(LOG_TAG, String.format( "Saving Participant (%s); DBCode: %d", p.getName(), tmp));
		return retCode;
	}

	/**
	 * load list of saved events in database
	 * @return list of all events
	 */
	public ArrayList<Event> load(){
		ArrayList<Event> list = new ArrayList<Event>(); 
		Cursor eventCursor = db.query(true, DATABASE_TABLE_EVENT, new String[] {
				KEY_ROWID, KEY_EVENT_ID   }, 
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
		List<Person> persons = loadParticipants(id);
		List<Payment> payments = loadPayments(id);
		e  = new Event(id, payments, persons);
		//Log.d(LOG_TAG, String.format("Event loaded: %s", name));
 		return e;
	}
	
	private List<Payment> loadPayments(long eventId) {
		ArrayList<Payment> list = new ArrayList<Payment>(); 
		Cursor paymentCursor = db.query(true, DATABASE_TABLE_PAYMENT, new String[] {
				KEY_ROWID, KEY_PAYMENT_CASH, KEY_PAYMENT_DATE, KEY_PAYMENT_DESCRIPTION,
				 KEY_PAYMENT_PAYER, KEY_EVENT_ID}, 
				KEY_EVENT_ID + "=" + eventId, null, null, null, null, null);
		Payment payment;
		while (paymentCursor.moveToNext()) {
			payment = getPaymentFromCursor(paymentCursor, eventId);
			list.add(payment);
		}
		paymentCursor.close();				
		return list;
	}


	private Payment getPaymentFromCursor(Cursor paymentCursor, long eventId) {
		long paymentId = paymentCursor.getLong(0);
		float cash = paymentCursor.getFloat(1);
		long date = paymentCursor.getLong(2);
		String description = paymentCursor.getString(3);
		Person payer = getPerson(paymentCursor.getLong(4));
		
		List<Person> debtors = getDebtors(eventId, paymentId);
			
		Payment p = new Payment(paymentId, cash, date, payer, debtors, description );
		return p;		
	}


	private Person getPerson(long personId) {
		Cursor personCursor = db.query(true, DATABASE_TABLE_PERSON, new String[] {
				KEY_ROWID,  KEY_USER_NAME, KEY_EVENT_ID  }, 
				KEY_ROWID + "=" + personId, null, null, null, null, null);
		personCursor.moveToNext();
		int size = personCursor.getCount();
		long id = personCursor.getLong(0);
		String name = personCursor.getString(1);
		personCursor.close();
		return new Person(id, name);		
	}


	private List<Person> getDebtors(long eventId, long paymentId) {
		Cursor debtorCursor = db.query(true, DATABASE_TABLE_DEBTOR, new String[] {
				KEY_ROWID,  KEY_DEBTOR_DEBTOR_ID, KEY_DEBTOR_EVENT_ID, KEY_DEBTOR_PAYMENT_ID  }, 
				KEY_DEBTOR_PAYMENT_ID + "=" + paymentId + " AND " +
				KEY_DEBTOR_EVENT_ID + " = " + eventId , null, null, null, null, null);
		// use cursor join  
		debtorCursor.moveToNext();
		if (debtorCursor.getCount() == 0){
			debtorCursor.close();
			return new ArrayList<Person>();
		}
		long debtorId = debtorCursor.getLong(1);
		debtorCursor.close();
		return null;
	}


	@Override
	public synchronized void close() {		
		super.close();
	}


	public long createNewEvent() {
		long retCode = -1;
		ContentValues initialValues = new ContentValues();			
		initialValues.put(KEY_EVENT_ID, -1);
		retCode = db.insert(DATABASE_TABLE_EVENT, null, initialValues);
		Log.d(LOG_TAG, String.format("Saving event, retCode %d", retCode));
		return retCode;		
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
		saveDebtors(event.getId(), payment);
		
		retCode = db.insert(DATABASE_TABLE_PAYMENT, null, initialValues);	
		Log.d(LOG_TAG, String.format(
				"Saving Payment %f (%s); DBCode: %d", payment.getCashAmount(), payment.getDescription(), retCode));
		return retCode;
		
	}


	private long saveDebtors(long eventId, Payment payment) {
		long retCode = -1;
		for (Person debtor : payment.getDebtors()){
			ContentValues initialValues = new ContentValues(); 
			initialValues.put(KEY_DEBTOR_EVENT_ID, eventId);
			initialValues.put(KEY_DEBTOR_PAYMENT_ID, payment.getId());
			initialValues.put(KEY_DEBTOR_DEBTOR_ID, debtor.getId());
			retCode = db.insert(DATABASE_TABLE_DEBTOR, null, initialValues);	
		} 
		Log.d(LOG_TAG, String.format(
				"Saving debtors %d; DBCode: %d", payment.getDebtors().size(),  retCode));
		return retCode;

	}
}
