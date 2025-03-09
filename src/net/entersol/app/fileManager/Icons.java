package net.entersol.app.fileManager;

import java.util.TreeMap;

public class Icons {
	private static final TreeMap<String, String> icons = new TreeMap<String, String>();
	
	public Icons(){
		icons.put("jpg", "Theme/jpg.png");
		icons.put("gif", "Theme/gif.png");
		icons.put("png", "Theme/png.png");
		icons.put("bmp", "Theme/bmp.png");

		icons.put("doc", "Theme/doc.png");
		icons.put("docx", "Theme/doc.png");
		icons.put("ppt", "Theme/ppt.png");
		icons.put("pptx", "Theme/ppt.png");
		icons.put("pptx", "Theme/ppt.png");
		icons.put("xls", "Theme/xls.png");
		icons.put("xlsx", "Theme/xls.png");

		icons.put("pdf", "Theme/pdf.png");
		icons.put("txt", "Theme/txt.png");
		
		icons.put("zip", "Theme/zip.png");
		icons.put("rar", "Theme/rar.png");
		
		icons.put("avi", "Theme/avi.png");
		icons.put("jpeg", "Theme/jpeg.png");
		icons.put("mp4", "Theme/mp4.png");
		icons.put("mp3", "Theme/mp.png");
		icons.put("wav", "Theme/wav.png");
		icons.put("mov", "Theme/mov.png");
		icons.put("vid", "Theme/vid.png");

		icons.put("dir", "Theme/folder.png");
	}
	public String getIcon(String type){
		if(icons.containsKey(type))
			return icons.get(type);
		else
			return "Theme/file.png";
	}
}
