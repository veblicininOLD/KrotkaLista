package com.shortList.application.model;

import java.util.List;

public class Event extends Entity {

	private List<Payment> payments;
	private List<Person> persons;	 
	
	public Event(long id, List<Payment> payments, List<Person> persons) {
		super(id);
		this.payments = payments;
		this.persons = persons;			
	}

	public float calculateDebit(Person person){
		float value = 0;
		
		for(Payment payment : payments){
			if (payment.isDebtor(person))
				value += payment.getCash();
		}		
		return value;
	}
		
	public float calculateExpenses(Person person){
		float value = 0;
		
		for(Payment payment : payments){
			if (payment.getPayer() == person)
				value += payment.getCash();
		}		
		return value;
	}
	
	public float distributeCommonExpenses(){
		float value = 0;
		
		return value;
	}
}
