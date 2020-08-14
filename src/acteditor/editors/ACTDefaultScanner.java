package acteditor.editors;


import org.eclipse.jface.text.*;
import org.eclipse.jface.text.rules.*;

public class ACTDefaultScanner extends RuleBasedScanner {

	public ACTDefaultScanner(ACTColors colors) {
		IToken default_word = new Token(new TextAttribute(colors.getColor(ACTColorConstants.DEFAULT)));

		setDefaultReturnToken(default_word);

		setRules(new IRule[] { new WhitespaceRule(new ACTWhiteSpaceDetector())});
	}
}
