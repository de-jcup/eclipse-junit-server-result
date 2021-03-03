package de.jcup.junit.serverresult.editor;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;

import de.jcup.junit.serverresult.ColorManager;

public class JunitServerResultDefaultTextScanner extends RuleBasedScanner {

	public JunitServerResultDefaultTextScanner(ColorManager manager) {
		IRule[] rules = new IRule[1];
		rules[0] = new WhitespaceRule(new JunitServerResultWhitespaceDetector());

		setRules(rules);
	}
}
