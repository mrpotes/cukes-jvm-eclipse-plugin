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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IImportDeclaration;
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
	
	private Map<String, Map<String, IMethod>> steps = new HashMap<String, Map<String, IMethod>>();
	
	// The plug-in ID
	public static final String PLUGIN_ID = "cukes-jvm-plugin"; //$NON-NLS-1$
	public static final String LAUNCH_CONFIG_TYPE = "potes.cucumberjvm.launching.cucumberTest";
	public static final String LAUNCH_FEATURE_PATH = LAUNCH_CONFIG_TYPE+".featurePath";

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
			SearchPattern pattern = SearchPattern.createPattern("cucumber.annotation.*.*", IJavaSearchConstants.TYPE, 
					IJavaSearchConstants.IMPORT_DECLARATION_TYPE_REFERENCE, SearchPattern.R_PATTERN_MATCH);
//			SearchPattern pattern = SearchPattern.createPattern(givenType, IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE);
//			pattern = SearchPattern.createOrPattern(pattern, SearchPattern.createPattern(thenType, IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE));
//			pattern = SearchPattern.createOrPattern(pattern, SearchPattern.createPattern(whenType, IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE));
//			pattern = SearchPattern.createOrPattern(pattern, SearchPattern.createPattern(andType, IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE));
//			pattern = SearchPattern.createOrPattern(pattern, SearchPattern.createPattern(butType, IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE));
			
			IJavaSearchScope searchScope = SearchEngine.createJavaSearchScope(element == null ? project.getChildren() : new IJavaElement[]{element}, element == null);
			SearchParticipant[] participants = new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()};
			AnnotationFinder annotationFinder = new AnnotationFinder(project, searchEngine, searchScope, participants);
			searchEngine.search(pattern, participants, searchScope, annotationFinder, null);
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
	
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
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
				log(new Status(Status.WARNING, PLUGIN_ID, "Use of cucumber annotation found is not a method: "+match.getElement()));
				return;
			}
			IMethod method = (IMethod) match.getElement();
			for (IAnnotation annotation : method.getAnnotations()) {
				String annName = annotation.getElementName();
				String annPackage = method.getDeclaringType().resolveType(annName)[0][0];
				if (annPackage.startsWith("cucumber.annotation.") && annotation.getMemberValuePairs().length > 0) {
					Map<String, IMethod> stepMap = getStepMap(annName);
					String step = annotation.getMemberValuePairs()[0].getValue().toString();
					stepMap.put(step, method);
				}
			}
		}
		
	};

	private final class AnnotationFinder extends SearchRequestor {
		
		private SearchParticipant[] participants;
		private IJavaSearchScope searchScope;
		private SearchEngine searchEngine;
		private IJavaProject project;

		public AnnotationFinder(IJavaProject project, SearchEngine searchEngine, IJavaSearchScope searchScope,
				SearchParticipant[] participants) {
			this.project = project;
			this.searchEngine = searchEngine;
			this.searchScope = searchScope;
			this.participants = participants;
		}

		@Override
		public void acceptSearchMatch(SearchMatch match)
				throws CoreException {
			if (!(match.getElement() instanceof IImportDeclaration)) {
				Status status = new Status(Status.WARNING, PLUGIN_ID, "Cucumber annotation found is not a type: "+match.getElement());
				log(status);
				return;
			}
			IImportDeclaration decl = (IImportDeclaration) match.getElement();
			IType type = project.findType(decl.getElementName());
			SearchPattern pattern = SearchPattern.createPattern(type, IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE);
			searchEngine.search(pattern, participants, searchScope, requestor, null);
		}
		
	};

	public void cleanResource(IResource resource) {
		for (Map<String, IMethod> stepsMap : steps.values()) {
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
	
	public Set<IType> getStepDefinitionTypes() {
		Set<IType> types = new HashSet<IType>();
		for (Map<String, IMethod> stepMap : steps.values()) {
			addTypes(types, stepMap);
		}
		return types;
	}
	
	public Set<String> getStepDefinitionPackages() {
		Set<String> packages = new HashSet<String>();
		for (IType type : getStepDefinitionTypes()) {
			String fqn = type.getFullyQualifiedName();
			if (fqn.contains(".")) {
				packages.add(fqn.substring(0, fqn.lastIndexOf('.')));
			} else {
				packages.add("");
			}
		}
		return packages;
	}
	
	private void addTypes(Set<IType> types, Map<String, IMethod> stepMethods) {
		for (IMethod method : stepMethods.values()) {
			types.add(method.getDeclaringType());
		}
	}

	public List<String> getSteps(String type) {
		Map<String, IMethod> stepEntries = steps.get(type);
		if (stepEntries == null) return null;
		return new ArrayList<String>(stepEntries.keySet());
	}

	private synchronized Map<String, IMethod> getStepMap(String annName) {
		Map<String, IMethod> stepMap = steps.get(annName);
		if (stepMap == null) {
			stepMap = new HashMap<String, IMethod>();
			steps.put(annName, stepMap);
		}
		return stepMap;
	}
}
