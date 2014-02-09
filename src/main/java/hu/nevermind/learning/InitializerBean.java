package hu.nevermind.learning;

import java.util.logging.Logger;
import hu.nevermind.learning.entity.Answer;
import hu.nevermind.learning.entity.Category;
import hu.nevermind.learning.entity.Question;
import hu.nevermind.learning.entity.Subcategory;
import hu.nevermind.learning.misc.PrepReader;
import hu.nevermind.learning.service.DataStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.Stateless;

/**
 * Nem tudok @Singleton, @Startup-ot használni, mert a Vaadin CDI nem indítja el automatikusan
 */
@Stateless
public class InitializerBean {

	private final static Logger logger = Logger.getLogger(InitializerBean.class.getName());

	public void init(final DataStore dataStore) {
		if (dataStore.allCategoriesCount() > 0) {
			return;
		}

		final Category ejbCategory = new Category("Enterprise JavaBeans Developer");
		final Category jpaCategory = new Category("Java Persistence API Developer");
		final Category webComponentCategory = new Category("Web Component Developer");
		final Category webServicesDeveloper = new Category("Web Services Developer");
		final Category ocaCategory = new Category("Oracle Certified Associate, Java SE 7 Programmer");
		final Category ocjpCategory = new Category("Oracle Certified Professional, Java SE 7 Programmer");
		
		dataStore.persist(webServicesDeveloper);
		dataStore.persist(ocaCategory);
		dataStore.persist(ocjpCategory);
		dataStore.persist(ejbCategory);
		dataStore.persist(jpaCategory);
		dataStore.persist(webComponentCategory);
		
		fillDb(dataStore, ocaCategory, "japv7.ets");
		fillDb(dataStore, ocjpCategory, "jqplusv7.ets");
		fillDb(dataStore, ejbCategory, "ejbplusv6.ets");
		fillDb(dataStore, jpaCategory, "jpapv6.ets");
		fillDb(dataStore, webServicesDeveloper, "jwspv6.ets");
		fillDb(dataStore, webComponentCategory, "jwpv6.ets");
		dataStore.flush();
	}

	private void fillDb(final DataStore dataStore, final Category category, final String fileName) {
		final HashMap<String, Question> questionMap = new HashMap<>();
		final HashMap<String, List<Answer>> answers = new HashMap<>();
		final HashMap<Integer, Subcategory> sectionMap = new HashMap<>();
		final HashMap<Integer, List<String>> sectionToQuestionMap = new HashMap<>();
		final List<HashMap> maps = PrepReader.getMaps(fileName);
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
					entity.setDescription((String) entry[2]);
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
		category.setSubcategorys(new ArrayList<Subcategory>());
		for (final Integer sectionId : sectionMap.keySet()) {
			final Subcategory section = sectionMap.get(sectionId);
			section.setQuestions(new ArrayList<Question>());
			section.setCategory(category);
			category.getSubcategorys().add(section);
			dataStore.persist(section);
			final List<String> qids = sectionToQuestionMap.get(sectionId);
			for (final String qid : qids) {
				final Question question = questionMap.get(qid);
				question.setCategory(section);
				question.setAnswers(new ArrayList<Answer>());
				section.getQuestions().add(question);
				final List<Answer> answerList = answers.get(qid);
				if (answerList == null) {
					logger.log(Level.WARNING, "No answers for: {0}\n Question won't be persisted!", question.getProblemStatement());
					continue;
				}
				dataStore.persist(question);
				for (Answer ans : answerList) {
					ans.setQuestion(question);
					dataStore.persist(ans);
					question.getAnswers().add(ans);
				}
			}
		}
	}
}
