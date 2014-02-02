package hu.nevermind.learning.service;

import hu.nevermind.learning.entity.User;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class UserService {
	@PersistenceContext(unitName="learningPU") 
	private EntityManager em;
	
	public List<User> allUsers() {
		return em.createNamedQuery("User.findAll", User.class).getResultList();
	}

	public User getUser(final String username) {
		final List<User> list = em.createNamedQuery("User.findByName", User.class)
				.setParameter("name", username)
				.getResultList();
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
}
