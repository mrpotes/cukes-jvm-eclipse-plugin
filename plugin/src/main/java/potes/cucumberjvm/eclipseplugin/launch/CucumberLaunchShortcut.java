/*
   Copyright 2012 James Phillpotts

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package potes.cucumberjvm.eclipseplugin.launch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import potes.cucumberjvm.eclipseplugin.Activator;

/**
 * @author James
 * 
 */
public class CucumberLaunchShortcut implements ILaunchShortcut2 {

	public void launch(IEditorPart editor, String mode) {
		launch(mode, getLaunchConfigurations(editor), (IFile)getLaunchableResource(editor));
	}

	public ILaunchConfiguration[] getLaunchConfigurations(IEditorPart editor) {
		return getLaunchConfigurations((IFile) getLaunchableResource(editor));
	}

	public IResource getLaunchableResource(IEditorPart editor) {
		IEditorInput editorInput = editor.getEditorInput();
		return (IFile) editorInput.getAdapter(IFile.class);
	}

	public void launch(ISelection selection, String mode) {
		launch(mode, getLaunchConfigurations(selection), (IFile)getLaunchableResource(selection));
	}

	public ILaunchConfiguration[] getLaunchConfigurations(ISelection selection) {
		return getLaunchConfigurations((IFile)getLaunchableResource(selection));
	}

	public IResource getLaunchableResource(ISelection selection) {
		if (selection instanceof IStructuredSelection && ((IStructuredSelection) selection).getFirstElement() instanceof IFile) {
			return (IFile)((IStructuredSelection) selection).getFirstElement();
		}
		return null;
	}

	private void launch(String mode, ILaunchConfiguration[] existingConfigurations, IFile launchableResource) {
		try {
			if (existingConfigurations.length > 1) {
				selectConfig(existingConfigurations, mode).launch(mode, null);
			} else if (existingConfigurations.length == 1){
				existingConfigurations[0].launch(mode, null);
			} else {
				createLaunchConfiguration(new IFile[] {launchableResource}).launch(mode, null);
			}
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(e.getStatus());
		} catch (InterruptedException e) {
			// User cancelled.
		}
	}

	private ILaunchConfiguration selectConfig(ILaunchConfiguration[] existingConfigurations, String mode) throws InterruptedException {
        ElementListSelectionDialog dialog= new ElementListSelectionDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), null);
        dialog.setElements(existingConfigurations);
        dialog.setTitle("Select "+mode+" configuration");
        dialog.setMultipleSelection(false);
        int result= dialog.open();
        if (result == Window.OK) {
            return (ILaunchConfiguration) dialog.getFirstResult();
        }
        throw new InterruptedException(); // cancelled by user
	}

	private ILaunchConfiguration[] getLaunchConfigurations(IFile file) {
		if (file == null) return null;
		try {
			return getLaunchConfigurations(new IFile[] { file });
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(e.getStatus());
			return null;
		}
	}

	private ILaunchConfiguration createLaunchConfiguration(IFile[] files) {
		ILaunchConfiguration config = null;
		try {
			ILaunchConfigurationType configType = getConfigType();
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, DebugPlugin.getDefault().getLaunchManager()
					.generateLaunchConfigurationName(files[0].getName()));
			List<String> features = new ArrayList<String>();
			for (IFile file: files) features.add(file.getProjectRelativePath().toOSString());
			wc.setAttribute(Activator.LAUNCH_FEATURE_PATH, features);
			// CONTEXTLAUNCHING
			wc.setMappedResources(files);
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, files[0].getProject().getName());
			wc.setAttribute("org.eclipse.jdt.junit.TEST_KIND", "org.eclipse.jdt.junit.loader.junit4");
			config = wc.doSave();
		} catch (CoreException ce) {
			Activator.getDefault().getLog().log(ce.getStatus());
		}
		return config;
	}

	private ILaunchConfigurationType getConfigType() {
		return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(Activator.LAUNCH_CONFIG_TYPE);
	}

	private ILaunchConfiguration[] getLaunchConfigurations(IFile[] files) throws CoreException {
		List<String> features = new ArrayList<String>();
		for (IFile file: files) {
			features.add(file.getProjectRelativePath().toOSString());
		}
		
		ILaunchConfiguration[] launchConfigurations = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(getConfigType());
		List<ILaunchConfiguration> matchingConfigurations = new ArrayList<ILaunchConfiguration>();
		for (ILaunchConfiguration launchConfig : launchConfigurations) {
			List<String> configFeatures = launchConfig.getAttribute(Activator.LAUNCH_FEATURE_PATH, Collections.EMPTY_LIST);
			if (configFeatures.containsAll(features) && features.containsAll(configFeatures)) matchingConfigurations.add(launchConfig);
		}
		return matchingConfigurations.toArray(new ILaunchConfiguration[matchingConfigurations.size()]);
	}

}
