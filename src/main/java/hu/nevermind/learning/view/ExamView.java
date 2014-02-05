package hu.nevermind.learning.view;

import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import hu.nevermind.learning.entity.Answer;
import hu.nevermind.learning.entity.Question;
import hu.nevermind.learning.entity.Subcategory;
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
	
	private Label problemStatementField;
	private List<Subcategory> categories;
	private Set<Question> questionSet;
	private Iterator<Question> questionsIterator;
	private Question currentQuestion;
	private VerticalLayout answersComponent;
	private List<UserInput> userInputs = new ArrayList<>();
	private Panel questionAndAnswerPanel;
	
	private static class UserInput {
		Question question;
		List<Integer> checkedAnswerIndices;

		private UserInput(Question question, List<Integer> checkedAnswerIndices) {
			this.question = question;
			this.checkedAnswerIndices = checkedAnswerIndices;
		}
	}

	public VerticalLayout createScreen(final UI ui, final VaadinSession session, final List<Subcategory> categories, final int numberOfQuestions) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		this.categories = categories;
		questionSet = new HashSet<>();
		for (final Subcategory cat : categories) {
			questionSet.addAll(questionService.findQuestions(cat, numberOfQuestions));
		}
		questionsIterator = questionSet.iterator();
		questionAndAnswerPanel = new Panel();
		questionAndAnswerPanel.setSizeFull();
		
		currentQuestion = questionsIterator.next();
		fillScreenWithQuestion();
		layout.addComponent(questionAndAnswerPanel);
		layout.setComponentAlignment(questionAndAnswerPanel, Alignment.BOTTOM_CENTER);
		
		final Component buttonComponent = createButtonsComponent();
		layout.addComponent(buttonComponent);
		layout.setComponentAlignment(buttonComponent, Alignment.BOTTOM_CENTER);
		
		layout.addComponent(new Label(Integer.toString(currentQuestion.getiType())));
		return layout;
	}

	private void fillScreenWithQuestion() {
		final VerticalLayout questionAndAnswerLayout = new VerticalLayout();
		questionAndAnswerLayout.setSizeFull();
		
		final Component questionComponent = createQuestionComponent(currentQuestion);
		
		questionAndAnswerLayout.addComponent(questionComponent);
		questionAndAnswerLayout.setComponentAlignment(questionComponent, Alignment.TOP_LEFT);
		questionAndAnswerLayout.setSizeFull();
		
		final Component answerComponent = createAnswersComponent(currentQuestion.getAnswers());
		questionAndAnswerLayout.addComponent(answerComponent);
		questionAndAnswerLayout.setComponentAlignment(answerComponent, Alignment.BOTTOM_LEFT);
		
		questionAndAnswerPanel.setContent(questionAndAnswerLayout);
	}
	
	private Component createQuestionComponent(final Question question) {
		problemStatementField = new Label();
		problemStatementField.setContentMode(ContentMode.HTML);
		problemStatementField.setValue(question.getProblemStatement());
		//problemStatementField.setWidth(500, Sizeable.Unit.PIXELS);
		final String text = question.getProblemStatement();
		int rows = 0;
		String other = "";
		for (int i = 0; i < text.length(); ++i) {
			final char ch = text.charAt(i);
			if (ch == '\n') {
				++rows;
				other += "<br/>";
			} else if (ch == ' ') {
				other += "&nbsp;&nbsp;";
			} else if (ch == '\t') {
				other += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			} else {
				other += ch;
			}
		}
		problemStatementField.setValue(other + "<br/>");
		//problemStatementField.setHeight(rows*15, Sizeable.Unit.PIXELS);
		return new Panel("Problem statment", problemStatementField);
	}
	
	private Component createAnswersComponent(final List<Answer> answers) {
		answersComponent = new VerticalLayout();
		for (final Answer ans : answers) {
			answersComponent.addComponent(new CheckBox(ans.getText()));
		}
		return new Panel("Answers", answersComponent);
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
		final List<Integer> indices = new ArrayList<>();
		for (int i = 0; i < answersComponent.getComponentCount(); ++i){
			final CheckBox cb = (CheckBox) answersComponent.getComponent(i);
			if (cb.getValue()) {
				indices.add(i);
			}
		}
		final UserInput ui = new UserInput(currentQuestion, indices);
		userInputs.add(ui);
		if (questionsIterator.hasNext()) {
			currentQuestion = questionsIterator.next();
			fillScreenWithQuestion();
		} else {
			
		}
		
	}
}
