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
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import hu.nevermind.learning.entity.Category;
import hu.nevermind.learning.entity.Subcategory;
import hu.nevermind.learning.service.CategoryService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

@CDIView(value = DashBoardView.NAME)
public class DashBoardView extends CustomComponent implements View {

	public static final String NAME = "";

	static final Action ACTION_A = new Action("A");
	static final Action ACTION_B = new Action("B");
	static final Action ACTION_C = new Action("C");

	private Label text = new Label();

	private TextField numberOfQuestionsPerCategory = new TextField("Kérdések száma kategóriánként");

	@Inject
	private CategoryService categoryService;

	private List<Table> tables = new ArrayList<>();

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
		final Component tables = initCategoriesTable();

		final VerticalLayout center = new VerticalLayout();
		center.addComponent(tables);

		final HorizontalLayout buttonAndInputField = new HorizontalLayout();
		buttonAndInputField.addComponent(numberOfQuestionsPerCategory);
		buttonAndInputField.addComponent(createStartButton());
		center.addComponent(buttonAndInputField);

		bodyLayout.addComponent(center);
		bodyLayout.addComponent(initNews());
		return bodyLayout;
	}

	private Button createStartButton() {
		final Button start = new Button("Kezdés", new Button.ClickListener() {

			@Override
			public void buttonClick(final ClickEvent event) {
				List<Subcategory> categories = new ArrayList<>();
				getSelectedSubcategoriesFromAllTable(categories);
				if (categories.isEmpty()) {
					getAllSubcategoryFromAllTable(categories);
				}
				final int numQuestions = Integer.parseInt(numberOfQuestionsPerCategory.getValue());
				getSession().setAttribute("categories", categories);
				getSession().setAttribute("numberOfQuestionsPerCategory", numQuestions);
				getUI().getNavigator().navigateTo(ExamView.NAME);
			}
		});
		return start;
	}

	private void getSelectedSubcategoriesFromAllTable(List<Subcategory> selectedCategories) {
		for (final Table table : tables) {
			final Set<String> selected = (Set) table.getValue();
			for (final String id : selected) {
				selectedCategories.add((Subcategory) ((BeanItem) table.getItem(id)).getBean());
			}
		}
	}

	private void getAllSubcategoryFromAllTable(List<Subcategory> selectedCategories) {
		for (final Table table : tables) {
			for (final Object id : table.getItemIds()) {
				selectedCategories.add((Subcategory) ((BeanItem) table.getItem(id)).getBean());
			}
		}
	}

	private Component initNews() {
		final VerticalLayout lay = new VerticalLayout();
		return lay;
	}

	private Component initCategoriesTable() {
		final GridLayout lay = new GridLayout(3, 3);
		List<Category> categories = categoryService.findAll();
		for (final Category category : categories) {
			final BeanContainer<Long, Subcategory> container = new BeanContainer<>(Subcategory.class);
			container.setBeanIdProperty("name");
			for (final Subcategory subcategory : category.getSubcategorys()) {
				container.addBean(subcategory);
			}
			if (container.size() == 0) {
				continue;
			}
			final Table table = new Table(category.getName());
			table.setSelectable(true);
			table.setMultiSelect(true);
			table.setSizeFull();
			table.setContainerDataSource(container);
			table.setVisibleColumns(new Object[]{"name"});
			table.setColumnHeaders(new String[]{category.getName()});
			table.sort(new Object[]{"name"}, new boolean[]{true});

			table.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target, final Object sender) {
					return new Action[]{ACTION_A, ACTION_B, ACTION_C};
				}

				@Override
				public void handleAction(final Action action, final Object sender, final Object target) {
					if (ACTION_A == action) {
					} else if (ACTION_B == action) {
					} else if (ACTION_C == action) {
					}
				}
			});
			tables.add(table);
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
