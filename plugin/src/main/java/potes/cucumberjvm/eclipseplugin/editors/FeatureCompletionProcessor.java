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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import potes.cucumberjvm.eclipseplugin.Activator;

public class FeatureCompletionProcessor implements IContentAssistProcessor {

	private static final ICompletionProposal[] NO_PROPOSALS = new ICompletionProposal[0];
	private static final char[] NO_AUTO_ACTIVATION = new char[0];
	private static final IContextInformation[] NO_CONTEXTS = new IContextInformation[0];
	private static final Pattern FIRST_WORD_PATTERN = Pattern.compile("^\\s*(\\w+ ?)(.*)$");
	private static final List<String> ALL_KEYWORDS = 
			Arrays.asList("Feature: ", "Background: ", "Scenario: ", "Scenario Outline: ", "Given ", "When ", "Then ", "And ", "But ");
	private static final List<String> STEP_KEYWORDS = Arrays.asList("Given", "When", "Then", "And", "But");
	private static final List<String> JOINING_KEYWORDS = Arrays.asList("And", "But");

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		IDocument document = viewer.getDocument();
		String[] words = lastWord(document, offset);
		String trimmedKeyword = words != null && words[0] != null ? words[0].trim() : null;
		if (words == null) {
			return NO_PROPOSALS;
		} else if (words[0] == null){
			return asProposalList(offset, 0, ALL_KEYWORDS);
		} else if (STEP_KEYWORDS.contains(trimmedKeyword)) {
			Activator.getDefault().getLog().log(new Status(Status.INFO, Activator.PLUGIN_ID, Arrays.toString(words)));
			List<String> stepProposals = Activator.getDefault().getSteps(trimmedKeyword);
			if (JOINING_KEYWORDS.contains(trimmedKeyword)) {
				List<String> moreStepProposals = Activator.getDefault().getSteps(findPreviousKeyword(document, offset));
				if (stepProposals == null) {
					stepProposals = moreStepProposals;
				} else {
					stepProposals.addAll(moreStepProposals);
				}
			}
			if (stepProposals != null) {
				return matchProposalStrings(offset, words[1], !words[0].endsWith(" "), stepProposals);
			}
		} else {
			return matchProposalStrings(offset, words[0], false, ALL_KEYWORDS);
		}
		return NO_PROPOSALS;
	}

	private String findPreviousKeyword(IDocument doc, int offset) {
		try {
			int loopOffset = doc.getLineOffset(doc.getLineOfOffset(offset)) - 1;
			while (loopOffset > 0) {
				String[] word = lastWord(doc, loopOffset);
				if (word != null && word[0] != null) {
					String w = word[0].trim();
					if (STEP_KEYWORDS.contains(w) && !JOINING_KEYWORDS.contains(w)) {
						return w;
					}
					loopOffset = doc.getLineOffset(doc.getLineOfOffset(loopOffset)) - 1;
				}
			}
		} catch (BadLocationException e) {
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
		}
		return null;
	}

	private String[] lastWord(IDocument doc, int offset) {
		try {
			int lineNum = doc.getLineOfOffset(offset);
			String line = doc.get(doc.getLineOffset(lineNum), offset - doc.getLineOffset(lineNum));
			if (line.matches("^\\s*$"))
				return new String[] {null, null};
			Matcher matcher = FIRST_WORD_PATTERN.matcher(line);
			matcher.find();
			return new String[] {matcher.group(1), matcher.group(2)};
		} catch (BadLocationException e) {
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
		}
		return null;
	}

	private ICompletionProposal[] matchProposalStrings(int offset, String matchAgainst, boolean addPrefixSpace, List<String> stepProposals) {
		List<String> matchingProposals = new ArrayList<String>();
		for (String proposal : stepProposals) {
			if (proposal.startsWith(matchAgainst)) {
				matchingProposals.add(addPrefixSpace ? " "+proposal : proposal);
			}
		}
		return asProposalList(offset - matchAgainst.length(), matchAgainst.length(), matchingProposals);
	}

	private ICompletionProposal[] asProposalList(int offset, int length, List<String> proposalStrings) {
		ICompletionProposal[] proposals = new ICompletionProposal[proposalStrings.size()];
		int i = 0;
		for (String proposal : proposalStrings) {
			IContextInformation info= new ContextInformation(proposal, "");
			proposals[i++]= new CompletionProposal(proposal, offset, length, proposal.length(), null, proposal, info, "");
		}
		return proposals;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer arg0, int arg1) {
		return NO_CONTEXTS;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return NO_AUTO_ACTIVATION;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return NO_AUTO_ACTIVATION;
	}

} 
