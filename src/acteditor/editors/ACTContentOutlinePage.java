package acteditor.editors;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.event.DocumentListener;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.Position;

import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/* Modified org.eclipse.ui.examples.javaeditor.JavaContentOutlinePage */

/**
 * A content outline page which always represents the content of the connected
 * editor.
 */
public class ACTContentOutlinePage extends ContentOutlinePage {

	/**
	 * A segment element.
	 * A segment may be a namespace or any structure that may have others 
	 * nested within it. Any nested segments are stored as children. 
	 */
	protected static class Segment{
		public String name;
		public Position position;
		private Position scope = new Position(0);
		public List<Segment> children = new ArrayList<>();
		public Segment parent;

		public Segment(String name, Position position) {
			this.name = name;
			this.position = position;
		}

		@Override
		public String toString() {
			return name;
		}

		public List<Segment> getChildren() {
			return this.children;
		}

		public boolean hasChildren() {
			return children.size() > 0;
		}

		public void addChild(Segment s) {
			children.add(s);
			s.parent = this;
		}

		public void addScope(Position p) {
			this.scope = p;
		}

		public Segment getParent() {
			return parent;
		}

	}

	/**
	 * Divides the editor's document into ten segments and provides elements for
	 * them.
	 */
	protected class ACTContentProvider implements ITreeContentProvider {

		protected final static String SEGMENTS = "__act_segments"; //$NON-NLS-1$
		protected IPositionUpdater fPositionUpdater = new DefaultPositionUpdater(SEGMENTS);

		protected Segment fContent;

		private final String keywords[] = { "namespace", "defproc", "defcell", "deftype", "defchan", "defenum",
				"function" };


		private Position getScope(IDocument document, int offset) throws BadLocationException {
			
			String content = document.get();
			int line_start = document.getLineOfOffset(offset);
			int curr_line = line_start;
			int start = -1, end = -1, braceCount = 0; // start and end of braces

			for (int i = offset; i < content.length(); i++) {

				// Increment on newline
				if (content.charAt(i) == '\n' || content.charAt(i) == '\r') {
					curr_line++;
				}

				// Ignore lines with comments
				else if (content.charAt(i) == '/' && content.charAt(i + 1) == '/') {
					if (curr_line + 1 <= document.getNumberOfLines()) {
						i = document.getLineOffset(curr_line + 1);
						curr_line++;
					} else {
						break;
					}
				}

				// Multi-line comment
				else if (content.charAt(i) == '/' && content.charAt(i + 1) == '*') {
					while (i < content.length() - 1) {
						if (content.charAt(i) == '*' && content.charAt(i + 1) == '/') {
							break;
						} else {
							i++;
						}
					}
					
				} else if (content.charAt(i) == '{') { // opening brace
					braceCount++;

					if (start < 0) {
						start = i;
					}
					
				} else if (content.charAt(i) == '}') {
					braceCount--;

					if (braceCount == 0) {
						end = i;

						if (start > 0)
							return new Position(start, end - start);

					}

				}
			}

			return new Position(0);
		}

		/**
		 * Parses the entire document and builds the tree 
		 * of segment objects.
		 * 
		 * @param document The current document
		 */
		protected void parse(IDocument document) {

			int lines = document.getNumberOfLines();
			
			// The parent segment
			fContent = new Segment("Main File", new Position(0, document.getLength()));

			for (int line = 0; line < lines; line++) {
				// search for keywords in the line

				try {
					int line_offset = document.getLineOffset(line);
					int line_length = document.getLineLength(line);
					String text = document.get(line_offset, line_length);
					Position p = new Position(line_offset, line_length);

					for (String word : keywords) {
						if (text.contains(word)) {
							Segment s = new Segment(word, p);
							s.scope = getScope(document, line_offset);				
							addToList(fContent, s, getNextWord(text, word), document);
						}
					}

				} catch (BadLocationException x) {
				}

			}
		}

