package hu.nevermind.learning.entity;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQueries({
	@NamedQuery(name = "Question.findAll", query = "SELECT q FROM Question q"),
	@NamedQuery(name = "Question.findBySubcategoryAndLimit", query = "SELECT q FROM Question q WHERE q.category = :subCategory"),
})
public class Question extends BaseEntity {
	
	@OneToMany(mappedBy = "question")
	private List<Answer> answers;
	
	@Column(nullable = false)
	private int toughness = 0;
	
	@Column(nullable = false, length = 10000)
	private String problemStatement;
	
	@Column(nullable = false)
	private boolean isProblemStatementInHtml;
	
	@Column(length = 10000)
	private String explanation;
	
	@Column(nullable = false)
	private boolean isExplanationInHtml = false;
	
	@ManyToOne
	private Subcategory category;

	private int iType;

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}
	
	

	public Subcategory getCategory() {
		return category;
	}

	public void setCategory(Subcategory category) {
		this.category = category;
	}

	public int getiType() {
		return iType;
	}

	public void setiType(int iType) {
		this.iType = iType;
	}
	
	
	
	public int getToughness() {
		return toughness;
	}

	public void setToughness(int toughness) {
		this.toughness = toughness;
	}

	public String getProblemStatement() {
		return problemStatement;
	}

	public void setProblemStatement(String problemStatement) {
		this.problemStatement = problemStatement;
	}

	public boolean isIsProblemStatementInHtml() {
		return isProblemStatementInHtml;
	}

	public void setIsProblemStatementInHtml(boolean isProblemStatementInHtml) {
		this.isProblemStatementInHtml = isProblemStatementInHtml;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public boolean isIsExplanationInHtml() {
		return isExplanationInHtml;
	}

	public void setIsExplanationInHtml(boolean isExplanationInHtml) {
		this.isExplanationInHtml = isExplanationInHtml;
	}
	
	
}

