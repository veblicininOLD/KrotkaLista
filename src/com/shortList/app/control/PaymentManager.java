package com.shortList.app.control;

import java.util.HashMap;
import java.util.Map;

import com.shortList.app.model.Event;
import com.shortList.app.model.Person;

public class PaymentManager {

	protected Event activeEvent = new Event();
	//protected List<Event> 
	
	private static final PaymentManager instance = new PaymentManager();

	public PaymentManager() {
		// load all from database
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
	
}
