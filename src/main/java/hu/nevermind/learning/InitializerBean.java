package hu.nevermind.learning;

import java.util.logging.Logger;
import hu.nevermind.learning.entity.Answer;
import hu.nevermind.learning.entity.Category;
import hu.nevermind.learning.entity.Question;
import hu.nevermind.learning.entity.Subcategory;
import hu.nevermind.learning.misc.PrepReader;
import hu.nevermind.learning.service.QuestionService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class InitializerBean {

	private final static Logger logger = Logger.getLogger(InitializerBean.class.getName());

	@PersistenceContext(unitName = "learningPU")
	private EntityManager em;

	@Inject
	QuestionService questionService;

	public void init() {
		if (questionService.allQuestions().isEmpty() == false) {
			return;
		}

		Category ejbCategory = new Category();
		ejbCategory.setName("Enterprise JavaBeans Developer");
		em.persist(ejbCategory);
		// Oracle Certified Expert, Java EE 6 
		em.persist(new Category("Java Persistence API Developer"));
		em.persist(new Category("Web Component Developer"));
		em.persist(new Category("Web Services Developer"));

		em.persist(new Category("Oracle Certified Associate, Java SE 7 Programmer"));
		em.persist(new Category("Oracle Certified Professional, Java SE 7 Programmer"));

		final HashMap<String, Question> questionMap = new HashMap<>();
		final HashMap<String, List<Answer>> answers = new HashMap<>();
		final HashMap<Integer, Subcategory> sectionMap = new HashMap<>();
		final HashMap<Integer, List<String>> sectionToQuestionMap = new HashMap<>();
		final List<HashMap> maps = PrepReader.getMaps();
		int i = 0;
		for (HashMap database : maps) {
			final String cn = (String) database.get("cn");
			if ("so.Question".equals(cn)) {
				for (Object[] entry : (ArrayList<Object[]>) database.get("data")) {
					final Question entity = new Question();
					entity.setToughness((Integer) entry[1]);
					entity.setProblemStatement((String) entry[2]);
					entity.setIsProblemStatementInHtml((Boolean) entry[3]);
					entity.setExplanation((String) entry[7]);
					entity.setIsExplanationInHtml((Boolean) entry[6]);
					entity.setiType((Integer) entry[13]);
					questionMap.put((String) entry[0], entity);
				}
			} else if ("questionspecificpart".equals(cn)) {
				for (Object[] entry : (ArrayList<Object[]>) database.get("data")) {
					final Answer entity = new Answer();
					entity.setText((String) entry[1]);
					entity.setComment((String) entry[2]);
					entity.setCorrect((Boolean) entry[3]);
					entity.setIsTextInHtml((Boolean) entry[4]);

					String qid = (String) entry[0];
					List<Answer> answerList = answers.get(qid);
					if (answerList == null) {
						answerList = new ArrayList<>();
						answers.put(qid, answerList);
					}
					answerList.add(entity);
				}
			} else if ("so.Section".equals(cn)) {
				for (Object[] entry : (ArrayList<Object[]>) database.get("data")) {
					final Subcategory entity = new Subcategory();
					entity.setName((String) entry[1]);
					sectionMap.put((Integer) entry[0], entity);
				}
			} else if ("qsmap".equals(cn)) {
				for (Object[] entry : (ArrayList<Object[]>) database.get("data")) {
					final Integer sectionId = (Integer) entry[1];
					List<String> questionList = sectionToQuestionMap.get(sectionId);
					if (questionList == null) {
						questionList = new ArrayList<>();
						sectionToQuestionMap.put(sectionId, questionList);
					}
					questionList.add((String) entry[0]);
				}
			}
		}
		for (final Integer sectionId : sectionMap.keySet()) {
			final Subcategory section = sectionMap.get(sectionId);
			section.setCategory(ejbCategory);
			em.persist(section);
			final List<String> qids = sectionToQuestionMap.get(sectionId);
			for (final String qid : qids) {
				final Question question = questionMap.get(qid);
				question.setCategory(section);
				final List<Answer> answerList = answers.get(qid);
				if (answerList == null) {
					logger.log(Level.WARNING, "No answers for: {0}\n Question won't be persisted!", question.getProblemStatement());
					continue;
				}
				em.persist(question);
				for (Answer ans : answerList) {
					ans.setQuestion(question);
					em.persist(ans);
				}
			}
		}
	}
}
