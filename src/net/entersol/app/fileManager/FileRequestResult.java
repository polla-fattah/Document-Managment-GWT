/*
 * Copyright © 2013, Entersol Company. All rights reserved.
 * Copy righted material do not look at(!)
 */

package net.entersol.app.fileManager;

import org.itemscript.core.values.JsonArray;


public interface FileRequestResult {
	void onRequestSuccess(JsonArray result);
	void onRequestFailed(FileException fileException);
}
