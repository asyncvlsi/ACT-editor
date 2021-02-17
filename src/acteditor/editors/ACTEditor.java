package acteditor.editors;

import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import java.util.List;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.util.HashMap;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.source.*;
import org.eclipse.jface.text.source.projection.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.jface.text.Position;

public class ACTEditor extends TextEditor {
	private ACTColors colors;
	private Annotation[] oldAnnotations;
	private ProjectionAnnotationModel annotationModel;
	private ACTVerifyKeyListener bracketListener;
	private ACTContentOutlinePage fOutlinePage;
	private ProjectionSupport fProjectionSupport;
	public ISourceViewer viewer;
	public List<Position> pos; 
	
	public ACTEditor() {
		super();
		colors = new ACTColors();
		setSourceViewerConfiguration(new ACTSourceViewerConfiguration(colors, this));
		setDocumentProvider(new ACTDocumentProvider());
	}
	
	public ISourceViewer getInternalSourceViewer() {
		return this.viewer;
	}
	
	public List<Position> getPositions() {
		return pos;
	}
	
	public IContentOutlinePage getContentOutlinePage() {
		return fOutlinePage;
	}
	@Override
	public void dispose() {
		colors.dispose();
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		// setting up the projection viewer
		ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();

		ProjectionSupport projectionSupport = new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
		projectionSupport.install();

		// turn projection mode on
		viewer.doOperation(ProjectionViewer.TOGGLE);

		annotationModel = viewer.getProjectionAnnotationModel();

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> required) {
		if (IContentOutlinePage.class.equals(required)) {
			if (fOutlinePage == null) {
				fOutlinePage= new ACTContentOutlinePage(getDocumentProvider(), this);
				if (getEditorInput() != null)
					fOutlinePage.setInput(getEditorInput());
			}
			

			return (T) fOutlinePage;
		}
		


		if (fProjectionSupport != null) {
			T adapter= fProjectionSupport.getAdapter(getSourceViewer(), required);
			if (adapter != null)
				return adapter;
		}

		return super.getAdapter(required);
	}
	
	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		// get the source viewer to return a projection viewer instead
		ISourceViewer viewer = new ProjectionViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(),
				styles);
		
		this.viewer = viewer;
		
		bracketListener = new ACTVerifyKeyListener();

		if (viewer instanceof ITextViewerExtension) {
			((ITextViewerExtension) viewer).appendVerifyKeyListener(bracketListener);
		}

		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);
		

		return viewer;

	}
		

	public void updateFoldingStructure(List<Position> positions) {
		
		
		Annotation[] annotations = new Annotation[positions.size()];

		// this will hold the new annotations along
		// with their corresponding positions

		HashMap<ProjectionAnnotation, Position> newAnnotations = new HashMap<ProjectionAnnotation, Position>();

		for (int i = 0; i < positions.size(); i++) {

			ProjectionAnnotation annotation = new ProjectionAnnotation();

			newAnnotations.put(annotation, positions.get(i));

			annotations[i] = annotation;

		}

		annotationModel.modifyAnnotations(oldAnnotations, newAnnotations, null);

		oldAnnotations = annotations;
	}
	
	@Override
	public void setFocus() {
	     getPreferenceStore().setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER, true);
	     getPreferenceStore().setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLUMN, 80);
	    super.setFocus();
	}
	
	@Override
	public void editorSaved() {
		if(fOutlinePage != null) {
			fOutlinePage.update();
		}
	}
	

}
