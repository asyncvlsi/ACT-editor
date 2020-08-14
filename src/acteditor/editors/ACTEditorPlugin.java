package acteditor.editors;

import org.eclipse.ui.plugin.*;
// import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;
import java.util.*;

public class ACTEditorPlugin extends AbstractUIPlugin {

	public static final String BUNDLE_ID = "acteditor.editors";

	private static ACTEditorPlugin plugin;

	private static ResourceBundle resourceBundle;

	public ACTEditorPlugin() {
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("acteditor.editors.ACTEditorResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	public static ACTEditorPlugin getDefault() {
		return plugin;
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public static String getResourceString(String key) {
		resourceBundle = ACTEditorPlugin.getDefault().getResourceBundle();
		try {
			return resourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public void dispose() {
	}
}
