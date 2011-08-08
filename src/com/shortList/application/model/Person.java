package com.shortList.application.model;

public class Person extends Entity {
	
	private String name; 
	
	public Person(long id, String name) {
		super(id);
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
