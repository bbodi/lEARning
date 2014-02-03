package hu.nevermind.learning.view;

import com.vaadin.cdi.CDIView;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.Action;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import hu.nevermind.learning.entity.Category;
import hu.nevermind.learning.entity.Subcategory;
import hu.nevermind.learning.service.CategoryService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

@CDIView(value = ExamView.NAME)
public class ExamView extends CustomComponent implements View {

	public static final String NAME = "EXAM";

	private Label text = new Label();

	private TextArea problemStatementArea = new TextArea();

	@Inject
	private CategoryService categoryService;

	Button logout = new Button("Logout", new Button.ClickListener() {

		@Override
		public void buttonClick(ClickEvent event) {

			// "Logout" the user
			getSession().setAttribute("user", null);

			// Refresh this view, should redirect to login view
			getUI().getNavigator().navigateTo(NAME);
		}
	});

	public ExamView() {
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

	private Component initBody() {
		final VerticalLayout center = new VerticalLayout();
		
		return center;
	}




	private Component initNews() {
		final VerticalLayout lay = new VerticalLayout();
		return lay;
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		final VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.addComponent(initHeader());
		mainLayout.addComponent(initBody());

		setCompositionRoot(mainLayout);
		final List<Subcategory> categories = (List<Subcategory>) getSession().getAttribute("categories");
		final int numberOfQuestionsPerCategory = (int) getSession().getAttribute("numberOfQuestionsPerCategory");
	}

}
