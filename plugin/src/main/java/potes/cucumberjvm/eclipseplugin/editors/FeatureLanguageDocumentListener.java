package potes.cucumberjvm.eclipseplugin.editors;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;

import potes.cucumberjvm.eclipseplugin.Activator;

class FeatureLanguageDocumentListener implements IDocumentListener {
	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {}

	@Override
	public void documentChanged(DocumentEvent event) {
		try {
			FeatureDocument document = (FeatureDocument)event.getDocument();
			int endOfFirstLine = document.getLineLength(0);
			if (event.fOffset < endOfFirstLine) {
				document.setLanguageFromContent(event.getDocument().get(0, endOfFirstLine), event);
			}
		} catch (BadLocationException e) {
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
		}
	}
}
