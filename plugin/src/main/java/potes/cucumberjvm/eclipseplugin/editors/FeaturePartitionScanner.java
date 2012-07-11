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

import org.eclipse.jface.text.rules.*;

public class FeaturePartitionScanner extends RuleBasedPartitionScanner {
	public final static String GHERKIN_COMMENT = "__gherkin_comment";
	public final static String GHERKIN_FEATURE = "__gherkin_feature";
	public final static String GHERKIN_SCENARIO = "__gherkin_scenario";
	public final static String GHERKIN_GIVEN = "__gherkin_given";
	public final static String GHERKIN_WHEN = "__gherkin_when";
	public final static String GHERKIN_THEN = "__gherkin_then";
	public final static String GHERKIN_AND = "__gherkin_and";
	public final static String GHERKIN_BUT = "__gherkin_and";

	public FeaturePartitionScanner() {

		IToken comment = new Token(GHERKIN_COMMENT);
		IToken feature = new Token(GHERKIN_FEATURE);
		IToken scenario = new Token(GHERKIN_SCENARIO);
		IToken given = new Token(GHERKIN_GIVEN);
		IToken when = new Token(GHERKIN_WHEN);
		IToken then = new Token(GHERKIN_THEN);
		IToken and = new Token(GHERKIN_AND);
		IToken but = new Token(GHERKIN_BUT);

		IPredicateRule[] rules = new IPredicateRule[8];

		rules[0] = new EndOfLineRule("#", comment);
		rules[1] = new EndOfLineRule("Feature:", feature);
		rules[2] = new EndOfLineRule("Scenario", scenario);
		rules[3] = new EndOfLineRule("Given", given);
		rules[4] = new EndOfLineRule("When", when);
		rules[5] = new EndOfLineRule("Then", then);
		rules[6] = new EndOfLineRule("And", and);
		rules[7] = new EndOfLineRule("But", but);

		setPredicateRules(rules);
	}
}
