//Followed tutorial at 
//https://www.eclipse.org/articles/Article-Folding-in-Eclipse-Text-Editors/folding.html
//as well as code in org.eclipse.ui.genericEditor.IndentFoldingStrategy


// This is a code folding strategy based on the 
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
	protected List<Position> fPositions = new ArrayList<>();

	/**
	 * The current document 
	 * @param document: The current document
	 */
	@Override
	public void setDocument(IDocument document) {
		this.fDocument = document;
	}
	
	/**
	 * The current editor instance
	 * @param editor the ACT editor instance 
	 */
	public void setEditor(ACTEditor editor) {
		this.editor = editor;
	}

	
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		fPositions.clear();
		begin(dirtyRegion.getOffset(), dirtyRegion.getLength());
	}

	public void reconcile(IRegion partition) {
		fPositions.clear();
		begin(partition.getOffset(), partition.getLength());
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

	/** 
	 * This function initiates calls to recalculate the folding regions.
	 * it clears the map holding  
	 * 
	 * @param offset 	Offset from the beginning of the file
	 *  				from which to begin calculations.
	 *  
	 * @param length	length from offset of text to consider
	 */
	
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
				editor.pos = fPositions;
			}
		});
	}

	
	/**
	 * This function checks if the current line is an import statement
	 * 
	 * @param text the text in the current line
	 * @return
	 */
	private boolean hasImport(String text) {

		text = text.strip();
		
	
		int delim = text.indexOf(' ');
		String first = text.substring(0, delim);
		
		if (delim > 0 &&  first.contains("import")){
			return true;
		}

		return false;
	}

	public void calculateBracketPositions(int offset, int length) throws BadLocationException {

		int start=1, end=1; /* start and end of imports to be folded */

		/* Check for import statements. will always be at the top */

		int line; /* the current line */
		int total_lines = fDocument.getNumberOfLines();
		boolean inside_comment = false;

		for (line = 0; line < total_lines; line++) {

			
			int line_offset = fDocument.getLineOffset(line);
			int line_length = fDocument.getLineLength(line);
			
			String text = fDocument.get(line_offset, line_offset + line_length).strip();
			
			
			if(inside_comment) {
				if(text.contains("*/"))
					inside_comment= false;
				continue;
			}
			
			/* Comments at the start of document? */
			if(text.length() > 2 && text.charAt(0) == '/') {
				
				if(text.charAt(1) == '/') { 		/* single line comment */
					continue;
				}
				
				else if(text.charAt(1) == '*'){		/* multi-line comment */
					inside_comment = true;
				}

			}


			/* empty lines? */
			else if(text == "\n"){
				continue;
			}
	
			/* import statement here ? */
			else if (hasImport(text) == true) {
				start = line;

				while (line < total_lines && hasImport(text)) {
					line_offset = fDocument.getLineOffset(line);
					line_length = fDocument.getLineLength(line);
					
					text = fDocument.get(line_offset, line_offset + line_length);
					line++;
				}
				
				

				end = line--;
				
				
				int start_offset = fDocument.getLineOffset(start);
				int end_offset = fDocument.getLineOffset(end);
				emitPosition(start_offset, end_offset - start_offset);
				break;
			}
			
			/* Actual code starts here, or maybe a comment was encountered 
			 * Might not behave appropriately if comments and imports are intermixed
			 * */
			else {
				break;
			}

		}

		// we're using one based indexing

		int currentLine = 1; // start checking from where the imports end, if any 
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

	/**
	 * Gets the position of a closing bracket, if any 
	 * @param line
	 * @param column
	 * @param charPosition
	 * @param content
	 * @return
	 * @throws BadLocationException
	 */
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
