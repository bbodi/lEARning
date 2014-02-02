package hu.nevermind.learning.entity;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author sharp
 */
@Entity
public class Subcategory extends BaseEntity {
	@OneToMany(mappedBy = "category")
	private List<Question> questions;
	
	private String name;
	
	@ManyToOne
	private Category category;

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
	
	
}
