package hu.nevermind.learning;


import hu.nevermind.learning.service.UserService;
import com.vaadin.annotations.Theme;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import hu.nevermind.learning.view.DashBoardView;
import hu.nevermind.learning.view.LoginView;
import javax.inject.Inject;

@Theme("mytheme")
@SuppressWarnings("serial")
@CDIUI
public class MyVaadinUI extends UI {

	@Inject
	private UserService userService;
	
	@Inject
    CDIViewProvider navigatorViewProvider;
	
	private Navigator navigator;

	@Override
	protected void init(VaadinRequest request) {

		navigator = new Navigator(this, this);
		navigator.addProvider(navigatorViewProvider);
		getNavigator().addView(LoginView.NAME, LoginView.class);
		getNavigator().addView(DashBoardView.NAME, DashBoardView.class);
		getNavigator().addViewChangeListener(new ViewChangeListener() {

			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {
				boolean isLoggedIn = getSession().getAttribute("user") != null;
				boolean isLoginView = event.getNewView() instanceof LoginView;

				if (!isLoggedIn && !isLoginView) {
					getNavigator().navigateTo(LoginView.NAME);
					return false;
				} else if (isLoggedIn && isLoginView) {
					return false;
				}
				return true;
			}

			@Override
			public void afterViewChange(ViewChangeEvent event) {

			}
		});
	}

}
