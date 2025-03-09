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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class InputTextDialog extends Composite {
	private String defaultText = "";
	private DialogBox dialog = new DialogBox();
	private static PromptUiBinder uiBinder = GWT.create(PromptUiBinder.class);

	interface PromptUiBinder extends UiBinder<Widget, InputTextDialog> {}

	@UiField Label promptMessage;
	@UiField TextBox inputText;
	@UiField Button okButton;
	@UiField Button cancelButton;

	public InputTextDialog() {
		creatUI("Input Text", "Input Massage", "");
	}

	public InputTextDialog(String title, String message){
		creatUI(title, message, "");
		
	}
	public InputTextDialog(String title, String message, String defaultText){
		creatUI(title, message, defaultText);
		
	}

	private void creatUI(String title, String message, String defaultText){
		initWidget(uiBinder.createAndBindUi(this));
		this.defaultText = defaultText;
		setMessage(message);
		setTitle(title);
		setFocuse(inputText);
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
		inputText.setSize(length, "");
	}
	@UiHandler("inputText")
	void onTextReturn(KeyPressEvent e) {
		if(e.getUnicodeCharCode() == 13){
			hide();
			onOk(inputText.getText());
		}
	}
	@UiHandler("okButton")
	void onOkClick(ClickEvent e) {
		hide();
		onOk(inputText.getText());
	}
	@UiHandler("cancelButton")
	void onCancelClick(ClickEvent e) {
		hide();
		onOk(inputText.getText());
	}
	public void onOk(String text){}
	public void onCancel(){}

	public void show(){
		dialog.center();
		inputText.setText(this.defaultText);
		inputText.selectAll();
	}
	public void show(String message){
		setMessage(message);
		dialog.center();
		inputText.setText(this.defaultText);
		inputText.selectAll();
	}
	public void show(String message, String defaultText){
		setMessage(message);
		dialog.center();
		inputText.setText(defaultText);
		inputText.selectAll();
	}
	
	public void hide(){
		dialog.hide();
	}
	public void setDefaultText(String defaultText){
		this.defaultText = defaultText;
		inputText.setText(defaultText);

	}
	private void setFocuse(final TextBox txt){
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
		    public void execute () {
		    	txt.setFocus(true);
		    }
		   });	

	}

}
