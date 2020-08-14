package acteditor.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ACTColors {
	
	// hashmap to hold the colors of our text
	protected Map<RGB, Color> fColorTable = new HashMap<>(10);

	// Dispose of the elements after use
	public void dispose() {
		fColorTable.values().forEach(Color::dispose);
	}
	
	// Retrieve a color from the map, or create one of there is none already
	public Color getColor(RGB rgb) {
		Color color = fColorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
}
