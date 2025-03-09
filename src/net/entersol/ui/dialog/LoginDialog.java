package net.entersol.ui.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LoginDialog extends Composite {
	private String defaultText = "";
	private DialogBox dialog = new DialogBox();
	private static PromptUiBinder uiBinder = GWT.create(PromptUiBinder.class);

	interface PromptUiBinder extends UiBinder<Widget, LoginDialog> {}

	@UiField Label promptMessage;
	@UiField TextBox userName;
	@UiField PasswordTextBox password;
	@UiField Label userNameLable;
	@UiField Label passwordNameLable;
	
	@UiField Button loginButton;
	@UiField Button cancelButton;
	@UiField CheckBox checkBox;
	
	public LoginDialog(){
		creatUI("Login", "Please Enter User name and password", "");
	}

	public LoginDialog(String title, String message){
		creatUI(title, message, "");
	}
	public LoginDialog(String title, String message, String defaultText){
		creatUI(title, message, defaultText);
	}
	private void creatUI(String title, String message, String defaultText){
		initWidget(uiBinder.createAndBindUi(this));
		this.defaultText = defaultText;
		setMessage(message);
		setTitle(title);
		setFocuse(userName);
		dialog.add(this);
	}

	public void setMessage(String text){
		promptMessage.setText(text);
	}
	public void setTitle(String title){
		dialog.setText(title);
	}
	public void setGlassEnabled(boolean enabled){
		dialog.setGlassEnabled(enabled);
	}
	public void setTextBoxLength(String length){
		userName.setSize(length, "");
		password.setSize(length, "");
	}
	public void setButtonLabels(String login, String cancel){
		loginButton.setText(login);
		cancelButton.setText(cancel);
	}
	public void setCheckBox(boolean ch){
		checkBox.setValue(ch);
	}
	public boolean getCheckBox(){
		return checkBox.getValue();
	}
	public void setCheckBoxLable(String lbl){
		checkBox.setText(lbl);
	}
	public void setUserPassword(String u, String p){
		userName.setValue(u);
		password.setText(p);
	}
	public void setUserNameLable(String l){
		userNameLable.setText(l);
	}
	public void setUserPasswordLable(String l){
		passwordNameLable.setText(l);
	}
	@UiHandler({"userName", "password"})
	void onTextReturn(KeyPressEvent e) {
		if(e.getUnicodeCharCode() == 13){
			hide();
			onLogin(userName.getText(), password.getText());
		}
	}
	@UiHandler("loginButton")
	void onOkClick(ClickEvent e) {
		hide();
		onLogin(userName.getText(), password.getText());
	}
	@UiHandler("cancelButton")
	void onCancelClick(ClickEvent e) {
		hide();
		onCancel();
	}
	public void onLogin(String u, String p){}
	public void onCancel(){}

	public void show(){
		dialog.center();
		userName.setText(this.defaultText);
		userName.selectAll();
	}
	public void show(String message){
		setMessage(message);
		dialog.center();
		userName.setText(this.defaultText);
		userName.selectAll();
	}
	public void show(String message, String defaultText){
		setMessage(message);
		dialog.center();
		userName.setText(defaultText);
		userName.selectAll();
	}
	
	public void hide(){
		dialog.hide();
	}

	private void setFocuse(final TextBox txt){
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
		    public void execute () {
		    	txt.setFocus(true);
		    }
		   });	
	}

}
