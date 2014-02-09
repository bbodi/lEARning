package hu.nevermind.learning.service;

import hu.nevermind.learning.entity.BaseEntity;
import hu.nevermind.learning.entity.Category;
import hu.nevermind.learning.entity.Question;
import hu.nevermind.learning.entity.Subcategory;
import hu.nevermind.learning.entity.User;
import java.util.List;

public interface DataStore {

	int allCategoriesCount();

	List<Question> allQuestions();

	List<User> allUsers();

	List<Category> findAllCategory();

	List<Question> findQuestions(final Subcategory cat);

	User getUser(final String username);

	<T extends BaseEntity> void persist(final T entity);

	public void flush();
	
}
