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

import static potes.cucumberjvm.eclipseplugin.editors.FeaturePartitionScanner.*;
import org.eclipse.jface.text.DefaultTextDoubleClickStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
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
			GHERKIN_COMMENT,
			GHERKIN_FEATURE,
			GHERKIN_BACKGROUND,
			GHERKIN_SCENARIO,
			GHERKIN_SCENARIO_OUTLINE,
			GHERKIN_TABLE,
			GHERKIN_GIVEN,
			GHERKIN_WHEN,
			GHERKIN_THEN };
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
		setDamagerRepairer(reconciler, dr, GHERKIN_COMMENT);

		dr= new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(colorManager.getColor(IFeatureColorConstants.DIRECTIVE))));
		setDamagerRepairer(reconciler, dr, GHERKIN_FEATURE, GHERKIN_SCENARIO, GHERKIN_SCENARIO_OUTLINE, GHERKIN_BACKGROUND, GHERKIN_EXAMPLES);

		dr= new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(colorManager.getColor(IFeatureColorConstants.STEP))));
		setDamagerRepairer(reconciler, dr, GHERKIN_AND, GHERKIN_BUT, GHERKIN_GIVEN, GHERKIN_THEN, GHERKIN_WHEN);

		dr= new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(colorManager.getColor(IFeatureColorConstants.STRING))));
		setDamagerRepairer(reconciler, dr, GHERKIN_TABLE);

		return reconciler;
	}
	
	private void setDamagerRepairer(PresentationReconciler pr, DefaultDamagerRepairer dr, String... contentTypes) {
		for (String contentType : contentTypes) {
			pr.setDamager(dr, contentType);
			pr.setRepairer(dr, contentType);
		}
	}
	
	static class SingleTokenScanner extends BufferedRuleBasedScanner {
		public SingleTokenScanner(TextAttribute attribute) {
			setDefaultReturnToken(new Token(attribute));
		}
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant = new ContentAssistant();
		assistant.setContentAssistProcessor(new FeatureCompletionProcessor(), IDocument.DEFAULT_CONTENT_TYPE);
		assistant.setContentAssistProcessor(new FeatureCompletionProcessor(), GHERKIN_GIVEN);
		assistant.setContentAssistProcessor(new FeatureCompletionProcessor(), GHERKIN_WHEN);
		assistant.setContentAssistProcessor(new FeatureCompletionProcessor(), GHERKIN_THEN);
		assistant.setContentAssistProcessor(new FeatureCompletionProcessor(), GHERKIN_AND);
		assistant.setContentAssistProcessor(new FeatureCompletionProcessor(), GHERKIN_BUT);
		return assistant;
	}
	

}