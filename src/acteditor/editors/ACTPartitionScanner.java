package acteditor.editors;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.jface.text.rules.*;


public class ACTPartitionScanner extends RuleBasedPartitionScanner {

		public final static String LANG_COMMENT  = "__lang_comment";
		public final static String LANG_STRING = "__lang_string";
		
		public ACTPartitionScanner() {
		
		Token comment = new Token(LANG_COMMENT);
		Token string = new Token(LANG_STRING);
		
		//set the rules for recognizing tokens
		final List<IRule> rules = new ArrayList<IRule>();
		
		rules.add(new SingleLineRule("\"", "\"", string, '\\')); 
		rules.add(new SingleLineRule("'", "'", string, '\\'));
		rules.add(new MultiLineRule("/*", "*/", comment));
		rules.add(new SingleLineRule("//", null, comment, (char) 0));
		
		
		final IPredicateRule[] result = new IPredicateRule[rules.size()];
		rules.toArray(result);
		
		setPredicateRules(result);
		
		}
}
