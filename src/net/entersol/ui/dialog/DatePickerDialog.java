package net.entersol.ui.dialog;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class DatePickerDialog extends Composite {
	private DialogBox dialog = new DialogBox();
	private static PromptUiBinder uiBinder = GWT.create(PromptUiBinder.class);

	interface PromptUiBinder extends UiBinder<Widget, DatePickerDialog> {}

	@UiField Label promptMessage;
	@UiField DateBox inputDate;
	@UiField Button okButton;
	@UiField Button cancelButton;

	public DatePickerDialog() {
		creatUI("Date Picker", "Pick a Date, Please");
	}
	public DatePickerDialog(String title, String message){
		creatUI(title, message);
	}
	private void creatUI(String title, String message){
		initWidget(uiBinder.createAndBindUi(this));
		setMessage(message);
		setTitle(title);
		dialog.add(this);
	}
	public void setDateBoxFormat(String format){
		inputDate.setFormat(new DateBox.DefaultFormat
				(DateTimeFormat.getFormat(format)));

	}
	public void setGlassEnabled(boolean enabled){
		dialog.setGlassEnabled(enabled);
	}
	public void setMessage(String text){
		promptMessage.setText(text);
	}
	
	public void setTitle(String title){
		dialog.setText(title);
	}
	public void setButtonsLable(String okBtn, String cancelBtn){
		okButton.setText(okBtn);
		cancelButton.setText(cancelBtn);
	}

	@UiHandler("okButton")
	void onOkClick(ClickEvent e) {
		hide();
		onOk(inputDate.getValue());
	}
	@UiHandler("cancelButton")
	void onCancelClick(ClickEvent e) {
		hide();
	}
	


	public void onOk(Date date){}
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

}
