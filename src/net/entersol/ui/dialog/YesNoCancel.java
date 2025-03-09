package net.entersol.ui.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class YesNoCancel extends Composite {
	private DialogBox dialog = new DialogBox();
	private static PromptUiBinder uiBinder = GWT.create(PromptUiBinder.class);

	interface PromptUiBinder extends UiBinder<Widget, YesNoCancel> {}

	@UiField Label promptMessage;
	@UiField Button yesButton;
	@UiField Button noButton;
	@UiField Button cancelButton;

	public YesNoCancel() {
		creatUI("Question", "Message");
	}
	public YesNoCancel(String title, String message) {
		creatUI(title, message);
	}
	private void creatUI(String title, String message){
		initWidget(uiBinder.createAndBindUi(this));
		setMessage(message);
		setTitle(title);
		setFocuse(yesButton);
		dialog.add(this);
	}
	public void setGlassEnabled(boolean enabled){
		dialog.setGlassEnabled(enabled);
	}

	public void setMessage(String text){
		promptMessage.setText(text);
	}
	public void setButtonsLable(String yesBtn, String noBtn, String cancelBtn){
		yesButton.setText(yesBtn);
		noButton.setText(noBtn);
		cancelButton.setText(noBtn);
	}
	public void setTitle(String title){
		dialog.setText("Question");
	}
	@UiHandler("yesButton")
	void onYesClick(ClickEvent e) {
		hide();
		onYes();
	}
	@UiHandler("noButton")
	void onNoClick(ClickEvent e) {
		hide();
		onNo();
	}
	@UiHandler("cancelButton")
	void onCancelClick(ClickEvent e) {
		hide();
		onCancel();
	}
	public void onYes(){}
	public void onNo(){}
	public void onCancel(){}

	public void show(){
		dialog.center();
	}
	public void show(String message){
		setMessage(message);
		dialog.center();
	}
	public void hide(){
		dialog.hide();
	}
	private void setFocuse(final Button btn){
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
		    public void execute () {
		    	btn.setFocus(true);
		    }
		   });	

	}
}
