//Followed tutorial at 
//https://www.eclipse.org/articles/Article-Folding-in-Eclipse-Text-Editors/folding.html
//as well as code in org.eclipse.ui.genericEditor.IndentFoldingStrategy

package acteditor.editors;


import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.swt.widgets.Display;
 

public class ACTBracketReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {

	private ACTEditor editor;
	private IDocument fDocument;

	protected int fOffset = 0;
	protected int fRangeEnd = 0;

	protected  List<Position> fPositions = new ArrayList<>();

	@Override
	public void setDocument(IDocument document) {
		this.fDocument = document;
	}

	public void setEditor(ACTEditor editor) {
		this.editor = editor;
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		begin(dirtyRegion.getOffset(), dirtyRegion.getLength());
	}

	public void reconcile(IRegion partition) {
		//begin(partition.getOffset(), partition.getLength());
	}

	@Override
	public void setProgressMonitor(IProgressMonitor monitor) {
		// none in use now
	}
	
//	public void setSourceViewer(ISourceViewer viewer) {
//		this.viewer = viewer;
//	}
	
	@Override
	public void initialReconcile() {
		reconcile(new DirtyRegion(0, fDocument.getLength(), DirtyRegion.INSERT, fDocument.get()), null);
		
	}

	protected void begin(int offset, int length) {
		fPositions.clear();

		try {
			calculateBracketPositions(offset, length);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				editor.updateFoldingStructure(fPositions);
			}

		});
	}

	public void calculateBracketPositions(int offset, int length) throws BadLocationException {

		// we're using one based indexing

		int currentLine = 1;
		int currentColumn = 1; // the column of the current line

		String content = fDocument.get(); // gets the whole document as a single string;
		int contentLength = content.length();

		for (int i = 0; i < contentLength; i++) {

			if (content.charAt(i) == '\n' || content.charAt(i) == '\r') {

				currentLine++;
				currentColumn = 1;

			} else {

				currentColumn++;

			}

			if (content.charAt(i) == '{') {
				int openBracket = 0, closingBracket = 0;
				try {
					openBracket = fDocument.getLineOffset(currentLine - 1) + currentColumn - 1;
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				try {
					closingBracket = getClosingBracket(currentLine, currentColumn, i, content);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}

				// ignore brackets outside of our working range
				if (!((openBracket >= offset) && closingBracket <= (offset + length))) {
					continue;
				}
				if (closingBracket != -1 && closingBracket > 0) { // found
					emitPosition(openBracket, closingBracket - openBracket);
				}
			}
		}

	}

	// moves until it reaches the end of a multi-line comment
	private int getClosingBracket(int line, int column, int charPosition, String content) throws BadLocationException {
		int startLine = line;
		int closingBracketOffset = 0;
		int j = charPosition;
		boolean done = false;
		int docLength = content.length();
		int bracketValue = 0;

		while (!done && (j < docLength)) {
			if (content.charAt(j) == '\n') {
				line++;
				column = 1;
			} else {
				column++;
			}

			// let intermediate open brackets = 1, closing brackets = -1;
			// we are done when we get a value of 0;

			if (content.charAt(j) == '{') {
				bracketValue++;
			}

			if (content.charAt(j) == '}') {

				bracketValue--;

				if (bracketValue == 0) { // found the closing bracket
					done = true;
					if (startLine != line) {
						try {
							closingBracketOffset = fDocument.getLineOffset(line - 1) + column - 1;
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					} else {
						closingBracketOffset = -1;
					}
				}
			}

			j++; // increment the position on each iteration.
		}

		return closingBracketOffset;

	}

	public void emitPosition(int startPosition, int length) {
		fPositions.add(new Position(startPosition, length));
	}

}
