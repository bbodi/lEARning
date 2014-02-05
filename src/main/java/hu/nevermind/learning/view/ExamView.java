package hu.nevermind.learning.view;

import com.vaadin.cdi.CDIView;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.Action;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import hu.nevermind.learning.entity.Answer;
import hu.nevermind.learning.entity.Category;
import hu.nevermind.learning.entity.Question;
import hu.nevermind.learning.entity.Subcategory;
import hu.nevermind.learning.service.CategoryService;
import hu.nevermind.learning.service.QuestionService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateful;
import javax.inject.Inject;

@Stateful
public class ExamView {

	public static final String NAME = "EXAM";
	
	@Inject
	private QuestionService questionService;
	
	private TextArea problemStatementField;
	private List<Subcategory> categories;
	private Set<Question> questionSet;
	private int numnumberOfQuestions;
	private Iterator<Question> questionsIterator;
	private Question currentQuestion;

	public VerticalLayout createScreen(final UI ui, final VaadinSession session, final List<Subcategory> categories, final int numnumberOfQuestions) {
		final VerticalLayout layout = new VerticalLayout();
		this.categories = categories;
		this.numnumberOfQuestions = numnumberOfQuestions;
		questionSet = new HashSet<>();
		for (final Subcategory cat : categories) {
			questionSet.addAll(questionService.findQuestions(cat, numnumberOfQuestions));
		}
		questionsIterator = questionSet.iterator();
		currentQuestion = questionsIterator.next();
		layout.addComponent(createQuestionComponent(currentQuestion));
		layout.addComponent(createAnswersComponent(currentQuestion.getAnswers()));
		layout.addComponent(createButtonsComponent());
		layout.addComponent(new Label(Integer.toString(currentQuestion.getiType())));
		return layout;
	}
	
	private Component createQuestionComponent(final Question question) {
		problemStatementField = new TextArea();
		problemStatementField.setEnabled(false);
		problemStatementField.setValue(question.getProblemStatement());
		problemStatementField.setSizeFull();
		return problemStatementField;
	}
	
	private Component createAnswersComponent(final List<Answer> answers) {
		final VerticalLayout layout = new VerticalLayout();
		for (final Answer ans : answers) {
			layout.addComponent(new CheckBox(ans.getText()));
		}
		return layout;
	}
	
	private Component createButtonsComponent() {
		final HorizontalLayout layout = new HorizontalLayout();
		layout.addComponent(new Button("Ok", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				onOkBtn();
			}
		}));
		return layout;
	}

	private void onOkBtn() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
