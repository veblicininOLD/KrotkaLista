package com.shortList.app.control;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

import com.shortList.app.db.DBAdapter;
import com.shortList.app.model.Event;
import com.shortList.app.model.Person;


/**
 * Class responsible for operations on events and payments.
 * 
 * Pointer for an active event is stored in shared preferences.
 * 
 * @author wojtek
 *
 */
public class PaymentManager  {

	public static final int NEW_PARTICIPANT = 0;
	
	protected Event activeEvent = new Event();
	protected DBAdapter db;
	//protected List<Event> 
	
	private static final PaymentManager instance = new PaymentManager();

	private static final String PREFERENCE_ACTIVE_EVENT = "active_event";

	public PaymentManager() {
		// load all from database
	}
	
	/**
	 * This method have to be called before using class PaymentManger
	 * TODO: make it better
	 * @param context of application using PaymentManager
	 */
	public void init(Context context){
		db = new DBAdapter(context);
		db.loadEvents();
	}
	
	public boolean setActiveEvent(long id, SharedPreferences settings ){ 
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(PREFERENCE_ACTIVE_EVENT, id);		 
		return editor.commit();
	}
	
	public void finish(){
		db.close();
	}
	
	/** account the event
	 * 
	 * @param event to account
	 * @return a list of cash to pay for each participant of an event
	 */
	public Map<Person, Float> account(Event event){
		Map<Person, Float> settlement = new HashMap<Person, Float>();

		return settlement;
	}
	
	public String[] getParticipantNames(){
		String[] names = new String[activeEvent.getPersons().size()];
		int i = 0;
		for(Person p : activeEvent.getPersons())
			names[i++] = p.getName();
		return names;
	}
 
	public boolean addParticipant(String name){
		return addParticipant(new Person(name));
	}
	
	public boolean addParticipant(Person person){
		return activeEvent.getPersons().add(person);
	}
	
	/** returns on instance of the Payment Manager.
	 * 
	 * @return instance of the Payment Manager
	 */
	public static PaymentManager getInstance() {
		return instance;
	}

	public Event getActiveEvent() {
		return activeEvent;
	}
	
	
}
