package acteditor.editors;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.IEditorPart;

public class ACTFormattingHandler extends AbstractHandler {
	private ACTEditor editor;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		editor = (ACTEditor) HandlerUtil.getActiveEditor(event);

		if (editor != null) {
			((SourceViewer) editor.getInternalSourceViewer()).doOperation(ISourceViewer.FORMAT);
		}

		return null;
	}

}
