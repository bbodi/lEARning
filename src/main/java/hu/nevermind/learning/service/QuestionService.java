package hu.nevermind.learning.service;

import hu.nevermind.learning.entity.Question;
import java.util.List;
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

	
}
