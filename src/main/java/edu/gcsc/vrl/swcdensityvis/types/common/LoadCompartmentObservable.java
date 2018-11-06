/// package's name
package edu.gcsc.vrl.swcdensityvis.types.common;

/// imports
import edu.gcsc.vrl.swcdensityvis.data.CompartmentInfo;
import edu.gcsc.vrl.swcdensityvis.importer.XML.XMLDensityVisualizerDiameterImpl;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @brief the compartment observable
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
public class LoadCompartmentObservable {

	/**
	 * @brief stores the observers and the geometry file information 
	 */
	private class CompartmentFileTag {

		public Collection<LoadCompartmentObserver> observers = new HashSet<LoadCompartmentObserver>();
		public CompartmentInfo data = null;
	}

	/**
	 * @brief stores the hoc global tab
	 */
	private class CompartmentFileGlobalTag {

		public Collection<LoadCompartmentObserver> observers = new HashSet<LoadCompartmentObserver>();
	}

	/**
	 * @brief if multiple file loaders exist they are distinguished by the
	 * file_tag
	 * @param file_tag
	 * @param id
	 * @param object
	 */
	private class Identifier {

		private final String file_tag;
		private final Object object;
		private final int windowID;

		/**
		 * @brief default ctor
		 * @param file_tag
		 * @param object
		 * @param windowID
		 */
		public Identifier(String file_tag, Object object, int windowID) {
			this.file_tag = file_tag;
			this.object = object;
			this.windowID = windowID;
		}

		/**
		 * @brief hashcode
		 */
		@Override
		public int hashCode() {
			int result = 17;
			result = 37 * result + file_tag.hashCode();
			result = 37 * result + object.hashCode();
			result = 37 * result + windowID;
			return result;
		}

		/**
		 * @brief equals
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			if (obj.getClass() != getClass()) {
				return false;
			}

			LoadCompartmentObservable.Identifier rhs = (LoadCompartmentObservable.Identifier) obj;

			return (file_tag.equals(rhs.file_tag)) && (object == rhs.object) && (windowID == rhs.windowID);
		}
	}

	/**
	 * @brief get the Compartment file tag
	 * @param file_tag
	 * @param object
	 * @param windowID
	 * @param create
	 */
	private synchronized CompartmentFileTag getTag(String file_tag, Object object, int windowID, boolean create) {
		LoadCompartmentObservable.Identifier id = new LoadCompartmentObservable.Identifier(file_tag, object, windowID);
		if (file_tags.containsKey(id)) {
			return file_tags.get(id);
		}
		if (create) {
			file_tags.put(id, new LoadCompartmentObservable.CompartmentFileTag());
			return getTag(file_tag, object, windowID, false);
		}

		return null;
	}

	/**
	 * @brief get the global file tag for the Compartment file
	 * @param file_tag
	 * @param create
	 * @return
	 */
	private synchronized LoadCompartmentObservable.CompartmentFileGlobalTag getGlobalTag(String file_tag, boolean create) {
		if (globalTags.containsKey(file_tag)) {
			return globalTags.get(file_tag);
		}

		if (create) {
			globalTags.put(file_tag, new LoadCompartmentObservable.CompartmentFileGlobalTag());
			return getGlobalTag(file_tag, false);
		}

		return null;
	}

	/**
	 * @brief Add an observer to this Observable. The observer listens to a
	 * file_tag. The observer will be updated with the current data
	 * automatically.
	 *
	 * @see this is a window based file_tag, i. e. this is a window-local
	 * tag, for a global file_tag which listens to a single CompartmentFileObservable
	 * from different windows use the below addObserver method for global
	 * tags!
	 *
	 * @param obs the observer to add
	 * @param file_tag the file_tag
	 * @param object
	 * @param windowID
	 */
	public synchronized void addObserver(LoadCompartmentObserver obs, String file_tag, Object object, int windowID) {
		getTag(file_tag, object, windowID, true).observers.add(obs);
		obs.update(getTag(file_tag, object, windowID, false).data);
	}

	/**
	 * @brief Add an observer to this Observable. The observer listens to a
	 * file_tag. The observer will be updated with the current data
	 * automatically.
	 *
	 * @param obs the observer to add
	 * @param file_tag the file_tag
	 */
	public synchronized void addObserver(LoadCompartmentObserver obs, String file_tag) {
		getGlobalTag(file_tag, true).observers.add(obs);

		for (Map.Entry<LoadCompartmentObservable.Identifier, LoadCompartmentObservable.CompartmentFileTag> entry : file_tags.entrySet()) {
			if (entry.getKey().file_tag.equals(file_tag)) {
				obs.update(entry.getValue().data);
			}
		}
	}

	/**
	 * @brief Removes an observer from this Observable
	 *
	 * @param obs the observer to remove
	 * @param file_tag the file_tag
	 * @param object
	 * @param windowID
	 */
	public synchronized void deleteObserver(LoadCompartmentObserver obs, String file_tag, Object object, int windowID) {
		LoadCompartmentObservable.Identifier id = new LoadCompartmentObservable.Identifier(file_tag, object, windowID);
		if (file_tags.containsKey(id)) {
			file_tags.get(id).observers.remove(obs);
		}
		if (globalTags.containsKey(file_tag)) {
			globalTags.get(file_tag).observers.remove(obs);
		}
	}

	/**
	 * @brief Removes an observer from this Observable
	 *
	 * @param obs the observer to remove
	 */
	public synchronized void deleteObserver(LoadCompartmentObserver obs) {

		for (Map.Entry<LoadCompartmentObservable.Identifier, LoadCompartmentObservable.CompartmentFileTag> entry : file_tags.entrySet()) {
			entry.getValue().observers.remove(obs);
		}
		for (Map.Entry<String, LoadCompartmentObservable.CompartmentFileGlobalTag> entry : globalTags.entrySet()) {
			entry.getValue().observers.remove(obs);
		}
	}

