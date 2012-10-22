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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

public class CucumberLanguage {
	private Map<String, Map<String, IMethod>> steps;
	private Set<String> extraPackages;

	public CucumberLanguage() {
		this.steps = new HashMap<String, Map<String, IMethod>>();
		this.extraPackages = Collections.synchronizedSet(new HashSet<String>());
	}

	public void cleanResource(IResource resource) {
		for (Map<String, IMethod> stepsMap : steps.values()) {
			Iterator<Entry<String, IMethod>> stepIterator = stepsMap.entrySet().iterator();
			while (stepIterator.hasNext()) {
				Entry<String, IMethod> entry = stepIterator.next();
				if (resource.equals(entry.getValue().getCompilationUnit().getResource())) {
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
	
	public Set<String> getCucumberDefinitionPackages(IJavaProject javaProject) throws JavaModelException {
		Set<String> packages = new HashSet<String>();
		for (IType type : getStepDefinitionTypes()) {
			IResource underlyingResource = type.getUnderlyingResource();
			if ((underlyingResource != null && javaProject.isOnClasspath(underlyingResource)) || javaProject.isOnClasspath(type)) {
				String fqn = type.getFullyQualifiedName();
				if (fqn.contains(".")) {
					packages.add(fqn.substring(0, fqn.lastIndexOf('.')));
				} else {
					packages.add("");
				}
			}
		}
		packages.addAll(extraPackages);
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

	void checkTypeInProject(IJavaProject project, IJavaElement element) throws JavaModelException, CoreException {
		checkProject(new SearchEngine(), project, element);
	}

	void checkProject(SearchEngine searchEngine, IJavaProject project, IJavaElement element)
			throws JavaModelException, CoreException {
		IType givenType = project.findType("cucumber.annotation.en.Given");
		if (givenType != null) {
			SearchPattern pattern = SearchPattern.createPattern("cucumber.annotation.*.*", IJavaSearchConstants.TYPE, 
					IJavaSearchConstants.IMPORT_DECLARATION_TYPE_REFERENCE, SearchPattern.R_PATTERN_MATCH);
			
			IJavaSearchScope searchScope = SearchEngine.createJavaSearchScope(element == null ? project.getChildren() : new IJavaElement[]{element}, element == null);
			SearchParticipant[] participants = new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()};
			AnnotationFinder annotationFinder = new AnnotationFinder(project, searchEngine, searchScope, participants);
			searchEngine.search(pattern, participants, searchScope, annotationFinder, null);
			
			searchNonLanguageAnnotation(searchEngine, project, searchScope, participants, "Before");
			searchNonLanguageAnnotation(searchEngine, project, searchScope, participants, "After");
			searchNonLanguageAnnotation(searchEngine, project, searchScope, participants, "Order");
		}
	}

	private void searchNonLanguageAnnotation(SearchEngine searchEngine, IJavaProject project, IJavaSearchScope searchScope, SearchParticipant[] participants,
			String name)
			throws JavaModelException, CoreException {
		SearchPattern pattern;
		IType type = project.findType("cucumber.annotation."+name);
		pattern = SearchPattern.createPattern(type, IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE);
		searchEngine.search(pattern, participants, searchScope, requestor, null);
	}

	public SearchRequestor requestor = new SearchRequestor() {
	
		@Override
		public void acceptSearchMatch(SearchMatch match)
				throws CoreException {
			if (!(match.getElement() instanceof IMethod)) {
				Activator.log(new Status(Status.WARNING, Activator.PLUGIN_ID, "Use of cucumber annotation found is not a method: "+match.getElement()));
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
				} else if (annPackage.equals("cucumber.annotation")) {
					extraPackages.add(method.getDeclaringType().getPackageFragment().getElementName());
				}
			}
		}
		
	};

	private final class AnnotationFinder extends SearchRequestor {
		
		private SearchParticipant[] participants;
		private IJavaSearchScope searchScope;
		private SearchEngine searchEngine;
		private IJavaProject project;
		private Set<String> types = new HashSet<String>();

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
				Status status = new Status(Status.WARNING, Activator.PLUGIN_ID, "Cucumber annotation found is not a type: "+match.getElement());
				Activator.log(status);
				return;
			}
			IImportDeclaration decl = (IImportDeclaration) match.getElement();
			String typeName = decl.getElementName();
			if (!alreadyProcessed(typeName)) {
				IType type = project.findType(typeName);
				SearchPattern pattern = SearchPattern.createPattern(type, IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE);
				searchEngine.search(pattern, participants, searchScope, requestor, null);
			}
		}

		private synchronized boolean alreadyProcessed(String typeName) {
			if (types.contains(typeName)) {
				return true;
			} else {
				types.add(typeName);
				return false;
			}
		}
		
	}

}