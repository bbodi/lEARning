package hu.nevermind.learning.view;

import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import hu.nevermind.learning.entity.Answer;
import hu.nevermind.learning.entity.Question;
import hu.nevermind.learning.entity.Subcategory;
import hu.nevermind.learning.service.DataStore;
import hu.nevermind.learning.service.DbDataStore;
import hu.nevermind.learning.service.MemoryDataStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateful;
import javax.inject.Inject;
import org.apache.commons.lang.StringEscapeUtils;

@Stateful
public class ExamView {

	public static class UserInput {

		Question question;
		List<Integer> checkedAnswerIndices;

		private UserInput(Question question, List<Integer> checkedAnswerIndices) {
			this.question = question;
			this.checkedAnswerIndices = checkedAnswerIndices;
		}
	}
	
	public static final String NAME = "EXAM";

	private UI ui;
	private VaadinSession session;

	@Inject
	@MemoryDataStore
	private DataStore dataStore;

	private Label problemStatementField;
	private List<Question> questionsToAsk;
	private Iterator<Question> questionsIterator;
	private Question currentQuestion;
	private VerticalLayout answersComponent;
	private Panel questionAndAnswerPanel;
	private int correctAnswers;
	private Map<Question, UserInput> userInputs;
	private List<Question> questionsAnsweredIncorrectly;
	private boolean checkMode;
	private Button showExplanationButton;

	public VerticalLayout createScreen(final UI ui, final VaadinSession session, final List<Subcategory> categories, final int numberOfQuestions) {
		this.ui = ui;
		this.session = session;
		this.checkMode = false;
		this.userInputs = new HashMap<>();
		this.questionsAnsweredIncorrectly = new ArrayList<>();
		this.correctAnswers = 0;

		final VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		questionsToAsk = loadRandomOrderedQuestions(categories, numberOfQuestions);

		questionAndAnswerPanel = new Panel();
		questionAndAnswerPanel.setSizeFull();

		questionsIterator = questionsToAsk.iterator();
		showNextQuestion();
		layout.addComponent(questionAndAnswerPanel);
		layout.setComponentAlignment(questionAndAnswerPanel, Alignment.BOTTOM_CENTER);

		final Component buttonComponent = createButtonsComponent();
		layout.addComponent(buttonComponent);
		layout.setComponentAlignment(buttonComponent, Alignment.BOTTOM_CENTER);
		return layout;
	}

	private List<Question> loadRandomOrderedQuestions(final List<Subcategory> categories, final int numberOfQuestions) {
		final List<Question> questionsToAsk = new ArrayList<>();
		for (final Subcategory cat : categories) {
			final List<Question> allQuestions = dataStore.findQuestions(cat);
			Collections.shuffle(allQuestions);
			final int limit = numberOfQuestions > allQuestions.size() ? allQuestions.size() : numberOfQuestions;
			final List<Question> limitedNumberQuestion = allQuestions.subList(0, limit);
			randomieAnswerOrder(limitedNumberQuestion);
			questionsToAsk.addAll(limitedNumberQuestion);
		}
		Collections.shuffle(questionsToAsk);
		return questionsToAsk;
	}

	private void randomieAnswerOrder(final List<Question> limitedNumberQuestion) {
		for (Question q : limitedNumberQuestion) {
			Collections.shuffle(q.getAnswers());
		}
	}
	
	private void showSummaryWindow() {
		final Window summaryWindow = new Window("Summary");
		summaryWindow.setModal(true);
		summaryWindow.setClosable(false);
		summaryWindow.setResizable(false);
		final VerticalLayout windowLayout = new VerticalLayout();
		final Label label = new Label("Score: " + correctAnswers + "/" + questionsToAsk.size());
		windowLayout.addComponent(label);
		windowLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);

		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setMargin(true);
		buttonLayout.setSpacing(true);

		if (checkMode == false) {
			final Button checkBtn = new Button("Check", new Button.ClickListener() {
				@Override
				public void buttonClick(final ClickEvent event) {
					summaryWindow.close();
					checkMode = true;
					showExplanationButton.setVisible(true);
					questionsIterator = questionsAnsweredIncorrectly.iterator();
					showNextQuestion();
				}
			});
			buttonLayout.addComponent(checkBtn);
		}

