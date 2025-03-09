/*
 * Copyright © 2013, Entersol Company. All rights reserved.
 * Copy righted material do not look at(!)
 */

package net.entersol.app.fileManager;

import com.google.gwt.user.client.ui.Image;

/**
 * This interface triggers methods after response arrived if its succeeded then {@link  ImageRequestResult#onRequestSuccess(Image) } will be called otherwise {@link  ImageRequestResult#onRequestFailed onRequestFailed} 
 * it has been used by {@link FileIO}
 * @author Polla Fattah
 *
 */
public interface ImageRequestResult {
	void onRequestSuccess(Image image);
	void onRequestFailed(FileException fileException);
}
