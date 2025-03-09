package net.entersol.ui.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class Waiting extends PopupPanel  {
	private static WaitingUiBinder uiBinder = GWT.create(WaitingUiBinder.class);

	interface WaitingUiBinder extends UiBinder<Widget, Waiting> {}
	
	@UiField Label dialogMessage;

	public Waiting() {
		creatUI(" Message", false);
	}
	public Waiting(boolean autoHide) {
		creatUI(" Message", autoHide);
	}

	public Waiting(String message) {
		creatUI( message, false);
	}
	public Waiting(String message, boolean autoHide) {
		creatUI(message, autoHide);
	}
	private void creatUI(String message, boolean autoHide){
		add(uiBinder.createAndBindUi(this));
		setMessage(message);
		setAnimationEnabled(true);
		setAutoHideEnabled(autoHide);
		setGlassEnabled(true);
		setStyleName("popupMessage");
	}

	public void show(String message){
		setMessage(message);
		super.show();
	}

	public void center(String message){
		setMessage(message);
		super.center();
	}

	public void setMessage(String message){
		dialogMessage.setText(message);
	}
}
