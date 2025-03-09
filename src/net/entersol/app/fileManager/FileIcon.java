package net.entersol.app.fileManager;

import org.itemscript.core.values.JsonObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileIcon extends Composite implements ContextMenuHandler{
	private static PromptUiBinder uiBinder = GWT.create(PromptUiBinder.class);

	interface PromptUiBinder extends UiBinder<Widget, FileIcon> {}

	@UiField Label fileName;
	@UiField Image fileIcon;
	@UiField Image star;
	//@UiField CheckBox checkBox;
	@UiField HTMLPanel icon;
	private JsonObject file;
	private Icons iconsCollection = new Icons();
	private PopupPanel contextMenu;
	private boolean selected;

	public FileIcon(JsonObject fileVal){
		file = fileVal;
		initWidget(uiBinder.createAndBindUi(this));
	    
		this.contextMenu = new PopupPanel(true);
	    this.contextMenu.add(new HTML("My Context menu!"));
	    this.contextMenu.hide();
	    addDomHandler(this, ContextMenuEvent.getType());
	    //TODO fileIcon.addDoubleClickHandler(handler);
	    
	    fileIcon.setUrl(iconsCollection.getIcon(file.getString("type")));
		setName(file.getString("name"));
		setStar(isStarred());
		fileIcon.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				selected = !selected;
				setBackgroundColor(selected);
			}});

		star.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean st = !isStarred();
				setStar(st);
				changeStach(st);
				onStar(file);
				
			}
		});
	}
	private void setBackgroundColor(boolean value){
		if(value){
			icon.getElement().getStyle().setProperty("backgroundColor", "#faf2cd");
			icon.getElement().getStyle().setProperty("borderColor", "blue");
		}
		else{
			icon.getElement().getStyle().setProperty("backgroundColor", "white");
			icon.getElement().getStyle().setProperty("borderColor", "white");
		}
		onSelect(value, file);

	}
	private void changeStach(boolean starred){
		if(starred){
			star.setUrl("Theme/star.png");
		}
		else{
			star.setUrl("Theme/unstar.png");
		}
	}
	
	private void setName(String name){
		if(name.length() > 21){
			fileName.setText(name.substring(0, 17) + "...");
			icon.setTitle(name + "\n polla");
		}
		else
			fileName.setText(name);
	}
	public boolean isSelected(){
		return selected;
	}
	public void select(boolean sel){
		selected = sel;
		setBackgroundColor(sel);
	}
	public void setStar(boolean st){
		file.p("starred", (st? "1":"0"));
		changeStach(st);
	}
	public boolean isStarred(){
		return file.getString("starred").equals("1");
	}
	
	public void onSelect(boolean select, JsonObject file){}
	public void onStar(JsonObject file){}
	@Override
	public void onContextMenu(ContextMenuEvent event) {
		// TODO Auto-generated method stub
	    // stop the browser from opening the context menu
		select(true);
	    event.preventDefault();
	    event.stopPropagation();
	    
	 
	    this.contextMenu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
	    this.contextMenu.show();
	}
}
