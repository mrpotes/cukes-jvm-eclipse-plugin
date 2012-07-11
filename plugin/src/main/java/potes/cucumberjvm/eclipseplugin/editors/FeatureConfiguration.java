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

import org.eclipse.jface.text.DefaultTextDoubleClickStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class FeatureConfiguration extends SourceViewerConfiguration {
	private ITextDoubleClickStrategy doubleClickStrategy;
	private ColorManager colorManager;

	public FeatureConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			FeaturePartitionScanner.GHERKIN_FEATURE,
			FeaturePartitionScanner.GHERKIN_AND,
			FeaturePartitionScanner.GHERKIN_BUT,
			FeaturePartitionScanner.GHERKIN_COMMENT,
			FeaturePartitionScanner.GHERKIN_GIVEN,
			FeaturePartitionScanner.GHERKIN_THEN,
			FeaturePartitionScanner.GHERKIN_WHEN,
			FeaturePartitionScanner.GHERKIN_SCENARIO };
	}
	public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new DefaultTextDoubleClickStrategy();
		return doubleClickStrategy;
	}
	
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {

		PresentationReconciler reconciler= new PresentationReconciler();

		DefaultDamagerRepairer dr= new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(colorManager.getColor(IFeatureColorConstants.DEFAULT))));
		setDamagerRepairer(reconciler, dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr= new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(colorManager.getColor(IFeatureColorConstants.COMMENT))));
		setDamagerRepairer(reconciler, dr, FeaturePartitionScanner.GHERKIN_COMMENT);

		dr= new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(colorManager.getColor(IFeatureColorConstants.DIRECTIVE))));
		setDamagerRepairer(reconciler, dr, FeaturePartitionScanner.GHERKIN_FEATURE, FeaturePartitionScanner.GHERKIN_SCENARIO);

		dr= new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(colorManager.getColor(IFeatureColorConstants.STEP))));
		setDamagerRepairer(reconciler, dr, FeaturePartitionScanner.GHERKIN_AND, FeaturePartitionScanner.GHERKIN_BUT, FeaturePartitionScanner.GHERKIN_GIVEN,
				FeaturePartitionScanner.GHERKIN_THEN, FeaturePartitionScanner.GHERKIN_WHEN);

		return reconciler;
	}
	
	private void setDamagerRepairer(PresentationReconciler pr, DefaultDamagerRepairer dr, String... contentTypes) {
		for (String contentType : contentTypes) {
			pr.setDamager(dr, contentType);
			pr.setRepairer(dr, contentType);
		}
	}
	
	static class SingleTokenScanner extends BufferedRuleBasedScanner {

		/**
		 * 
		 * 
		 * @param attribute
		 */
		public SingleTokenScanner(TextAttribute attribute) {
			setDefaultReturnToken(new Token(attribute));
		}
	};


}