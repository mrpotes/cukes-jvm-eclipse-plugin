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


import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	
	private Map<String, IMethod> givenSteps = new HashMap<String, IMethod>();
	private Map<String, IMethod> thenSteps = new HashMap<String, IMethod>();
	private Map<String, IMethod> whenSteps = new HashMap<String, IMethod>();
	private Map<String, IMethod> andSteps = new HashMap<String, IMethod>();
	private Map<String, IMethod> butSteps = new HashMap<String, IMethod>();
	
	// The plug-in ID
	public static final String PLUGIN_ID = "cukes-jvm-plugin"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		IJavaProject[] projects = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects();
		
		SearchRequestor requestor = new SearchRequestor() {

			@Override
			public void acceptSearchMatch(SearchMatch match)
					throws CoreException {
				if (!(match.getElement() instanceof IMethod)) {
					getLog().log(new Status(Status.WARNING, PLUGIN_ID, "Use of cucumber annotation found is not a method: "+match.getElement()));
				}
				IMethod method = (IMethod) match.getElement();
				for (IAnnotation annotation : method.getAnnotations()) {
					String annotationName = annotation.getElementName();
					if ("Given".equals(annotationName)) {
						givenSteps.put(annotation.getMemberValuePairs()[0].toString(), method);
					} else if ("Then".equals(annotationName)) {
						thenSteps.put(annotation.getMemberValuePairs()[0].toString(), method);
					} else if ("When".equals(annotationName)) {
						whenSteps.put(annotation.getMemberValuePairs()[0].toString(), method);
					} else if ("And".equals(annotationName)) {
						andSteps.put(annotation.getMemberValuePairs()[0].toString(), method);
					} else if ("But".equals(annotationName)) {
						butSteps.put(annotation.getMemberValuePairs()[0].toString(), method);
					}
				}
			}
			
		};
		
		SearchEngine searchEngine = new SearchEngine();
		for (IJavaProject project : projects) {
			IType givenType = project.findType("cucumber.annotation.en.Given");
			if (givenType != null) {
				IType thenType = project.findType("cucumber.annotation.en.Then");
				IType whenType = project.findType("cucumber.annotation.en.When");
				IType andType = project.findType("cucumber.annotation.en.And");
				IType butType = project.findType("cucumber.annotation.en.But");
				SearchPattern pattern = SearchPattern.createPattern(givenType, IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE);
				pattern = SearchPattern.createOrPattern(pattern, SearchPattern.createPattern(thenType, IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE));
				pattern = SearchPattern.createOrPattern(pattern, SearchPattern.createPattern(whenType, IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE));
				pattern = SearchPattern.createOrPattern(pattern, SearchPattern.createPattern(andType, IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE));
				pattern = SearchPattern.createOrPattern(pattern, SearchPattern.createPattern(butType, IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE));
				searchEngine.search(pattern, new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()}, 
						SearchEngine.createJavaSearchScope(project.getChildren(), true), requestor, null);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
}
