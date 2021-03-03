package de.jcup.junit.serverresult.editor;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class JunitServerResultWhitespaceDetector implements IWhitespaceDetector {

	@Override
	public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
}