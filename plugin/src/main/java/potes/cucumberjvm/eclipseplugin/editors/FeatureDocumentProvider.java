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

package potes.cucumberjvm.eclipseplugin.editors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;

import potes.cucumberjvm.eclipseplugin.Activator;

public class FeatureDocumentProvider extends FileDocumentProvider {
	
	@Override
	protected IDocument createEmptyDocument() {
		return new FeatureDocument();
	}

	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null && document instanceof FeatureDocument) {
			IDocumentPartitioner partitioner =
				new FastPartitioner(
					new FeaturePartitionScanner((FeatureDocument) document),
					new String[] {
						IDocument.DEFAULT_CONTENT_TYPE,
						FeaturePartitionScanner.GHERKIN_FEATURE,
						FeaturePartitionScanner.GHERKIN_AND,
						FeaturePartitionScanner.GHERKIN_BUT,
						FeaturePartitionScanner.GHERKIN_COMMENT,
						FeaturePartitionScanner.GHERKIN_GIVEN,
						FeaturePartitionScanner.GHERKIN_THEN,
						FeaturePartitionScanner.GHERKIN_WHEN,
						FeaturePartitionScanner.GHERKIN_SCENARIO });
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
	
	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element,
			IDocument document, boolean overwrite) throws CoreException {
		super.doSaveDocument(monitor, element, document, overwrite);
		if (element instanceof IFileEditorInput && document instanceof FeatureDocument) {
			IFileEditorInput editor = (IFileEditorInput)element;
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(editor.getFile().getContents(), getEncoding(element)), 50);
				StringBuilder buffer= new StringBuilder(50);
				char[] readBuffer= new char[50];
				int n= in.read(readBuffer);
				while (n > 0 && buffer.length() < 50) {
					buffer.append(readBuffer, 0, Math.min(n, buffer.capacity() - buffer.length()));
					n= in.read(readBuffer);
				}
	
				((FeatureDocument)document).setLanguageFromContent(buffer.toString());
			} catch (Exception e) {
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			} finally {
				try {
					if (in != null) in.close();
				} catch (IOException e) {
					Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
				}
			}
		}
	}
}