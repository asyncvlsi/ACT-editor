package acteditor.editors;

import org.eclipse.jface.text.IDocument;
//import org.eclipse.jdt.internal.ui.text.java.JavaFormattingStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
//import org.eclipse.jface.text.formatter.IContentFormatter;
//import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jdt.internal.ui.text.java.JavaFormattingStrategy;
import org.eclipse.jface.text.DefaultAutoIndentStrategy;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
//import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;


public class ACTSourceViewerConfiguration extends SourceViewerConfiguration {
	/**
	 * Single token scanner.
	 */
	static class SingleTokenScanner extends BufferedRuleBasedScanner {
		public SingleTokenScanner(TextAttribute attribute) {
			setDefaultReturnToken(new Token(attribute));
		}
	}

	private ACTKeywordScanner k_scanner;
	private ACTCommentScanner c_scanner;
	private ACTColors colors;
	private ACTEditor editor;

	public ACTSourceViewerConfiguration(ACTColors colors, ACTEditor editor) {
		this.colors = colors;
		this.editor = editor;
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE, ACTPartitionScanner.LANG_COMMENT,
				ACTPartitionScanner.LANG_STRING };
	}

	protected ACTCommentScanner getMyCommentScanner() {
		if (c_scanner == null) {
			c_scanner = new ACTCommentScanner(colors);
		}
		return c_scanner;
	}

	protected ACTKeywordScanner getMyKeywordScanner() {
		if (k_scanner == null) {
			k_scanner = new ACTKeywordScanner(colors);
			k_scanner.setDefaultReturnToken(new Token(new TextAttribute(colors.getColor(ACTColorConstants.DEFAULT))));
		}
		return k_scanner;
	}


	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		
		PresentationReconciler reconciler = new PresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getMyKeywordScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new DefaultDamagerRepairer(getMyKeywordScanner());
		reconciler.setDamager(dr, ACTPartitionScanner.LANG_STRING);
		reconciler.setRepairer(dr, ACTPartitionScanner.LANG_STRING);

		dr = new DefaultDamagerRepairer(getMyCommentScanner());
		reconciler.setDamager(dr, ACTPartitionScanner.LANG_COMMENT);
		reconciler.setRepairer(dr, ACTPartitionScanner.LANG_COMMENT);
		
		return reconciler;
	}
	
	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		ACTBracketReconcilingStrategy strategy = new ACTBracketReconcilingStrategy();
		strategy.setEditor(editor);
		
		//ACTContentOutlinePage 
		MonoReconciler reconciler = new MonoReconciler(strategy, false);
		
		return reconciler;
	}
	

	
	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		IAutoEditStrategy strategy = (IDocument.DEFAULT_CONTENT_TYPE.equals(contentType) ? new ACTAutoIndentationStrategy() : new DefaultIndentLineAutoEditStrategy());
		return new IAutoEditStrategy[] { strategy };
	}
	
	
	@Override
	public int getTabWidth(ISourceViewer sourceViewer) {
		return 4;
	}
	
//	@Override
//	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
//	  final MultiPassContentFormatter formatter= new MultiPassContentFormatter(getConfiguredDocumentPartitioning(sourceViewer), IDocument.DEFAULT_CONTENT_TYPE);
//	  formatter.setMasterStrategy(new DefaultIndentLineAutoEditStrategy());
//	  return formatter;
//	}

}
