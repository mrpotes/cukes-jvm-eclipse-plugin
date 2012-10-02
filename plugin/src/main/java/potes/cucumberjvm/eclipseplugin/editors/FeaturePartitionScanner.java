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

import gherkin.I18n;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

public class FeaturePartitionScanner extends RuleBasedPartitionScanner {
	public final static String GHERKIN_COMMENT = "__gherkin_comment";
	public final static String GHERKIN_FEATURE = "__gherkin_feature";
	public final static String GHERKIN_BACKGROUND = "__gherkin_background";
	public final static String GHERKIN_SCENARIO = "__gherkin_scenario";
	public final static String GHERKIN_SCENARIO_OUTLINE = "__gherkin_scenario_outline";
	public final static String GHERKIN_EXAMPLES = "__gherkin_examples";
	public final static String GHERKIN_GIVEN = "__gherkin_given";
	public final static String GHERKIN_WHEN = "__gherkin_when";
	public final static String GHERKIN_THEN = "__gherkin_then";
	public final static String GHERKIN_AND = "__gherkin_and";
	public final static String GHERKIN_BUT = "__gherkin_but";
	public final static String GHERKIN_TABLE = "__gherkin_table";

    public static final String FEATURE_KEY = "feature";
    public static final String BACKGROUND_KEY = "background";
    public static final String SCENARIO_KEY = "scenario";
    public static final String OUTLINE_KEY = "scenario_outline";
    public static final String EXAMPLES_KEY = "examples";
    public static final String GIVEN_KEY = "given";
    public static final String WHEN_KEY = "when";
    public static final String THEN_KEY = "then";
    public static final String AND_KEY = "and";
    public static final String BUT_KEY = "but";

    public FeaturePartitionScanner(FeatureDocument document) {
		I18n language = document.getLanguage();
		setRules(language);
	}

	protected void setRules(I18n language) {
		IToken comment = new Token(GHERKIN_COMMENT);
		IToken feature = new Token(GHERKIN_FEATURE);
		IToken background = new Token(GHERKIN_BACKGROUND);
		IToken scenario = new Token(GHERKIN_SCENARIO);
		IToken scenarioOutline = new Token(GHERKIN_SCENARIO_OUTLINE);
		IToken examples = new Token(GHERKIN_EXAMPLES);
		IToken given = new Token(GHERKIN_GIVEN);
		IToken when = new Token(GHERKIN_WHEN);
		IToken then = new Token(GHERKIN_THEN);
		IToken and = new Token(GHERKIN_AND);
		IToken but = new Token(GHERKIN_BUT);
		IToken table = new Token(GHERKIN_TABLE);

		List<IPredicateRule> rules = new ArrayList<IPredicateRule>();

		rules.add(new EndOfLineRule("#", comment));
		rules.add(new EndOfLineRule("|", table));
		
		addRules(rules, feature, language.keywords(FEATURE_KEY));
		addRules(rules, background, language.keywords(BACKGROUND_KEY));
		addRules(rules, scenario, language.keywords(SCENARIO_KEY));
		addRules(rules, scenarioOutline, language.keywords(OUTLINE_KEY));
		addRules(rules, examples, language.keywords(EXAMPLES_KEY));
		addRules(rules, given, language.keywords(GIVEN_KEY));
		addRules(rules, when, language.keywords(WHEN_KEY));
		addRules(rules, then, language.keywords(THEN_KEY));
		addRules(rules, and, language.keywords(AND_KEY));
		addRules(rules, but, language.keywords(BUT_KEY));

		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}

	private void addRules(List<IPredicateRule> rules, IToken token, List<String> keywords) {
		for (String keyword : keywords) {
			rules.add(new EndOfLineRule(keyword, token));
		}
	}
}
