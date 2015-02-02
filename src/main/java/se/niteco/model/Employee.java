package se.niteco.model;

/**
 * Class Employee. This is the data model that we need to characterize an employee from Niteco.
 */
public class Employee {
	private int id;
	private String name;
	private String email;
	private String team;
	private String role;
	private int salary;
	private City city;
	private String picture;
	
	public Employee (int id, String name, String email, String team, String role, int salary, City city) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.team = team;
		this.role = role;
		this.salary = salary;
		this.city = city;
		this.setPicture(null);
	}
	
	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	public int getSalary() {
		return salary;
	}
	
	public void setSalary(int salary) {
		this.salary = salary;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}
}
