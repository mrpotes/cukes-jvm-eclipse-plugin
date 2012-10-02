package potes.cucumberjvm.eclipseplugin.editors;

import gherkin.I18n;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;

public class FeatureDocument extends Document {
	
	private static final Pattern LANGUAGE_PATTERN = Pattern.compile("^# language: (\\S*).*$");
	private static final Map<String,I18n> TRANSLATIONS = new HashMap<String, I18n>();
	private static final String DEFAULT_LANGUAGE = "en";
	
	static {
		try {
			for (I18n translation : I18n.getAll()) {
				TRANSLATIONS.put(translation.getIsoCode(), translation);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private I18n language;
	private FeaturePartitionScanner scanner;

	public I18n getLanguage() {
		return language;
	}

	@Override
	public void set(String text, long modificationStamp) {
		super.set(text, modificationStamp);
		setLanguageFromContent(text, null);
	}

	public void setLanguageFromContent(String text, DocumentEvent event) {
		I18n original = language;
		if (text.startsWith("# language:")) {
			Matcher m = LANGUAGE_PATTERN.matcher(text.substring(0, text.indexOf('\n')));
			m.find();
			String languageCode = m.group(1);
			language = TRANSLATIONS.get(languageCode);
		} 
		if (language == null) {
			language = TRANSLATIONS.get(DEFAULT_LANGUAGE);
		}
		if (original != null && !original.equals(language) && event != null) {
			scanner.setRules(language);
			updateDocumentStructures(event);
			doFireDocumentChanged(event);
		}
	}

	public void setScanner(FeaturePartitionScanner scanner) {
		this.scanner = scanner;
	}
}
