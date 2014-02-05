package hu.nevermind.learning.service;

import hu.nevermind.learning.entity.Question;
import hu.nevermind.learning.entity.Subcategory;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class QuestionService {
	@PersistenceContext(unitName="learningPU") 
	private EntityManager em;
	
	public List<Question> allQuestions() {
		return em.createNamedQuery("Question.findAll", Question.class).getResultList();
	}

	public List<Question> findQuestions(final Subcategory cat, final int maxNumber) {
		final List<Question> list = em.createNamedQuery("Question.findBySubcategoryAndLimit", Question.class)
				.setParameter("subCategory", cat)
				.getResultList();
		final int limit = maxNumber > list.size() ? list.size() : maxNumber;
		return list.subList(0, limit);
	}

	
}
