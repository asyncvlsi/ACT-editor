package acteditor.editors;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class ACTOutlineAdapterFactory implements IAdapterFactory {

	@Override
	public Class<?>[] getAdapterList() {
		return new Class<?>[] { IContentOutlinePage.class };
	}

	@Override
	public <T> T getAdapter(Object obj, Class<T> required) {
		if (IContentOutlinePage.class.equals(required)) {
            ACTEditor editor = (ACTEditor) obj;
            return (T) new ACTContentOutlinePage(editor.getDocumentProvider(), editor);
         }	
		return (T) null; 
      }
}
