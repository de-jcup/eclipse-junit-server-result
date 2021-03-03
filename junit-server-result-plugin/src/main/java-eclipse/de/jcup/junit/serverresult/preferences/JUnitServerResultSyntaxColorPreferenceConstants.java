package de.jcup.junit.serverresult.preferences;
public enum JUnitServerResultSyntaxColorPreferenceConstants implements PreferenceIdentifiable, PreferenceLabeled{
	COLOR_NORMAL_TEXT("colorNormalText","Normal text color"),
	
	COLOR_LOG_DEBUG("colorLogDebug","Log DEBUG"),
	
	COLOR_LOG_INFO("colorLogInfo","Log INFO"),
	
	COLOR_LOG_WARN("colorLogWarn","Log WARN"),
	
	COLOR_LOG_ERROR("colorLogError","Log ERROR"),
	
	;

	private String id;
	private String labelText;

	private JUnitServerResultSyntaxColorPreferenceConstants(String id, String labelText) {
		this.id = id;
		this.labelText=labelText;
	}

	public String getLabelText() {
		return labelText;
	}
	
	public String getId() {
		return id;
	}

}
