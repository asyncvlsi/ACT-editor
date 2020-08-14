package acteditor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.*;
import org.eclipse.jface.text.rules.*;

public class ACTKeywordScanner extends RuleBasedScanner {

	// keywords
	public static String modules [] = { "import", "open", "export", "namespace", "defproc", "defcell", "template",
			"deftype", "defchan", "defenum", "function", "interface" };
	
	public static String builtins [] = { "pint", "pbool", "preal","ptype",
			"int","ints","bool","enum","chan"};
	
	public static String local_s[] = {"else","skip","passn","passp","transgate",
			"requires","ensures","timing"};

	public static String others [] = {"methods","chp","hse","prs","spec","refine","sizing",
			"Initialize", "dataflow" };


	public ACTKeywordScanner(ACTColors colors) {
		//the default text token
		IToken def = new Token(new TextAttribute(colors.getColor(ACTColorConstants.DEFAULT)));
		
		//for other words
		IToken module = new Token(new TextAttribute(colors.getColor(ACTColorConstants.DEF_MOD)));
		IToken builtin = new Token(new TextAttribute(colors.getColor(ACTColorConstants.BUILTIN)));
		IToken local = new Token(new TextAttribute(colors.getColor(ACTColorConstants.LOCAL_S)));
		IToken other  = new Token(new TextAttribute(colors.getColor(ACTColorConstants.OTHERS)));
		IToken string = new Token(new TextAttribute(colors.getColor(ACTColorConstants.STRING)));
		
		// set the rules for recognizing tokens
		final List<IRule> rules = new ArrayList<IRule>();
		
		WordRule wordrule = new WordRule(new ACTKeywordDetector(), def);
		
		for(String s : modules) {
			wordrule.addWord(s, module);
		}
		
		for(String s : builtins) {
			wordrule.addWord(s, builtin);
		}
		
		for(String s : local_s) {
			wordrule.addWord(s, local);
		}
		
		for(String s: others) {
			wordrule.addWord(s, other);
		}
		
		
		rules.add(new SingleLineRule("\"", "\"", string, '\\')); 
		rules.add(new SingleLineRule("'", "'", string, '\\'));
		rules.add(new WhitespaceRule(new ACTWhiteSpaceDetector()));
		rules.add(wordrule);
		
		IRule[] result= new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}
}