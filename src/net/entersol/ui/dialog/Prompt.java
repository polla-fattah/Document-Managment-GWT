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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Prompt extends Composite {
	public static final String ALERT = "Theme/alert.png";
	public static final String SUCCESS = "Theme/success.png";
	public static final String ERROR = "Theme/error.png";
	public static final String INFORMATION = "Theme/information.png";
	
	private DialogBox dialog = new DialogBox();
	private static PromptUiBinder uiBinder = GWT.create(PromptUiBinder.class);

	interface PromptUiBinder extends UiBinder<Widget, Prompt> {}

	@UiField Image promptImage;
	@UiField Label promptMessage;
	@UiField Button okButton;

	public Prompt() {
		creatUI(ALERT, "Alert", "Prompt Message");
	}


	public Prompt(String img, String title) {

		creatUI(img, title, "Prompt Message");

	}
	public Prompt(String img, String title, String message) {
		creatUI(img, title, message);
	}
	private void creatUI(String img, String title, String message){
		initWidget(uiBinder.createAndBindUi(this));
		setImage(img);
		setMessage(message);
		setTitle(title);
		setFocuse(okButton);
		dialog.add(this);
	}

	public void setMessage(String text){
		promptMessage.setText(text);
	}
	public void setTitle(String title){
		dialog.setText(title);
	}
	public void setButtonsLable(String okBtn){
		okButton.setText(okBtn);
	}
	@UiHandler("okButton")
	void onClick(ClickEvent e) {
		hide();
		onOk();
	}
	@UiHandler("okButton")
	void onTextReturn(KeyPressEvent e) {
		if(e.getUnicodeCharCode() == 13){
			hide();
			onOk();
		}
	}
	public void onOk(){
	}

	public void show(){
		dialog.center();
	}
	public void show(String message){
		setMessage(message);
		dialog.center();
	}
	public void show(String img, String title, String message){
		setImage(img);
		setMessage(message);
		setTitle(title);
		dialog.center();
	}
	public void hide(){
		dialog.hide();
	}

	public void setImage(String url){
		promptImage.setUrl(url);
	}
	private void setFocuse(final Button btn){
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
		    public void execute () {
		    	btn.setFocus(true);
		    }
		   });	

	}
}
