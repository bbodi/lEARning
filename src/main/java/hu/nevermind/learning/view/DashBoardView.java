package hu.nevermind.learning.view;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
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
import hu.nevermind.learning.service.DataStore;
import hu.nevermind.learning.service.DbDataStore;
import hu.nevermind.learning.service.MemoryDataStore;
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

	private TextField numberOfQuestionsPerCategory;

	@Inject
	@MemoryDataStore
	private DataStore dataStore;

	private List<Table> tables;

	public VerticalLayout createScreen(final UI ui, final VaadinSession session) {
		this.session = session;
		this.ui = ui;
		this.tables = new ArrayList<>();
		final Component tablesComponent = initCategoriesTable();
		final VerticalLayout center = new VerticalLayout();
		center.addComponent(tablesComponent);
		final HorizontalLayout buttonAndInputField = new HorizontalLayout();
		numberOfQuestionsPerCategory = new TextField("Numbers of questions per category:");
		numberOfQuestionsPerCategory.setValue("10");
		buttonAndInputField.addComponent(numberOfQuestionsPerCategory);
		final Button startButton = createStartButton();
		buttonAndInputField.addComponent(startButton);
		buttonAndInputField.setComponentAlignment(startButton, Alignment.BOTTOM_CENTER);
		center.addComponent(buttonAndInputField);
		return center;
	}

	private Component initCategoriesTable() {
		final GridLayout lay = new GridLayout(3, 3);
		List<Category> categories = dataStore.findAllCategory();
		for (final Category category : categories) {
			final BeanContainer<Long, Subcategory> container = new BeanContainer<>(Subcategory.class);
			container.setBeanIdProperty("name");
			for (final Subcategory subcategory : category.getSubcategorys()) {
				container.addBean(subcategory);
			}
			if (container.size() == 0) {
				continue;
			}
			final Table table = new Table();
			table.setSelectable(true);
			table.setMultiSelect(true);
			table.setSizeFull();
			table.setContainerDataSource(container);
			table.setVisibleColumns(new Object[]{"name"});
			table.setItemDescriptionGenerator(new ItemDescriptionGenerator() {
				@Override
				public String generateDescription(Component source, Object itemId, Object propertyId) {
					return ((BeanItem<Subcategory>)table.getItem(itemId)).getBean().getDescription();
				}
			});
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
		start.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		return start;
	}

	private void startExam() throws NumberFormatException {
		final List<Subcategory> categories = getSelectedSubcategoriesFromAllTable();
		if (categories.isEmpty()) {
			getAllSubcategoryFromAllTable(categories);
		}
		final int numQuestions = Integer.parseInt(numberOfQuestionsPerCategory.getValue());
		session.setAttribute("categories", categories);
		session.setAttribute("numberOfQuestionsPerCategory", numQuestions);
		ui.getNavigator().navigateTo(MainView.NAME + "/" + ExamView.NAME);
	}

	private List<Subcategory> getSelectedSubcategoriesFromAllTable() {
		final List<Subcategory> selectedCategories = new ArrayList<>();
		for (final Table table : tables) {
			final Set<String> selected = (Set) table.getValue();
			for (final String id : selected) {
				selectedCategories.add((Subcategory) ((BeanItem) table.getItem(id)).getBean());
			}
		}
		return selectedCategories;
	}

	private void getAllSubcategoryFromAllTable(List<Subcategory> selectedCategories) {
		for (final Table table : tables) {
			for (final Object id : table.getItemIds()) {
				selectedCategories.add((Subcategory) ((BeanItem) table.getItem(id)).getBean());
			}
		}
	}
}
