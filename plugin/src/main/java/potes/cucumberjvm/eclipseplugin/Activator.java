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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
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
	
	private Map<String, Map<String, IMethod>> steps = new HashMap<String, Map<String, IMethod>>();
	
	{
		steps.put("Given", givenSteps);
		steps.put("Then", thenSteps);
		steps.put("When", whenSteps);
		steps.put("And", andSteps);
		steps.put("But", butSteps);
	}
	
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
		
		
		
		SearchEngine searchEngine = new SearchEngine();
		for (IJavaProject project : projects) {
			checkProject(searchEngine, project, null);
		}
		
		IResourceChangeListener listener = new JavaResourceChangeReporter();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
	}
	
	public void checkTypeInProject(IJavaProject project, IJavaElement element) throws JavaModelException, CoreException {
		checkProject(new SearchEngine(), project, element);
	}

	/**
	 * @param requestor
	 * @param searchEngine
	 * @param project
	 * @param element 
	 * @throws JavaModelException
	 * @throws CoreException
	 */
	private void checkProject(SearchEngine searchEngine, IJavaProject project, IJavaElement element)
			throws JavaModelException, CoreException {
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
			
			IJavaSearchScope searchScope = SearchEngine.createJavaSearchScope(element == null ? project.getChildren() : new IJavaElement[]{element}, element == null);
			searchEngine.search(pattern, new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()}, 
					searchScope, requestor, null);
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
	
	private SearchRequestor requestor = new SearchRequestor() {

		@Override
		public void acceptSearchMatch(SearchMatch match)
				throws CoreException {
			if (!(match.getElement() instanceof IMethod)) {
				getLog().log(new Status(Status.WARNING, PLUGIN_ID, "Use of cucumber annotation found is not a method: "+match.getElement()));
			}
			IMethod method = (IMethod) match.getElement();
			for (IAnnotation annotation : method.getAnnotations()) {
				String annotationName = annotation.getElementName();
				String step = annotation.getMemberValuePairs()[0].getValue().toString();
//				getLog().log(new Status(Status.INFO, PLUGIN_ID, "Adding step definition ["+step+"] for "+method.getCompilationUnit().getResource()));
				if ("Given".equals(annotationName)) {
					givenSteps.put(step, method);
				} else if ("Then".equals(annotationName)) {
					thenSteps.put(step, method);
				} else if ("When".equals(annotationName)) {
					whenSteps.put(step, method);
				} else if ("And".equals(annotationName)) {
					andSteps.put(step, method);
				} else if ("But".equals(annotationName)) {
					butSteps.put(step, method);
				}
			}
		}
		
	};

	@SuppressWarnings("unchecked")
	public void cleanResource(IResource resource) {
		cleanResource(resource, givenSteps, thenSteps, whenSteps, andSteps, butSteps);
	}

	private void cleanResource(IResource resource, Map<String, IMethod>... steps) {
		for (Map<String, IMethod> stepsMap : steps) {
			Iterator<Entry<String, IMethod>> stepIterator = stepsMap.entrySet().iterator();
			while (stepIterator.hasNext()) {
				Entry<String, IMethod> entry = stepIterator.next();
				if (resource.equals(entry.getValue().getCompilationUnit().getResource())) {
//					getLog().log(new Status(Status.INFO, PLUGIN_ID, "Removing step definition ["+entry.getKey()+"] for "+resource.toString()));
					stepIterator.remove();
				}
			}
		}
	}
	
	public List<String> getSteps(String type) {
		Map<String, IMethod> stepEntries = steps.get(type);
		if (stepEntries == null) return null;
		return new ArrayList<String>(stepEntries.keySet());
	}
}
