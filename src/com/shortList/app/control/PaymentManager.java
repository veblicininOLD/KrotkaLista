package com.shortList.app.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.shortList.app.db.DBAdapter;
import com.shortList.app.model.Event;
import com.shortList.app.model.Payment;
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
	
	private static final String LOG_TAG = " PaymentManager";
	
	protected Event activeEvent = new Event();
	protected DBAdapter db;
	protected List<Event> events; 
	
	private static final PaymentManager instance = new PaymentManager();

	private static final String PREFERENCE_ACTIVE_EVENT = "active_event";
	private static final String PREFS_NAME = "ShortListPrefs";


	public PaymentManager() { 
	}
	
	/**
	 * This method have to be called before using class PaymentManger
	 * TODO: make it better
	 * @param context of application using PaymentManager
	 */
	public void init(Context context){
		db = new DBAdapter(context);
		List<Event> events =  db.load();
		
		/**  if in database doesn't exists any saved game, 
		 *   the new event is created and set as an active event */
		if(events.size() == 0){
			long id = db.createNewEvent();
			setActiveEvent(id, context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE));
			this.activeEvent = new Event(id);			
		}else{
			this.events = events;
			SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			long activeEventId = settings.getLong(PREFERENCE_ACTIVE_EVENT, -1);
			this.activeEvent = findEventById(activeEventId);
		}
			
		db.close();
	}
	
	/**
	 * find an event by a given id
	 * @param id of an event
	 * @return null if an event not found
	 */
	public Event findEventById(long id){
		for(Event e : events)
			if (e.getId() == id)
				return e;
		Log.d(LOG_TAG, String.format("Event for id: %d not found!", id));
		return null;
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
	
	protected float getBalanceByPerson(Person person){
		return getBalanceByPersonByEvent(activeEvent,  person);
	}
		
	protected float getBalanceByPersonByEvent(Event event, Person person){
		return getExpensensByPersonByEvent(event, person) - getDebitForEvent(event, person);
	}
	
	protected Map<Person, Float> summarize(Event event){
		Map<Person, Float> results = new HashMap<Person, Float>();
		
		for(Person p : event.getPersons()){
			results.put(p, getBalanceByPersonByEvent(event, p));
			Log.d(LOG_TAG, String.format("Person %s has to pay %f$.", p.getName(), results.get(p)));
		}
		
		return results;
	}
	
	public Map<Person, Float> summarize(){
		return summarize(activeEvent);
	}
	
	protected float getExpensensByPerson(Person person){
		return getExpensensByPersonByEvent(activeEvent, person);
	}
	
	private float getExpensensByPersonByEvent(Event event, Person person){
		float sum = 0;
		for(Payment p : event.getPayments()){
			if (p.getPayer().equals(person))
				sum += p.getCashAmount();
		}		
		return sum;
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

	/**
	 * add a payment to an active event
	 * @param payment to add
	 */
	public void addPayment(Payment payment) {		
		activeEvent.getPayments().add(payment);
	}
	
	public void addPayment(Payment payment, Event event) {
		// TODO Auto-generated method stub
		
	}

	public List<Person> getPersonsFromNames(List<String> debtors) {
		List<Person> persons = new ArrayList<Person>();
		for (String debtor : debtors){
			persons.add(findPersonByName(debtor));
		}
		return persons;
	}
	
	public Person findPersonByName(String name){
		for(Person p : activeEvent.getPersons()){
			if (p.getName().equals(name))
				return p;
		}
		return null;
	}

	private float getDebit(Person person){
		return getDebitForEvent(activeEvent, person);
	}
	
	
	
	private float getDebitForEvent(Event event, Person person){
		float sum = 0;
		for(Payment p : event.getPayments()){
			if (p.isDebtor(person))
				sum += p.getCashAmountProPerson();
		}			
		return sum;
	}
	public Person getSuggestedPerson() {
		return  getSuggestedPersonFromEvent(activeEvent);
	}
	
	public Person getSuggestedPersonFromEvent(Event event) {
		Person person = null;
		float max = 0.0f;
		float temp;
		for (Person p : event.getPersons()){
			temp = getDebitForEvent(event, p);
			if (temp > max){
				person = p;
				temp = max;
			}
		}
		return person;
	}
}
