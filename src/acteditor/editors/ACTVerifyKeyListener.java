package acteditor.editors;

import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.VerifyEvent;

 
public class ACTVerifyKeyListener implements VerifyKeyListener {
	public final static char open_brackets[] = { '{', '[', '(' };
	
	@Override
	public void verifyKey(VerifyEvent event) {

		for (char c : open_brackets) {
			if (c == event.character) {
				StyledText text = (StyledText) event.getSource();
				//event.doit = true;
				switch (c) {
				case '{':
					text.insert(String.valueOf('}'));
					break;
				case '[':
					text.insert(String.valueOf(']'));
					break;
				case '(':
					text.insert(String.valueOf(')'));
					break;
				}
			}
		}

	}
}
