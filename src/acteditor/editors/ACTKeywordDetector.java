package acteditor.editors;

import org.eclipse.jface.text.rules.IWordDetector;

public class ACTKeywordDetector implements IWordDetector {
	
	@Override
	public boolean isWordPart(char c) {
		return Character.isLetter(c);
	}

	@Override
	public boolean isWordStart(char c) {
		return Character.isLetterOrDigit(c);
	}

}
