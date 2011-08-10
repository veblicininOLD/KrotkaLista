package com.shortList.app.control;

import java.util.HashMap;
import java.util.Map;

import com.shortList.app.model.Event;
import com.shortList.app.model.Person;

public class PaymentManager  {

	public static final int NEW_PARTICIPANT = 0;
	
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
	
}
