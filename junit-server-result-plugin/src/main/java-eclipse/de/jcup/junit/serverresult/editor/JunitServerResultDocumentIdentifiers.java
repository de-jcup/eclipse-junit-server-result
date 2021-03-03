package de.jcup.junit.serverresult.editor;
public enum JunitServerResultDocumentIdentifiers implements JunitServerResultDocumentIdentifier {
    LOG_DEBUG,

    LOG_INFO,
	
    LOG_WARN,

    LOG_ERROR,
	
	;


	@Override
	public String getId() {
		return name();
	}
	public static String[] allIdsToStringArray(){
		return allIdsToStringArray(null);
	}
	public static String[] allIdsToStringArray(String additionalDefaultId){
		JunitServerResultDocumentIdentifiers[] values = values();
		int size = values.length;
		if (additionalDefaultId!=null){
			size+=1;
		}
		String[] data = new String[size];
		int pos=0;
		if (additionalDefaultId!=null){
			data[pos++]=additionalDefaultId;
		}
		for (JunitServerResultDocumentIdentifiers d: values){
			data[pos++]=d.getId();
		}
		return data;
	}

}
