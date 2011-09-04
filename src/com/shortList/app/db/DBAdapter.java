package com.shortList.app.db;
  
import static com.shortList.app.db.Constants.CREATE_TABLE_DEBTOR;
import static com.shortList.app.db.Constants.CREATE_TABLE_EVENT;
import static com.shortList.app.db.Constants.CREATE_TABLE_PAYMENT;
import static com.shortList.app.db.Constants.CREATE_TABLE_PERSON;
import static com.shortList.app.db.Constants.DATABASE_NAME;
import static com.shortList.app.db.Constants.DATABASE_TABLE_DEBTOR;
import static com.shortList.app.db.Constants.DATABASE_TABLE_EVENT;
import static com.shortList.app.db.Constants.DATABASE_TABLE_EVENT_PAYMENTS;
import static com.shortList.app.db.Constants.DATABASE_TABLE_PAYMENT;
import static com.shortList.app.db.Constants.DATABASE_TABLE_PERSON;
import static com.shortList.app.db.Constants.DATABASE_VERSION;
import static com.shortList.app.db.Constants.KEY_DEBTOR_DEBTOR_ID;
import static com.shortList.app.db.Constants.KEY_DEBTOR_EVENT_ID;
import static com.shortList.app.db.Constants.KEY_DEBTOR_PAYMENT_ID;
import static com.shortList.app.db.Constants.KEY_EVENT_ID;
import static com.shortList.app.db.Constants.KEY_PAYMENT_CASH;
import static com.shortList.app.db.Constants.KEY_PAYMENT_DATE;
import static com.shortList.app.db.Constants.KEY_PAYMENT_DESCRIPTION;
import static com.shortList.app.db.Constants.KEY_PAYMENT_PAYER;
import static com.shortList.app.db.Constants.KEY_ROWID;
import static com.shortList.app.db.Constants.KEY_USER_NAME;

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
		long eventId = personCursor.getLong(2);
		Person p = new Person(id, name, eventId);
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

	public long saveParticipant(Person person, Event event) {
		return saveParticipant(person.getName(), event);
	}
	
	
	public long saveParticipant(String p, Event event) {		
		long retCode = 0L;
		long tmp;
		ContentValues initialValues = new ContentValues(); 
		initialValues.put(KEY_EVENT_ID, event.getId());	 
		initialValues.put(KEY_USER_NAME, p); 	
		tmp = db.insert(DATABASE_TABLE_PERSON, null, initialValues);
		retCode = ( tmp < 0) ? -1 : retCode;	
		//Log.d(LOG_TAG, event.)
		Log.d(LOG_TAG, String.format( "Saving Participant (%s); DBCode: %d", p, tmp));
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
		List<Payment> payments = loadPayments(id, persons);
		e  = new Event(id, payments, persons);
		//Log.d(LOG_TAG, String.format("Event loaded: %s", name));
 		return e;
	}
	
	private List<Payment> loadPayments(long eventId, List<Person> persons) {
		ArrayList<Payment> list = new ArrayList<Payment>(); 
		Cursor paymentCursor = db.query(true, DATABASE_TABLE_PAYMENT, new String[] {
				KEY_ROWID, KEY_PAYMENT_CASH, KEY_PAYMENT_DATE, KEY_PAYMENT_DESCRIPTION,
				 KEY_PAYMENT_PAYER, KEY_EVENT_ID}, 
				KEY_EVENT_ID + "=" + eventId, null, null, null, null, null);
		Payment payment;
		while (paymentCursor.moveToNext()) {
			payment = getPaymentFromCursor(paymentCursor, eventId, persons);
			list.add(payment);
		}
		Log.d(LOG_TAG, String.format(" ====== loading payments, size[%d]", list.size()));
		paymentCursor.close();				
		return list;
	}


	private Payment getPaymentFromCursor(Cursor paymentCursor, long eventId, List<Person> persons) {
		long paymentId = paymentCursor.getLong(0);
		float cash = paymentCursor.getFloat(1);
		long date = paymentCursor.getLong(2);
		String description = paymentCursor.getString(3);
		
		Person payer = findPersonById(paymentCursor.getLong(4), persons); // new Person(paymentCursor.getLong(4), findNameById(paymentCursor.getLong(4), persons));  //getPerson(paymentCursor.getLong(4));
		
		List<Person> debtors = getDebtors(eventId, paymentId, persons);
			
		Payment p = new Payment(paymentId, cash, date, payer, debtors, description );
		return p;		
	}


	private Person getPerson(long personId) {
		Cursor personCursor = db.query(true, DATABASE_TABLE_PERSON, new String[] {
				KEY_ROWID,  KEY_USER_NAME, KEY_EVENT_ID  }, 
				KEY_ROWID + "=" + personId, null, null, null, null, null);
		personCursor.moveToNext();
//		int size = personCursor.getCount();
//		Log.d(LOG_TAG, size + "");
		long id = personCursor.getLong(0);
		String name = personCursor.getString(1);
		long eventId = personCursor.getLong(2);
		personCursor.close();
		return new Person(id, name, eventId);		
	}


	private List<Person> getDebtors(long eventId, long paymentId, List<Person> persons) {
		//TODO
		Cursor debtorCursor = db.query(true, DATABASE_TABLE_DEBTOR, new String[] {
				KEY_ROWID,  KEY_DEBTOR_DEBTOR_ID, KEY_DEBTOR_EVENT_ID, KEY_DEBTOR_PAYMENT_ID  }, 
				KEY_DEBTOR_PAYMENT_ID + "=" + paymentId /*+ " AND " +
				KEY_DEBTOR_EVENT_ID + " = " + eventId*/ , null, null, null, null, null);
		// use cursor join  
		List<Person> list = new ArrayList<Person>(); 		
		Person debtor;
	 
		while (debtorCursor.moveToNext()) {
			debtor = getDebtorFromCursor(debtorCursor, eventId, persons);
			list.add(debtor);
		}
		
		debtorCursor.close();
		Log.d(LOG_TAG, String.format("Amount of loaded debtors for a payment: %d", list.size()));
		return list;
	}


	private Person getDebtorFromCursor(Cursor debtorCursor, long eventId, List<Person> persons) {
		long debtorId = debtorCursor.getLong(1); 		
		String name = findNameById(debtorId, persons);		
		Person person = new Person(debtorId, name, eventId);
		Log.d(LOG_TAG, String.format("Loading debtor %s, [%d]", name, debtorId));
		return person;
	}

	private String findNameById(long id, List<Person> persons){
		for(Person p : persons){
			if (p.getId() == id)
				return p.getName();
		}
		return "";
	}

	private Person findPersonById(long id, List<Person> persons){
		for(Person p : persons){
			if (p.getId() == id)
				return p;
		}
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
		 
		
		retCode = db.insert(DATABASE_TABLE_PAYMENT, null, initialValues);	
		saveDebtors(event.getId(), payment.getDebtors(), retCode);
		Log.d(LOG_TAG, String.format(
				"Saving Payment %f (%s); DBCode: %d, PayerId: %d, EventID %d", payment.getCashAmount(), payment.getDescription(), retCode, payment.getPayer().getId(), event.getId()));
		return retCode;
		
	}


	private long saveDebtors(long eventId, List<Person> debtors, long paymentId) {
		long retCode = -1;
		for (Person debtor : debtors){
			ContentValues initialValues = new ContentValues(); 
			initialValues.put(KEY_DEBTOR_EVENT_ID, eventId);
			initialValues.put(KEY_DEBTOR_PAYMENT_ID, paymentId);
			initialValues.put(KEY_DEBTOR_DEBTOR_ID, debtor.getId());
			retCode = db.insert(DATABASE_TABLE_DEBTOR, null, initialValues);	
		Log.d(LOG_TAG, String.format(
				"Saving debtors %d; DBCode: %d eventId: %d, paymentId: %d, debtor: %s, debtorID %d ", debtors.size(),  retCode, eventId, paymentId, debtor.getName(), debtor.getId()));
		} 
		return retCode;
	}


	public long save(Event activeEvent) {
		long retCode = -1; 
		
		ContentValues initialValues = new ContentValues();
	//	initialValues.put(KEY_EVENT_ID, eventId); 	
		retCode = db.insert(DATABASE_TABLE_EVENT, null, initialValues);	
		Log.d(LOG_TAG, String.format(
				"Saving event DBCode: %d",  retCode));
		return retCode;
	}


	public void deletePayment(Payment paymentToDelete, Event activeEvent) {
		//TODO:wtf??!!!!
		int deletedRows = db.delete(DATABASE_TABLE_PAYMENT, /*"( "+ KEY_EVENT_ID + "=? ) AND ( " +*/ KEY_ROWID + "=? ", new String[] { /*activeEvent.getId() + " " , */paymentToDelete.getId() + ""});
		db.delete(DATABASE_TABLE_DEBTOR,  KEY_EVENT_ID + "=? AND " + KEY_DEBTOR_PAYMENT_ID + " =?", new String[] { activeEvent.getId() + " " , paymentToDelete.getId() + ""});
		Log.d(LOG_TAG, String.format("Deleted rows: %d, eventId: %d, paymentId: %d", deletedRows, activeEvent.getId(), paymentToDelete.getId() ));
	}

	public void deletePerson(String personToDelete, Event activeEvent) {
		int ret = db.delete(DATABASE_TABLE_PERSON, /*"(" + KEY_EVENT_ID + " =?) AND (" +*/ KEY_USER_NAME + " LIKE ? ", new String[] { /*activeEvent.getId() + " ",*/ personToDelete + "" });
		Log.d(LOG_TAG, "deleted persons: " + ret + " personToDelete " + personToDelete);
	}

	public void deletePerson(long personToDelete, Event activeEvent) {
		db.delete(DATABASE_TABLE_PERSON, KEY_EVENT_ID + "=? AND " + KEY_ROWID + "=?", new String[] { activeEvent.getId() + " " , personToDelete + ""});	
	}

	public void deletePerson(Person personToDelete, Event activeEvent) {
		db.delete(DATABASE_TABLE_PERSON, KEY_EVENT_ID + "=? AND " + KEY_ROWID + "=?", new String[] { activeEvent.getId() + " " , personToDelete.getId() + ""});	
	}

}
