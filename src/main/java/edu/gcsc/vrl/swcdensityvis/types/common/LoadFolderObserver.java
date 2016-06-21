/// package's name
package edu.gcsc.vrl.swcdensityvis.types.common;

import edu.gcsc.vrl.swcdensityvis.data.FolderInfo;

/**
 * @brief observer design pattern
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public interface LoadFolderObserver {
	public void update(FolderInfo data);
}
