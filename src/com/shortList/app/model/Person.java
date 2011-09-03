package com.shortList.app.model;

public class Person extends Entity {

	protected String name;
	protected long eventId;
	//public final static long NEW_PERSON = -1;

	public Person(long id, String name, long eventId) {
		super(id);
		this.name = name;
		this.eventId = eventId;
	}

	public Person(String name) { 
		this.name = name;
	}

	public String getName() {
		return name;
	}
 
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Person))
			return false;
		Person p = (Person) o;
		return p.getName().equals(getName());
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}
}