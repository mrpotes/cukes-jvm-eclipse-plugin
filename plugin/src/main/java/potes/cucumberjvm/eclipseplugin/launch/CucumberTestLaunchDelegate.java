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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.junit.launcher.JUnitLaunchConfigurationDelegate;

import potes.cucumberjvm.eclipseplugin.Activator;

public class CucumberTestLaunchDelegate extends JUnitLaunchConfigurationDelegate {
	
	private static final FullyQualifiedNameOnlyType RUN_CUCUMBER_TEST = new FullyQualifiedNameOnlyType("potes.cucumberjvm.test.RunCucumberTest");

	@Override
	public String[] getClasspath(ILaunchConfiguration configuration) throws CoreException {
		String[] projectClasspath = super.getClasspath(configuration);
		projectClasspath = Arrays.copyOf(projectClasspath, projectClasspath.length + 1);
		File temp = new File(System.getProperty("java.io.tmpdir"));
		File jar = new File(temp, "cukes-run-test.jar");
		FileOutputStream os;
		try {
			os = new FileOutputStream(jar);
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("cukes-run-test.jar");
		int data = is.read();
		while(data != -1) {
			os.write(data);
			data = is.read();
		}
		} catch (FileNotFoundException e) {
			throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "Coudln't find file", e));
		} catch (IOException e) {
			throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "Couldn't copy jar", e));
		}
		Activator.getDefault().getLog().log(new Status(Status.INFO, Activator.PLUGIN_ID, "Classpath: "+Arrays.toString(projectClasspath)));
		projectClasspath[projectClasspath.length - 1] = jar.getAbsolutePath();
		return projectClasspath;
	}
	
	@Override
	protected IMember[] evaluateTests(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {
		return new IMember[] {RUN_CUCUMBER_TEST};
	}
	
	@Override
	public String getVMArguments(ILaunchConfiguration configuration) throws CoreException {
		boolean first = true;
		StringBuilder builder = new StringBuilder("-ea -Dcucumber.options=\"");
		for (String pkg : Activator.getDefault().getStepDefinitionPackages()) {
			builder.append(first ? "--glue " : " --glue ").append(pkg.replace('.', '/'));
			first = false;
		}
		
		List<String> paths = configuration.getAttribute(Activator.LAUNCH_FEATURE_PATH, Collections.EMPTY_LIST);
		for (String path : paths) {
			builder.append(" ").append(path);
		}
		builder.append("\"");
		return builder.toString();
	}
}
