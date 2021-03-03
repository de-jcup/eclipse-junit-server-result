package de.jcup.junit.serverresult.editor;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.WordPatternRule;

public class ExactWordPatternRule extends WordPatternRule{

	private String toStringValue;
	StringBuilder traceSb;
	boolean trace = false;
	
	private int allowedPrefix=-1;
	private int allowedPostfix=-1;
	
	public void setAllowedPostfix(char allowedPostfix) {
		this.allowedPostfix = allowedPostfix;
	}
	public void setAllowedPrefix(char allowedPrefix) {
		this.allowedPrefix = allowedPrefix;
	}
	
	public ExactWordPatternRule(IWordDetector detector, String exactWord, IToken token) {
		this(detector,exactWord,token,true);
	}
	
	public ExactWordPatternRule(IWordDetector detector, String exactWord, IToken token, boolean breaksOnEOF) {
		super(detector, exactWord, null, token);
		toStringValue=getClass().getSimpleName()+":"+exactWord;
		this.fBreaksOnEOF=breaksOnEOF;
	}
	
	protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed) {
		/* sequence is not the word found by word detector but the start sequence!!!!! (in this case always the exact word)*/
		
		// -------------------------------------------------
		// example: exactWord='test' 
		// 
		// subjects: atest,test,testa
		//                   ^----------------only result!
		Counter counter = new Counter();
		if (trace){
			// trace contains NOT first character, this is done at PatternRule
			traceSb = new StringBuilder();
		}
		int column=scanner.getColumn();
		boolean wordHasPrefix;
		if (column==1){
			wordHasPrefix=false;
		}else{
			scannerUnread(scanner, counter);
			scannerUnread(scanner, counter);
			char charBefore =(char)scannerRead(scanner, counter);
			scannerRead(scanner, counter);
			wordHasPrefix = isIllegalPrefixCharacter(charBefore);
		}
		if (wordHasPrefix){
			scannerRead(scanner, counter);
			return counter.cleanupAndReturn(scanner,false);
		}
		for (int i= 1; i < sequence.length; i++) {
			int c= scannerRead(scanner, counter);
			if (c == ICharacterScanner.EOF){
				if (eofAllowed) {
					return counter.cleanupAndReturn(scanner,true);
				}else{
					return counter.cleanupAndReturn(scanner,false);
				}
			} else if (c != sequence[i]) {
				scannerUnread(scanner, counter);
				for (int j= i-1; j > 0; j--){
					scannerUnread(scanner, counter);
				}
				return counter.cleanupAndReturn(scanner,false);
			}
		}
		int read = scannerRead(scanner, counter);
		char charAfter = (char)read;
		scannerUnread(scanner, counter);
		
		/* when not allowedPostFix and not a whitespace and not end reached - do cleanup*/
		if (charAfter!=allowedPostfix && ! Character.isWhitespace(charAfter) && ICharacterScanner.EOF!=read){
			/* the word is more than the exact one - e.g. instead of 'test' 'testx' ... so not correct*/
			return counter.cleanupAndReturn(scanner,false);
		}
		return counter.cleanupAndReturn(scanner,true);
	}

	
	private boolean isIllegalPrefixCharacter(char charBefore) {
		if (charBefore==allowedPrefix){
			return false;
		}
		boolean isPrefix = ! Character.isWhitespace(charBefore);
		return isPrefix;
	}

	private int scannerRead(ICharacterScanner scanner, Counter counter) {
		int c = scanner.read();
		if (c==ICharacterScanner.EOF){
			return c;
		}
		counter.count++;
		if (trace){
			traceSb.append((char)c);
		}
		return c;
		
	}

	private void scannerUnread(ICharacterScanner scanner, Counter counter) {
		scanner.unread();
		counter.count--;
		if (trace){
			int length = traceSb.length();
			if (length<1){
				traceSb.append("[(-1)]");
			}else{
				length=length-1;
				traceSb.setLength(length);
			}
		}
	}

	@Override
	public String toString() {
		return toStringValue;
	}
	
}