		final Button homeBtn = new Button("Go Home", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				summaryWindow.close();
				ui.getNavigator().navigateTo(MainView.NAME);
			}
		});

		buttonLayout.addComponent(homeBtn);
		windowLayout.addComponent(buttonLayout);
		summaryWindow.setContent(windowLayout);
		summaryWindow.center();
		this.ui.addWindow(summaryWindow);
	}

	private void showNextQuestion() {
		if (questionsIterator.hasNext() == false) {
			showSummaryWindow();
			return;
		}
		currentQuestion = questionsIterator.next();
		setExplanationButtonVisibility();
		final VerticalLayout questionAndAnswerLayout = new VerticalLayout();

		final Component questionComponent = createQuestionComponent(currentQuestion);
		questionComponent.setWidth(1000, Sizeable.Unit.PIXELS);
		questionComponent.setHeight(300, Sizeable.Unit.PIXELS);

		questionAndAnswerLayout.addComponent(questionComponent);
		questionAndAnswerLayout.setComponentAlignment(questionComponent, Alignment.TOP_LEFT);

		final Component answerComponent = createAnswersComponent(currentQuestion.getAnswers());
		answerComponent.setWidth(1000, Sizeable.Unit.PIXELS);
		answerComponent.setHeight(200, Sizeable.Unit.PIXELS);
		questionAndAnswerLayout.addComponent(answerComponent);
		questionAndAnswerLayout.setComponentAlignment(answerComponent, Alignment.BOTTOM_LEFT);

		questionAndAnswerPanel.setContent(questionAndAnswerLayout);
	}

	private void setExplanationButtonVisibility() {
		if (checkMode) {
			boolean hasExplanation = currentQuestion.getExplanation() != null && currentQuestion.getExplanation().isEmpty() == false;
			showExplanationButton.setVisible(hasExplanation);
		}
	}

	private Component createQuestionComponent(final Question question) {
		problemStatementField = new Label();
		String problemStatement = question.getProblemStatement();
		if (question.isIsProblemStatementInHtml() == false) {
			problemStatement = StringEscapeUtils.escapeHtml(problemStatement);
		}
		problemStatementField.setContentMode(ContentMode.HTML);
		final String problemStatementText = convertHilightedToHtml(problemStatement);
		problemStatementField.setValue(problemStatementText);
		final Panel panel = new Panel("Problem statment", problemStatementField);
		return panel;
	}

	private String convertHilightedToHtml(final String text) {
		String other = "";
		int rowLength = 0;
		int javaSyntaxSeparatorCount = 0;
		for (int i = 0; i < text.length(); ++i) {
			final char ch = text.charAt(i);
			if (ch == '\n') {
				other += "<br/>";
				rowLength = 0;
				javaSyntaxSeparatorCount = 0;
			} else if (ch == ' ') {
				other += "&nbsp;";
				rowLength += 1;
				javaSyntaxSeparatorCount = 0;
			} else if (ch == '\t') {
				rowLength += 4;
				other += "&nbsp;&nbsp;&nbsp;&nbsp;";
				javaSyntaxSeparatorCount = 0;
			} else if (ch == '/') {
				other += '/';
				javaSyntaxSeparatorCount++;
			} else {
				rowLength += 1;
				other += ch;
				javaSyntaxSeparatorCount = 0;
			}
			if (rowLength > 140 && javaSyntaxSeparatorCount == 0) {
				other += "<br/>";
				rowLength = 0;
			}
		}
		other = syntaxHighlight(other);
		return other;
	}

	private Component createAnswersComponent(final List<Answer> answers) {
		answersComponent = new VerticalLayout();
		int answerIndex = 0;
		for (final Answer ans : answers) {
			final String answer = StringEscapeUtils.escapeHtml(ans.getText());
			String syntaxHilitedAnswer = syntaxHighlight(answer);
			final CheckBox checkbox = new CheckBox("");
			if (checkMode) {
				final UserInput userInputFotThisQuestion = userInputs.get(ans.getQuestion());
				final List<Integer> checkedByUserIndices = userInputFotThisQuestion.checkedAnswerIndices;
				final boolean checkedByUser = checkedByUserIndices.contains(answerIndex);
				checkbox.setValue(checkedByUser);
				final boolean shouldHaveChecked = ans.isCorrect();
				if (shouldHaveChecked) {
					syntaxHilitedAnswer = "<span style='color:green;'>" + syntaxHilitedAnswer + "</span>";
				} else if (checkedByUser) {
					syntaxHilitedAnswer = "<span style='color:red;'>" + syntaxHilitedAnswer + "</span>";
				}
			}
			checkbox.setReadOnly(checkMode);
			answersComponent.addComponent(new HorizontalLayout(checkbox, new Label(syntaxHilitedAnswer, ContentMode.HTML)));
			++answerIndex;
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
		showExplanationButton = new Button("Explanation", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				final Window explanationWindow = new Window("Explanation");
				explanationWindow.setModal(true);
				explanationWindow.setClosable(false);
				explanationWindow.setResizable(false);
				final VerticalLayout windowLayout = new VerticalLayout();
				final Component expkanationComponent = creatEexplanationComponent(currentQuestion);
				windowLayout.addComponent(expkanationComponent);
				windowLayout.setComponentAlignment(expkanationComponent, Alignment.MIDDLE_CENTER);

				final Button okBtn = new Button("Close", new Button.ClickListener() {
					@Override
					public void buttonClick(final ClickEvent event) {
						explanationWindow.close();
					}
				});
				windowLayout.addComponent(okBtn);
				explanationWindow.setContent(windowLayout);
				explanationWindow.center();
				ui.addWindow(explanationWindow);
			}
		});
		showExplanationButton.setVisible(false);
		layout.addComponent(showExplanationButton);
		return layout;
	}

	private void onOkBtn() throws UnsupportedOperationException {
		if (checkMode == false) {
			final List<Integer> indices = processUserInput();
			final UserInput ui = new UserInput(currentQuestion, indices);
			userInputs.put(currentQuestion, ui);
		}
		showNextQuestion();
	}

	private List<Integer> processUserInput() throws IndexOutOfBoundsException {
		final List<Integer> indices = new ArrayList<>();
		boolean correct = true;
		for (int i = 0; i < answersComponent.getComponentCount(); ++i) {
			final HorizontalLayout horizontalLayoutComponent = (HorizontalLayout) answersComponent.getComponent(i);
			final CheckBox cb = (CheckBox) horizontalLayoutComponent.getComponent(0);
			final boolean checked = cb.getValue();
			if (checked) {
				indices.add(i);
			}
			final Answer answer = currentQuestion.getAnswers().get(i);
			final boolean shouldHaveChecked = answer.isCorrect();
			if ((checked && !shouldHaveChecked) || (!checked && shouldHaveChecked)) {
				correct = false;
			}
		}
		if (correct) {
			correctAnswers++;
		} else {
			questionsAnsweredIncorrectly.add(currentQuestion);
		}
		return indices;
	}

	private Component creatEexplanationComponent(final Question question) {
		String explanation = question.getExplanation();
		if (question.isIsExplanationInHtml() == false) {
			explanation = StringEscapeUtils.escapeHtml(explanation);
		}
		final Label label = new Label(convertHilightedToHtml(explanation));
		label.setContentMode(ContentMode.HTML);
		final Panel panel = new Panel("", label);
		return panel;
	}
	
	static String syntaxHighlight(final String text) {
		final StringBuilder sb = new StringBuilder(text.length());
		int lastIndex = 0;
		while (true) {
			final int startJavaTextIndex = text.indexOf("///", lastIndex);
			if (startJavaTextIndex == -1) {
				break;
			}
			int endJavaTextIndex = getEndingIndex(text, startJavaTextIndex);
			sb.append(text.substring(lastIndex, startJavaTextIndex));
			final String syntaxHighlighted = colorizeJavaKeywords(text.substring(startJavaTextIndex + 3, endJavaTextIndex));
			sb.append(syntaxHighlighted);
			lastIndex = endJavaTextIndex + 3;
		}
		if (lastIndex < text.length()) {
			sb.append(text.substring(lastIndex));
		}
		return sb.toString();
	}

	private static int getEndingIndex(final String text, final int startJavaTextIndex) {
		int endJavaTextIndex = text.indexOf("///", startJavaTextIndex + 3);
		if (endJavaTextIndex == -1) {
			endJavaTextIndex = text.length();
		}
		return endJavaTextIndex;
	}

	private static String colorizeJavaKeywords(String javaText) {
		javaText = javaText.replaceAll("(\\b[0-9]+\\b)", "<span style='color:red;'>$1</span>");

		//javaText = javaText.replaceAll("([\\.\\+\\-\\;\\(\\)\\[\\]\\=]+)", "<span style='text-weight: bold;'>$1</span>");
		javaText = javaText.replaceAll("@", "<span style='color:purple;'>@</span>");

		javaText = javaText.replaceAll("\\babstract\\b", "<span style='color:blue;'>abstract</span>");
		javaText = javaText.replaceAll("\\bcontinue\\b", "<span style='color:blue;'>continue</span>");
		javaText = javaText.replaceAll("\\bfor\\b", "<span style='color:blue;'>for</span>");
		javaText = javaText.replaceAll("\\bnew\\b", "<span style='color:blue;'>new</span>");
		javaText = javaText.replaceAll("\\bswitch\\b", "<span style='color:blue;'>switch</span>");
		javaText = javaText.replaceAll("\\bassert\\b", "<span style='color:blue;'>assert</span>");
		javaText = javaText.replaceAll("\\bdefault\\b", "<span style='color:blue;'>default</span>");
		javaText = javaText.replaceAll("\\bgoto\\b", "<span style='color:blue;'>goto</span>");
		javaText = javaText.replaceAll("\\bpackage\\b", "<span style='color:blue;'>package</span>");
		javaText = javaText.replaceAll("\\bsynchronized\\b", "<span style='color:blue;'>synchronized</span>");
		javaText = javaText.replaceAll("\\bboolean\\b", "<span style='color:blue;'>boolean</span>");
		javaText = javaText.replaceAll("\\bdo\\b", "<span style='color:blue;'>do</span>");
		javaText = javaText.replaceAll("\\bif\\b", "<span style='color:blue;'>if</span>");
		javaText = javaText.replaceAll("\\bprivate\\b", "<span style='color:blue;'>private</span>");
		javaText = javaText.replaceAll("\\bthis\\b", "<span style='color:blue;'>this</span>");
		javaText = javaText.replaceAll("\\bbreak\\b", "<span style='color:blue;'>break</span>");
		javaText = javaText.replaceAll("\\bdouble\\b", "<span style='color:blue;'>double</span>");
		javaText = javaText.replaceAll("\\bimplements\\b", "<span style='color:blue;'>implements</span>");
		javaText = javaText.replaceAll("\\bprotected\\b", "<span style='color:blue;'>protected</span>");
		javaText = javaText.replaceAll("\\bthrow\\b", "<span style='color:blue;'>throw</span>");
		javaText = javaText.replaceAll("\\bbyte\\b", "<span style='color:blue;'>byte</span>");
		javaText = javaText.replaceAll("\\belse\\b", "<span style='color:blue;'>else</span>");
		javaText = javaText.replaceAll("\\bimport\\b", "<span style='color:blue;'>import</span>");

		javaText = javaText.replaceAll("\\bpublic\\b", "<span style='color:blue;'>public</span>");
		javaText = javaText.replaceAll("\\bthrows\\b", "<span style='color:blue;'>throws</span>");
		javaText = javaText.replaceAll("\\bcase\\b", "<span style='color:blue;'>case</span>");
		javaText = javaText.replaceAll("\\benum\\b", "<span style='color:blue;'>enum</span>");
		javaText = javaText.replaceAll("\\binstanceof\\b", "<span style='color:blue;'>instanceof</span>");
		javaText = javaText.replaceAll("\\breturn\\b", "<span style='color:blue;'>return</span>");
		javaText = javaText.replaceAll("\\btransient\\b", "<span style='color:blue;'>transient</span>");
		javaText = javaText.replaceAll("\\bimport\\b", "<span style='color:blue;'>import</span>");

		javaText = javaText.replaceAll("\\bcatch\\b", "<span style='color:blue;'>catch</span>");
		javaText = javaText.replaceAll("\\bextends\\b", "<span style='color:blue;'>extends</span>");
		javaText = javaText.replaceAll("\\bint\\b\\b", "<span style='color:blue;'>int</span>");
		javaText = javaText.replaceAll("\\bshort\\b", "<span style='color:blue;'>short</span>");
		javaText = javaText.replaceAll("\\btry\\b", "<span style='color:blue;'>try</span>");
		javaText = javaText.replaceAll("\\bchar\\b", "<span style='color:blue;'>char</span>");
		javaText = javaText.replaceAll("\\bfinal\\b", "<span style='color:blue;'>final</span>");
		javaText = javaText.replaceAll("\\binterface\\b", "<span style='color:blue;'>interface</span>");
		javaText = javaText.replaceAll("\\bstatic\\b", "<span style='color:blue;'>static</span>");
		javaText = javaText.replaceAll("\\bvoid\\b", "<span style='color:blue;'>void</span>");

		javaText = javaText.replaceAll("\\bclass\\b", "<span style='color:blue;'>class</span>");
		javaText = javaText.replaceAll("\\bfinally\\b", "<span style='color:blue;'>finally</span>");
		javaText = javaText.replaceAll("\\blong\\b", "<span style='color:blue;'>long</span>");
		javaText = javaText.replaceAll("\\bstrictfp\\b", "<span style='color:blue;'>strictfp</span>");
		javaText = javaText.replaceAll("\\bvolatile\\b", "<span style='color:blue;'>volatile</span>");
		javaText = javaText.replaceAll("\\bconst\\b", "<span style='color:blue;'>const</span>");
		javaText = javaText.replaceAll("\\bfloat\\b", "<span style='color:blue;'>float</span>");
		javaText = javaText.replaceAll("\\bnative\\b", "<span style='color:blue;'>native</span>");

		javaText = javaText.replaceAll("\\bsuper\\b", "<span style='color:blue;'>super</span>");
		javaText = javaText.replaceAll("\\bwhile\\b", "<span style='color:blue;'>while</span>");

		return "<span style='font-family: Courier;'>" + javaText + "</span>";
	}
}
