package net.entersol.ui.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class Message extends PopupPanel  {
	public static final String ALERT = "Theme/alert.png";
	public static final String SUCCESS = "Theme/success.png";
	public static final String ERROR = "Theme/error.png";
	public static final String INFORMATION = "Theme/information.png";

	private static MessageUiBinder uiBinder = GWT.create(MessageUiBinder.class);

	interface MessageUiBinder extends UiBinder<Widget, Message> {}
	
	@UiField Image messageImage;
	@UiField Label dialogMessage;

	public Message() {
		creatUI(ALERT, " Message", true);
	}
	public Message(boolean autoHide) {
		creatUI(ALERT, " Message", autoHide);
	}

	public Message(String img, String message) {
		creatUI(img, message, true);
	}
	public Message(String img, String message, boolean autoHide) {
		creatUI(img, message, autoHide);
	}
	private void creatUI(String img, String message, boolean autoHide){
		add(uiBinder.createAndBindUi(this));
		setImage(img);
		setMessage(message);
		setAnimationEnabled(true);
		setAutoHideEnabled(autoHide);
		setStyleName("popupMessage");
	}
	@UiHandler("closeButton")
	void onTextReturn(ClickEvent e) {
			hide();
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
	public void setImage(String image){
		messageImage.setUrl(image);
	}
	

}
