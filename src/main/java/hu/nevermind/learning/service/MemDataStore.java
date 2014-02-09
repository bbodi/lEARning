package hu.nevermind.learning.service;

import hu.nevermind.learning.entity.Answer;
import hu.nevermind.learning.entity.BaseEntity;
import hu.nevermind.learning.entity.Category;
import hu.nevermind.learning.entity.Question;
import hu.nevermind.learning.entity.Subcategory;
import hu.nevermind.learning.entity.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.Stateful;
import javax.inject.Singleton;

@Singleton
@MemoryDataStore
public class MemDataStore implements DataStore {

	List<Category> categories = new ArrayList<>();

	List<Subcategory> subCategories = new ArrayList<>();

	List<Question> questions = new ArrayList<>();

	List<Answer> answers = new ArrayList<>();

	List<User> users = new ArrayList<>();
	
	long idCounter = 0;

	@Override
	public List<Category> findAllCategory() {
		return Collections.unmodifiableList(categories);
	}

	@Override
	public int allCategoriesCount() {
		return categories.size();
	}

	@Override
	public List<Question> allQuestions() {
		return Collections.unmodifiableList(questions);
	}

	@Override
	public List<Question> findQuestions(final Subcategory cat) {
		final List<Question> matchingQuestions = new ArrayList<>();
		for (final Question q : questions) {
			if (q.getCategory().getId() == cat.getId()) {
				matchingQuestions.add(q);
			}
		}
		return matchingQuestions;
	}

	@Override
	public List<User> allUsers() {
		return users;
	}

	@Override
	public User getUser(final String username) {
		for (final User user : users) {
			if (user.getName().equals(username)) {
				return user;
			}
		}
		return null;
	}

	@Override
	public <T extends BaseEntity> void persist(final T entity) {
		if (entity instanceof Answer) {
			answers.add((Answer) entity);
		} else if (entity instanceof Question) {
			questions.add((Question) entity);
		} if (entity instanceof User) {
			users.add((User) entity);
		} else if (entity instanceof Category) {
			categories.add((Category) entity);
		} else if (entity instanceof Subcategory) {
			subCategories.add((Subcategory) entity);
		}
		if (entity.getId() == null) {
			entity.setId(idCounter);
			idCounter++;
		}
	}

	@Override
	public void flush() {
		// nothing to do
	}
}
