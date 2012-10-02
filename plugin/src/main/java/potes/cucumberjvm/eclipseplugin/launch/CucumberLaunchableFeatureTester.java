package potes.cucumberjvm.eclipseplugin.launch;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;

import potes.cucumberjvm.eclipseplugin.Activator;

public class CucumberLaunchableFeatureTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if ("isCucumberProject".equals(property)) {
			IResource resource = (IResource)receiver;
			try {
				IProject project = resource.getProject();
				if (!project.hasNature(JavaCore.NATURE_ID)) return false;
				if (JavaCore.create(project).findType("junit.framework.Test") == null) return false;
				if (JavaCore.create(project).findType("cucumber.junit.Cucumber") == null) return false;
				return true;
			} catch (CoreException e) {
				Activator.getDefault().getLog().log(e.getStatus());
			}
		}
		return false;
	}

}
