package potes.cucumberjvm.eclipseplugin.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

public class FeatureDocumentProvider extends FileDocumentProvider {

	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner =
				new FastPartitioner(
					new FeaturePartitionScanner(),
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
}