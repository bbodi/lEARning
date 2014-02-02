package hu.nevermind.learning.service;

import hu.nevermind.learning.entity.Category;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class CategoryService {
	@PersistenceContext(unitName="learningPU") 
	private EntityManager em;
	
	public List<Category> findAll() {
		return em.createNamedQuery("Category.findAll", Category.class).getResultList();
	}
}
