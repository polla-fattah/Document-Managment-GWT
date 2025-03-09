package net.entersol.ui.dialog;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**

 */
public class ClosableDialog extends DialogBox{
	private static MessageUiBinder uiBinder = GWT.create(MessageUiBinder.class);
	interface MessageUiBinder extends UiBinder<Widget, ClosableDialog> {}

	@UiField HTMLPanel mainTitle;
	@UiField HTML close;
	@UiField HTML title;


	public ClosableDialog(boolean autoHide, boolean modal){
		super(autoHide, modal);
		uiBinder.createAndBindUi(this);
		Element td = getCellElement(0, 1);
		DOM.removeChild(td, (Element) td.getFirstChildElement());
		DOM.appendChild(td, mainTitle.getElement());
	}
	public ClosableDialog(boolean autoHide){
		this(autoHide, true);
	}
	public ClosableDialog(){
		this(false);
	}

	@Override
	public String getHTML(){
		return this.title.getHTML();
	}

	@Override
	public String getText(){
		return this.title.getText();
	}

	@Override
	public void setHTML(String html){
		this.title.setHTML(html);
	}

	@Override
	public void setText(String text){
		this.title.setText(text);
	}

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event){
		NativeEvent nativeEvent = event.getNativeEvent();

		if (!event.isCanceled() && (event.getTypeInt() == Event.ONCLICK)  && isCloseEvent(nativeEvent)){
			this.hide();
		}
		super.onPreviewNativeEvent(event);
	}

	private boolean isCloseEvent(NativeEvent event){
		return event.getEventTarget().equals(close.getElement());//compares equality of the underlying DOM elements
	}
}

