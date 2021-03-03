/*
 * Copyright 2021 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.junit.serverresult.editor;

import static de.jcup.junit.serverresult.editor.JunitServerResultDocumentIdentifiers.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

import de.jcup.junit.serverresult.preferences.JUnitServerResultPreferences;

public class JunitServerResultDocumentPartitionScanner extends RuleBasedPartitionScanner {

	private OnlyLettersKeyWordDetector onlyLettersWordDetector = new OnlyLettersKeyWordDetector();

	public int getOffset(){
		return fOffset;
	}
	
	public JunitServerResultDocumentPartitionScanner() {
	    boolean syntaxHighlightingFullLine = JUnitServerResultPreferences.getInstance().isColorizingFullLogLine();
	    
		IToken logDebug= createToken(LOG_DEBUG);
		IToken logInfo = createToken(LOG_INFO);
		IToken logWarn = createToken(LOG_WARN);
		IToken logError = createToken(LOG_ERROR);

		List<IPredicateRule> rules = new ArrayList<>();
		if (syntaxHighlightingFullLine) {
		    rules.add(new SingleLineRule("DEBUG", "", logDebug));
		    rules.add(new SingleLineRule("INFO", "", logInfo));
		    rules.add(new SingleLineRule("WARNING", "", logWarn));
		    rules.add(new SingleLineRule("WARN", "", logWarn));
		    rules.add(new SingleLineRule("ERROR", "", logError));
		    
		}else {
		    rules.add(new ExactWordPatternRule(onlyLettersWordDetector,"DEBUG",logDebug));
		    rules.add(new ExactWordPatternRule(onlyLettersWordDetector,"INFO",logInfo));
		    rules.add(new ExactWordPatternRule(onlyLettersWordDetector,"WARNING",logWarn));
		    rules.add(new ExactWordPatternRule(onlyLettersWordDetector,"WARN",logWarn));
		    rules.add(new ExactWordPatternRule(onlyLettersWordDetector,"ERROR",logError));
		}

		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}
	
	private IToken createToken(JunitServerResultDocumentIdentifier identifier) {
		return new Token(identifier.getId());
	}
}