		private void addToList(Segment fContent, Segment s, String word, IDocument document) {

			/* Add to the appropriate segment */			
			for (Object seg : fContent.children) {
				
				try {
						Segment segment = ((Segment) seg);
						
						int seg_start = document.getLineOfOffset(segment.scope.offset);
						int seg_end = document.getLineOfOffset(segment.scope.offset + segment.scope.length);
						int s_start = document.getLineOfOffset(s.scope.offset);
						int s_end = document.getLineOfOffset(s.scope.offset + s.scope.length);

						if (segment.name.contains("namespace") 
								&& seg_start <= s_start 
								&& seg_end >= s_end) {

							addToList(segment, s, word, document);
							return;
						}
				} catch (BadLocationException e) {

					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			String name = MessageFormat.format("{1} : {0}", s.name, word);
			Position p = s.position;
			Segment temp = new Segment(name, p);
			temp.scope = s.scope;
			fContent.addChild(temp);

		}

		private String getNextWord(String str, String word) {
			try {
				List<String> text = Arrays.asList(str.split("\\s+"));
				int index_of;
				index_of = text.indexOf(word);
				return (index_of == -1) ? "[no name]" : text.get(index_of + 1);
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			viewer.refresh();
			if (oldInput != null) {
				IDocument document = fDocumentProvider.getDocument(oldInput);
				if (document != null) {
					try {
						document.removePositionCategory(SEGMENTS);
					} catch (BadPositionCategoryException x) {
					}
					document.removePositionUpdater(fPositionUpdater);
				}
			}

			if (fContent != null) {
				fContent.children.clear();
			}

			if (newInput != null) {
				IDocument document = fDocumentProvider.getDocument(newInput);
				if (document != null) {
					document.addPositionCategory(SEGMENTS);
					document.addPositionUpdater(fPositionUpdater);
					parse(document);
				}
			}
		}

		/*
		 * @see IContentProvider#dispose
		 */
		@Override
		public void dispose() {
			if (fContent != null) {
				fContent.getChildren().clear();
				fContent = null;
			}
		}

		@Override
		public Object[] getElements(Object element) {
			 if (element instanceof Segment)
				return ((Segment) element).children.toArray();
			return fContent.children.toArray();
		//	 return new Object[0];
		}

		@Override
		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}

		@Override
		public Object getParent(Object element) {
			if (element instanceof Segment)
				return ((Segment) element).getParent();
			return null;
		}

		@Override
		public Object[] getChildren(Object element) {
			if (element instanceof Segment) {
				Segment s = (Segment) element;
				return s.children.toArray();
			}
			return new Object[0];
		}
	}

	protected Object fInput;
	protected IDocumentProvider fDocumentProvider;
	protected ACTEditor fTextEditor;

	/**
	 * Creates a content outline page using the given provider and the given editor.
	 *
	 * @param provider the document provider
	 * @param editor   the editor
	 */
	public ACTContentOutlinePage(IDocumentProvider provider, ITextEditor editor) {
		super();
		fDocumentProvider = provider;
		fTextEditor = (ACTEditor) editor;
	}

	@Override
	public void createControl(Composite parent) {

		super.createControl(parent);

		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new ACTContentProvider());
		viewer.setLabelProvider(new ACTLabelProvider());
		viewer.addSelectionChangedListener(this);

		if (fInput != null)
			viewer.setInput(fInput);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {

		super.selectionChanged(event);

		IStructuredSelection selection = event.getStructuredSelection();
		if (selection.isEmpty())
			fTextEditor.resetHighlightRange();
		else {
			Segment segment = (Segment) selection.getFirstElement();
			int start = segment.position.getOffset();
			int length = segment.position.getLength();
			try {
				fTextEditor.setHighlightRange(start, length, true);
			} catch (IllegalArgumentException x) {
				fTextEditor.resetHighlightRange();
			}
		}
	}

	/**
	 * Sets the input of the outline page
	 *
	 * @param input the input of this outline page
	 */
	public void setInput(Object input) {
		fInput = input;
		update();
	}

	/**
	 * Updates the outline page.
	 */
	public void update() {
		TreeViewer viewer = getTreeViewer();

		if (viewer != null) {
			Control control = viewer.getControl();
			if (control != null && !control.isDisposed()) {
				control.setRedraw(false);
				viewer.setInput(fInput);
				viewer.expandAll();
				control.setRedraw(true);
			}

		}

	}

}
