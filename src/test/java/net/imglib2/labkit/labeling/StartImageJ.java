
package net.imglib2.labkit.labeling;

import net.imagej.ImageJ;

/**
 * @author Matthias Arzt
 */
public class StartImageJ {

	public static void main(String... args) {
		ImageJ imageJ = new ImageJ();
		imageJ.ui().showUI();
	}
}
