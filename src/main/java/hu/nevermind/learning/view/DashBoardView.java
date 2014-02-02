package hu.nevermind.learning.view;

import com.vaadin.cdi.CDIView;
import com.vaadin.data.util.BeanContainer;
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
import com.vaadin.ui.VerticalLayout;
import hu.nevermind.learning.entity.Category;
import hu.nevermind.learning.entity.Subcategory;
import hu.nevermind.learning.service.CategoryService;
import java.util.List;
import javax.inject.Inject;

@CDIView(value = DashBoardView.NAME)
public class DashBoardView extends CustomComponent implements View {

	public static final String NAME = "";

	private Label text = new Label();
	
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

	public DashBoardView() {
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
		final HorizontalLayout bodyLayout = new HorizontalLayout();
		bodyLayout.addComponent(initCategoriesTable());
		bodyLayout.addComponent(initNews());
		return bodyLayout;
	}
	
	private Component initNews() {
		final VerticalLayout lay = new VerticalLayout();
		return lay;
	}
	
	private Component initCategoriesTable() {
		final GridLayout lay=  new GridLayout(3, 3);
		List<Category> categories = categoryService.findAll();
		for (final Category category : categories) {
			Table table = new Table(category.getName());
			BeanContainer<Long, Subcategory> container = new BeanContainer<>(Subcategory.class);
			container.setBeanIdProperty("name");
			for (final Subcategory subcategory : category.getSubcategorys()) {
				container.addBean(subcategory);
			}
			table.setSizeFull();
			table.setContainerDataSource(container);
			table.setVisibleColumns( new Object[] {"name"} );
			table.sort(new Object[] {"name"}, new boolean[]{true});
			
			lay.addComponent(table);
		}
		return lay;
	}
	
	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		final VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.addComponent(initHeader());
		mainLayout.addComponent(initBody());
		
		setCompositionRoot(mainLayout);
		// Get the user name from the session
		String username = String.valueOf(getSession().getAttribute("user"));

		// And show the username
		text.setValue("Hello " + username);
	}

}