	/**
	 * @brief Removes all observer of a file_tag from this Observable
	 *
	 * @param file_tag the file_tag
	 * @param object
	 * @param windowID
	 */
	public synchronized void deleteObservers(String file_tag, Object object, int windowID) {
		LoadCompartmentObservable.Identifier id = new LoadCompartmentObservable.Identifier(file_tag, object, windowID);
		if (file_tags.containsKey(id)) {
			file_tags.get(id).observers.clear();
		}
		for (Map.Entry<String, LoadCompartmentObservable.CompartmentFileGlobalTag> entry : globalTags.entrySet()) {
			entry.getValue().observers.clear();
		}
	}

	/**
	 * @brief Notifies all observers of a file_tag about the currently given
	 * data
	 *
	 * @param file_tag the file_tag
	 * @param object
	 * @param windowID
	 */
	public synchronized void notifyObservers(String file_tag, Object object, int windowID) {
		System.err.println("within notifyObservers");
		// get data for file_tag
		LoadCompartmentObservable.CompartmentFileTag fileTag = getTag(file_tag, object, windowID, false);
		if (fileTag == null) {
			System.err.println("No local observers present!");
		}
		// if no such file_tag present, return (i.e. no observer)
		if (fileTag != null) {

			// notify observers of this file_tag
			for (LoadCompartmentObserver b : fileTag.observers) {
				b.update(fileTag.data);
				System.err.println("Notify a observer of file_tag: " + fileTag);
			}
		}

		// get data for global file_tag
		LoadCompartmentObservable.CompartmentFileGlobalTag hocGlobalTag = getGlobalTag(file_tag, false);

		// if no such file_tag present, return (i.e. no observer)
		if (hocGlobalTag == null) {
			System.err.println("No global observers present!");
		}

		if (hocGlobalTag != null) {

			// notify observers of this file_tag
			for (LoadCompartmentObserver b : hocGlobalTag.observers) {
				if (fileTag != null) {
					b.update(fileTag.data);
					System.err.println("b.update(fileTag.data) called");
				} else {
					System.err.println("b.update(null) called!");
					b.update(null);
				}
			}
		}
	}

	/**
	 * @brief Notifies a specific observer of a file_tag about the currently
	 * given data
	 *
	 * @param obs the observer
	 * @param file_tag the file_tag
	 * @param object the object containing the observer
	 * @param windowID the window containing the object
	 */
	public synchronized void notifyObserver(LoadCompartmentObserver obs, String file_tag, Object object, int windowID) {
		// try getting data for file_tag
		LoadCompartmentObservable.CompartmentFileTag fileTag = getTag(file_tag, object, windowID, false);

		if (fileTag != null) {
			obs.update(fileTag.data);
		}
	}

	/**
	 * @brief sets a filename for a file_tag. The file will be analysed and
	 * the contained data will be broadcasted to all observer of the
	 * file_tag.
	 *
	 * @param file the file
	 * @param file_tag the file_tag
	 * @param object
	 * @param windowID
	 * @return empty string if successful, error-msg if error occured
	 */
	public synchronized String setSelectedFile(final File file, String file_tag, Object object, int windowID) {
		LoadCompartmentObservable.CompartmentFileTag fileTag = getTag(file_tag, object, windowID, true);
		/**
		 * Note: The compartment selection is only supported for Neurolucida.
		 * Depending on the file extension we assume a parser and get
		 * the default implementation to parse the file - for now
		 * only XML files are supported and the compartments from
		 * one file are used as the selectable compartment types.
		 * Compartment types can be selected / de-selected in the GUI.
		 */
		XMLDensityVisualizerDiameterImpl impl = new XMLDensityVisualizerDiameterImpl();
		impl.setFiles(new ArrayList<File>() {
			private static final long serialVersionUID = 1L;
			{ add(file); }
		});
		Set<String> compartments = impl.get_compartments();
		System.err.println("Compartments: " + compartments);
		fileTag.data = new CompartmentInfo();
		fileTag.data.set_names_compartments(compartments);

		// now we notify the obersver of this file_tag
		notifyObservers(file_tag, object, windowID);
		System.err.println("Notify Observers called");
		return "";
	}

	/**
	 * @brief Sets that a file_tag has an invalid file.
	 *
	 * @param file_tag the file_tag
	 * @param object
	 * @param windowID
	 */
	public synchronized void setInvalidFile(String file_tag, Object object, int windowID) {
		LoadCompartmentObservable.CompartmentFileTag fileTag = getTag(file_tag, object, windowID, true);

		// set to new (empty) data
		fileTag.data = null;

		// now we notify the observer of this file_tag
		notifyObservers(file_tag, object, windowID);
	}

	// stores the file infos (i. e. sections etc)
	private final transient Map<Identifier, CompartmentFileTag> file_tags = new HashMap<Identifier, CompartmentFileTag>();
	private final transient Map<String, CompartmentFileGlobalTag> globalTags = new HashMap<String, CompartmentFileGlobalTag>();
	// singleton instance
	private static volatile LoadCompartmentObservable instance = null;
	
	
	/**
	 * @brief private ctor (singleton)
	 */
	private LoadCompartmentObservable() {
	}

	/**
	 * @brief returns the singleton instance
	 * @return instance
	 */
	public static LoadCompartmentObservable getInstance() {
		if (instance == null) {
			synchronized (LoadCompartmentObservable.class) {
				if (instance == null) {
					instance = new LoadCompartmentObservable();
				}
			}
		}
		return instance;
	}
}
