package de.jcup.junit.serverresult.editor;
import org.eclipse.jface.text.rules.IWordDetector;

public class OnlyLettersKeyWordDetector implements IWordDetector{
	
	@Override
	public boolean isWordStart(char c) {
		if (! Character.isLetter(c)){
			return false;
		}
		return true;
	}

	@Override
	public boolean isWordPart(char c) {
		if (! Character.isLetter(c)){
			return false;
		}
		return true;
	}
}
