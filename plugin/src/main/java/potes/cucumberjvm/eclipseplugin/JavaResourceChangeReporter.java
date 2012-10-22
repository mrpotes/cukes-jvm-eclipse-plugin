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


package potes.cucumberjvm.eclipseplugin;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author James
 *
 */
public class JavaResourceChangeReporter implements IResourceChangeListener {

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		try {
			event.getDelta().accept(new Visitor());
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(new Status(Status.ERROR, e.getStatus().getPlugin(), e.getStatus().getMessage(), e));
		}
	}
	
	private static final class Visitor implements IResourceDeltaVisitor {

		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			if ((delta.getKind() == IResourceDelta.REMOVED || delta.getKind() == IResourceDelta.CHANGED) && delta.getResource().getType() == IResource.FILE) {
				Activator.getLanguage().cleanResource(delta.getResource());
			}
			IJavaElement javaResource = null;
			try {
				javaResource = JavaCore.create(delta.getResource());
			} catch (Exception e) {}
			if (javaResource != null) {
//				Activator.getDefault().getLog().log(new Status(Status.INFO, Activator.PLUGIN_ID, javaResource.getClass().getName()+": "+javaResource.toString()));
				if (javaResource instanceof IType || javaResource instanceof ICompilationUnit) {
					if (delta.getKind() == IResourceDelta.ADDED || delta.getKind() == IResourceDelta.CHANGED) {
						Activator.getLanguage().checkTypeInProject(getJavaProject(javaResource), javaResource);
					}
				}
			}
			return true;
		}

		private IJavaProject getJavaProject(IJavaElement javaResource) throws CoreException {
			if (javaResource.getJavaProject().getProject().hasNature(JavaCore.NATURE_ID)) {
				return javaResource.getJavaProject();
			}
			for (IJavaProject javaProject : JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects()) {
				for (IContainer container : ResourcesPlugin.getWorkspace().getRoot().findContainersForLocationURI(javaResource.getResource().getLocationURI())) {
					IProject project = container.getProject();
					if (javaProject.getProject().equals(project)) return javaProject;
				}
			}
			throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "Cannot find java project containing resource: "+javaResource.toString()));
		}
		
	}

}
