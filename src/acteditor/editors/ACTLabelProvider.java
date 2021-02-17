package acteditor.editors;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import acteditor.editors.ACTContentOutlinePage.Segment;

public class ACTLabelProvider extends LabelProvider {

	//private Bundle bundle = Platform.getBundle("acteditor.editors");
	private Bundle bundle = FrameworkUtil.getBundle(getClass());
	
	final URL url_namespace= FileLocator.find(bundle, new Path("icons/namespace.png"), null);
	final URL url_defproc= FileLocator.find(bundle, new Path("icons/defproc.png"), null);
	final URL url_defchan= FileLocator.find(bundle, new Path("icons/defchan.png"), null);
	final URL url_defcell= FileLocator.find(bundle, new Path("icons/defcell.png"), null);
	final URL url_deftype= FileLocator.find(bundle, new Path("icons/deftype.png"), null);
	final URL url_defenum= FileLocator.find(bundle, new Path("icons/defenum.png"), null);
	final URL url_function= FileLocator.find(bundle, new Path("icons/function.png"), null);
	
	private Image img_namespace = ImageDescriptor.createFromURL(url_namespace).createImage();
	private Image img_defproc =  ImageDescriptor.createFromURL(url_defproc).createImage();
	private Image img_defchan =  ImageDescriptor.createFromURL(url_defchan).createImage();
	private Image img_defcell =  ImageDescriptor.createFromURL(url_defcell).createImage();
	private Image img_deftype = ImageDescriptor.createFromURL(url_deftype).createImage();
	private Image img_defenum =  ImageDescriptor.createFromURL(url_defenum).createImage();
	private Image img_function = ImageDescriptor.createFromURL(url_function).createImage();
	
	@Override 
	public Image getImage(Object element) {
		
		
		if (element instanceof Segment) {
			
			String name = ((Segment) element).name;

			if (name.contains("namespace"))
				return img_namespace;

			else if (name.contains("defproc"))
				return img_defproc;

			else if (name.contains("defchan"))
				return img_defchan;

			else if (name.contains("defcell"))
				return img_defcell;

			else if (name.contains("deftype"))
				return img_deftype;

			else if (name.contains("defenum"))
				return img_defenum;

			else if (name.contains("function"))
				return img_function;
			
		}
		
		return super.getImage(element);
	}
	
	@Override 
	public void dispose() {
		img_namespace.dispose();
		img_defproc.dispose();
		img_defchan.dispose();
		img_defcell.dispose();
		img_deftype.dispose();
		img_defenum.dispose();
		img_function.dispose();
	}
	
}
