package com.shortList.application.control;

import java.util.HashMap;
import java.util.Map;

import com.shortList.application.model.Event;
import com.shortList.application.model.Person;

public class PaymentManager {

	/** account the event
	 * 
	 * @param event to account
	 * @return a list of cash to pay for each participant of an event
	 */
	public Map<Person, Float> account(Event event){
		Map<Person, Float> settlement = new HashMap<Person, Float>();
		
		return settlement;
	}
	 
	
	
}
