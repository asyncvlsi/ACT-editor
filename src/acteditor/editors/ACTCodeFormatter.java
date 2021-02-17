package acteditor.editors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.jface.text.formatter.IFormattingStrategyExtension;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;

public class ACTCodeFormatter extends MultiPassContentFormatter implements IFormattingStrategyExtension{
	private IDocument document;
	private String partitioning;
	private String type;
	private IFormattingContext context;
	
	public ACTCodeFormatter(String partitioning, String type, IDocument document) {
		super(partitioning, type);
		this.partitioning = partitioning;
		this.type = type;
		this.document = document;
	}

	@Override
	public void formatMaster(IFormattingContext context, IDocument document, int offset, int length) {

		/* Implement later */
	}
	
	public void format() {
		
	}

	@Override
	public void formatterStarts(IFormattingContext arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void formatterStops() {
		// TODO Auto-generated method stub
		
	}

}
