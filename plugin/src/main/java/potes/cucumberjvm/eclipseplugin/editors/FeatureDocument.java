package potes.cucumberjvm.eclipseplugin.editors;

import org.eclipse.jface.text.Document;

public class FeatureDocument extends Document {
	
	private String language = "en";

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}
