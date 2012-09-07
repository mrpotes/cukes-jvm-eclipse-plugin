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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;

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
			setDocumentContent(document, (IFileEditorInput)element, getEncoding(element));
		}
	}
}