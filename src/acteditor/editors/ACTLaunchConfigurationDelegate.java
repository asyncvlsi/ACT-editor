package acteditor.editors;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

public class ACTLaunchConfigurationDelegate implements ILaunchConfigurationDelegate {
	
	@Override
	public void launch(ILaunchConfiguration config,
						String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		
		// Make a new launch configuration 
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType("acteditor.editors.launchConfig");
		ILaunchConfigurationWorkingCopy configuration = type.newInstance(null, "ACT Launch");
		
		// Get the path to aflat 
		String aflat_path = System.getenv("ACT_HOME") + "/bin/aflat";
		
		try {
			Runtime.getRuntime().exec("sh -c" + aflat_path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//configuration.getAttribute(aflat_path, false);
		
		configuration.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor());
		
	}
	

}
