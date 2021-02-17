package acteditor.editors;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.plugin.*;
// import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;
import java.util.*;

//import javax.swing.text.View;

public class ACTEditorPlugin extends AbstractUIPlugin {

	public static final String BUNDLE_ID = "acteditor.editors";

	public static final String TEST_PERSPECTIVE_ID = BUNDLE_ID + "ACTOutlineAdapterFactory";

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

	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];

		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	public void run() {
		MessageConsole myConsole = findConsole("act_console");
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		String id = IConsoleConstants.ID_CONSOLE_VIEW;
		
		// Display the console we are writing to 
		try {
			IConsoleView view = (IConsoleView) page.showView(id);
			view.display(myConsole);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		MessageConsoleStream out = myConsole.newMessageStream();
		out.println("Successfully written to the console!");
	}
}
