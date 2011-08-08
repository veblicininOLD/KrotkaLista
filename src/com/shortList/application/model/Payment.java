package com.shortList.application.model;

import java.util.Date;
import java.util.List;

public class Payment extends Entity {

	/** cash value of a payment **/
	private float cash;
	/** date of execution of payment **/
	private Date date;
	/** person who payer a payment **/
	private Person payer; 
	/** persons who were payed for **/
	private List<Person> debtors;
	/** comments for a payment **/
	private String description; 
	
	public Payment(long id, float cash, Date date, Person payer,
			List<Person> debtors, String description) {
		super(id);
		this.cash = cash;
		this.date = date;
		this.payer = payer;
		this.debtors = debtors;
		this.description = description;
	}

	public boolean isDebtor(Person person){
		return debtors.contains(person);		
	}
	
	public Date getDate() {
		return date;
	}
	public Person getPayer() {
		return payer;
	}
	public List<Person> getDebtors() {
		return debtors;
	}
	public String getDescription() {
		return description;
	}
	public float getCash() {
		return cash;
	} 
	
	
	
}
