package hu.nevermind.learning.view;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import hu.nevermind.learning.entity.Subcategory;
import java.util.List;
import javax.inject.Inject;

@CDIView(value = MainView.NAME)
public class MainView extends CustomComponent implements View {

	public static final String NAME = "";
	
	@Inject
	private DashBoardView dashBoardView;
	
	@Inject
	private ExamView examView;

	Button logout = new Button("Logout", new Button.ClickListener() {

		@Override
		public void buttonClick(ClickEvent event) {
			getSession().setAttribute("user", null);
			getUI().getNavigator().navigateTo(NAME);
		}
	});
	private Panel mainPanel;

	public MainView() {
	}

	private Component initHeader() {
		final HorizontalLayout headerLay = new HorizontalLayout();
		final Label title = new Label("lEARning");
		title.setSizeFull();
		headerLay.addComponent(title);
		headerLay.addComponent(logout);
		headerLay.setExpandRatio(title, 4);
		headerLay.setExpandRatio(logout, 1);
		return headerLay;
	}

	private Component initNews() {
		final VerticalLayout lay = new VerticalLayout();
		return lay;
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		final VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.addComponent(initHeader());
		final HorizontalLayout bodyLayout = new HorizontalLayout();
		final String parameters = event.getParameters();
		mainPanel = new Panel();
		if (parameters == null || parameters.isEmpty()) {
			final Component center = dashBoardView.createScreen(getUI(), getSession());
			mainPanel.setContent(center);
		} else if ("exam".equals(parameters)){
			final List<Subcategory> categories = (List<Subcategory>) getSession().getAttribute("categories");
			final int numberOfQuestions = (int) getSession().getAttribute("numberOfQuestionsPerCategory");
			final Component center = examView.createScreen(getUI(), getSession(), categories, numberOfQuestions);
			mainPanel.setContent(center);
		} else {
			throw new IllegalArgumentException(parameters);
		}
		
		mainPanel.setWidth(70, Sizeable.Unit.PERCENTAGE);
		
		bodyLayout.addComponent(mainPanel);
		bodyLayout.setComponentAlignment(mainPanel, Alignment.MIDDLE_CENTER);
		
		bodyLayout.addComponent(initNews());
		
		mainLayout.addComponent(bodyLayout);
		mainLayout.setComponentAlignment(bodyLayout, Alignment.MIDDLE_CENTER);

		setCompositionRoot(mainLayout);
		// Get the user name from the session
		String username = String.valueOf(getSession().getAttribute("user"));
	}

}
