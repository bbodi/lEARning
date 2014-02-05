package hu.nevermind.learning.view;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.Action;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import hu.nevermind.learning.entity.Category;
import hu.nevermind.learning.entity.Subcategory;
import hu.nevermind.learning.service.CategoryService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateful;
import javax.inject.Inject;

@Stateful
public class DashBoardView {

	static final Action ACTION_A = new Action("A");
	static final Action ACTION_B = new Action("B");
	static final Action ACTION_C = new Action("C");
	
	private UI ui;
	private VaadinSession session;

	private final TextField numberOfQuestionsPerCategory = new TextField("Kérdések száma kategóriánként");

	@Inject
	private CategoryService categoryService;

	private List<Table> tables = new ArrayList<>();

	public VerticalLayout createScreen(final UI ui, final VaadinSession session ) {
		this.session = session;
		this.ui = ui;
		final Component tablesComponent = initCategoriesTable();
		final VerticalLayout center = new VerticalLayout();
		center.addComponent(tablesComponent);
		final HorizontalLayout buttonAndInputField = new HorizontalLayout();
		buttonAndInputField.addComponent(numberOfQuestionsPerCategory);
		final Button startButton = createStartButton();
		buttonAndInputField.addComponent(startButton);
		buttonAndInputField.setComponentAlignment(startButton, Alignment.MIDDLE_CENTER);
		center.addComponent(buttonAndInputField);
		return center;
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
					onTableMenuClick(action);
				}
			});
			tables.add(table);
			lay.addComponent(table);
		}
		return lay;
	}

	private void onTableMenuClick(final Action action) {
		if (ACTION_A == action) {
		} else if (ACTION_B == action) {
		} else if (ACTION_C == action) {
		}
	}

	private Button createStartButton() {
		final Button start = new Button("Kezdés", new Button.ClickListener() {
			@Override
			public void buttonClick(final Button.ClickEvent event) {
				startExam();
			}
		});
		return start;
	}

	private void startExam() throws NumberFormatException {
		List<Subcategory> categories = new ArrayList<>();
		getSelectedSubcategoriesFromAllTable(categories);
		if (categories.isEmpty()) {
			getAllSubcategoryFromAllTable(categories);
		}
		final int numQuestions = Integer.parseInt(numberOfQuestionsPerCategory.getValue());
		session.setAttribute("categories", categories);
		session.setAttribute("numberOfQuestionsPerCategory", numQuestions);
		ui.getNavigator().navigateTo(MainView.NAME + "/exam");
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
}
