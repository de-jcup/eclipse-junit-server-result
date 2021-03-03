package de.jcup.junit.serverresult.editor;
public class JunitServerResultPartionerFactory {

	public static JunitServerResultPartitioner create(){
		String[] legalContentTypes = JunitServerResultDocumentIdentifiers.allIdsToStringArray();

		JunitServerResultDocumentPartitionScanner scanner = new JunitServerResultDocumentPartitionScanner();
		JunitServerResultPartitioner partitioner = new JunitServerResultPartitioner(scanner, legalContentTypes);
		
		return partitioner;
	}
}
