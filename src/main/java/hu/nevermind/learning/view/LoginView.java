package hu.nevermind.learning.view;


import com.vaadin.cdi.CDIView;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import hu.nevermind.learning.InitializerBean;
import hu.nevermind.learning.service.UserService;
import hu.nevermind.learning.entity.User;
import javax.inject.Inject;

@CDIView(value = LoginView.NAME)
public class LoginView extends CustomComponent implements View, Button.ClickListener {

    public static final String NAME = "login";
	
    private final TextField userNameField;
    private final PasswordField passwordField;
    private final Button loginButton;
	
	@Inject
	private UserService userService;
	
	@Inject
	private InitializerBean initializerBean;

    public LoginView() {
        setSizeFull();
        // Create the user input field
        userNameField = new TextField("User:");
        userNameField.setWidth("300px");
        userNameField.setRequired(true);
        userNameField.setInputPrompt("Your username (eg. joe@email.com)");
        //userNameField.addValidator(new EmailValidator("Username must be an email address"));
        userNameField.setInvalidAllowed(false);

        // Create the password input field
        passwordField = new PasswordField("Password:");
        passwordField.setWidth("300px");
        //passwordField.addValidator(new PasswordValidator());
        //passwordField.setRequired(true);
        passwordField.setValue("");
        passwordField.setNullRepresentation("");
		passwordField.setEnabled(false);

        // Create login button
        loginButton = new Button("Login", this);

        // Add both to a panel
        VerticalLayout fields = new VerticalLayout(userNameField, passwordField, loginButton);
        fields.setCaption("Please login to access the application. (test@test.com/passw0rd)");
        fields.setSpacing(true);
        fields.setMargin(new MarginInfo(true, true, true, false));
        fields.setSizeUndefined();

        // The view root layout
        VerticalLayout viewLayout = new VerticalLayout(fields);
        viewLayout.setSizeFull();
        viewLayout.setComponentAlignment(fields, Alignment.MIDDLE_CENTER);
        viewLayout.setStyleName(Reindeer.LAYOUT_BLUE);
        setCompositionRoot(viewLayout);
    }
    
    @Override
    public void enter(ViewChangeEvent event) {
        userNameField.focus();
    }

    private static final class PasswordValidator extends AbstractValidator<String> {

        public PasswordValidator() {
            super("The password provided is not valid");
        }

        @Override
        protected boolean isValidValue(String value) {
            if (value != null
                    && (value.length() < 8 || !value.matches(".*\\d.*"))) {
                return false;
            }
            return true;
        }

        @Override
        public Class<String> getType() {
            return String.class;
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (!userNameField.isValid() || !passwordField.isValid()) {
            return;
        }

        String username = userNameField.getValue();
        String password = this.passwordField.getValue();
		final User user = userService.getUser(username);

        if(user != null){
			if ("bbodi".equals(username)) {
				initializerBean.init();
			}
            getSession().setAttribute("user", user);
            getUI().getNavigator().navigateTo(MainView.NAME);

        } else {
            this.passwordField.setValue(null);
            this.passwordField.focus();
        }
    }
}