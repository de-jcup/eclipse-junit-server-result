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
