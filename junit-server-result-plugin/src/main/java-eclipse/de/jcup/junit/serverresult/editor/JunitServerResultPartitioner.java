package de.jcup.junit.serverresult.editor;

import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

public class JunitServerResultPartitioner extends FastPartitioner{

	public JunitServerResultPartitioner(IPartitionTokenScanner scanner, String[] legalContentTypes) {
		super(scanner, legalContentTypes);
	}

}
