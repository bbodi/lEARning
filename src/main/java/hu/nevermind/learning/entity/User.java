package hu.nevermind.learning.entity;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "user_table")
@NamedQueries({
	@NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
	@NamedQuery(name = "User.findByName", query = "SELECT u FROM User u WHERE u.name = :name")
})
public class User extends BaseEntity {
	
	
	private String name;

	public User() {
	}
	
	public User(String username) {
		this.name = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "hu.nevermind.learning.entity.User[ id=" + getId() + " ]";
	}
	
}
