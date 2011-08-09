package com.shortList.app.model;

import java.util.ArrayList;
import java.util.List;

public class Event extends Entity {

	private List<Payment> payments;
	private List<Person> persons;	 

	public Event(long id, List<Payment> payments, List<Person> persons) {
		super(id);
		this.payments = payments;
		this.persons = persons;			
	}

	public Event() {
		this.payments = new ArrayList<Payment>();
		this.persons = new ArrayList<Person>();
	}

	public float calculateDebit(Person person){
		float value = 0;

		for(Payment payment : payments){
			if (payment.isDebtor(person))
				value += (payment.getCashAmount() / payment.getDebtors().size() );
		}		
		return value;
	}

	public float calculateExpenses(Person person){
		float value = 0;

		for(Payment payment : payments){
			if (payment.getPayer() == person)
				value += payment.getCashAmount();
		}		
		return value;
	}

	public float distributeCommonExpenses(){
		float value = 0;

		return value;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public List<Person> getPersons() {
		return persons;
	}
	
	
	
}