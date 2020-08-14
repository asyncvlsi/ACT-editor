package acteditor.editors;

import org.eclipse.jface.text.*;
import org.eclipse.jface.text.rules.*;

public class ACTCommentScanner extends RuleBasedScanner{
	public ACTColors colors;
	
	public ACTCommentScanner(ACTColors colors) {
		IToken comment_token = new Token(new TextAttribute(colors.getColor(ACTColorConstants.LANG_COMMENT)));
		setDefaultReturnToken(comment_token);
	}
}