
package net.imglib2.labkit;

import net.imglib2.labkit.actions.LabelEditAction;
import net.imglib2.labkit.models.ColoredLabelsModel;
import net.imglib2.labkit.models.ImageLabelingModel;
import net.imglib2.labkit.panel.ImageInfoPanel;
import net.imglib2.labkit.panel.LabelPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * LabelingComponent provides a UI for displaying an image overlaid with a
 * labeling. It features brush tools for modifying the labeling, and a sidebar
 * that shows a list of all the labels.
 */
public class LabelingComponent extends JPanel implements AutoCloseable {

	private final BasicLabelingComponent labelingComponent;

	public LabelingComponent(JFrame dialogBoxOwner, ImageLabelingModel model) {
		this.labelingComponent = new BasicLabelingComponent(dialogBoxOwner, model);
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new MigLayout("", "[grow]", "[][grow]"));
		leftPanel.add(ImageInfoPanel.newFramedImageInfoPanel(model, labelingComponent), "grow, wrap");
		DefaultExtensible extensible = new DefaultExtensible(null, dialogBoxOwner);
		new LabelEditAction(extensible, false, new ColoredLabelsModel(model));
		leftPanel.add(LabelPanel.newFramedLabelPanel(model, extensible, false),
			"grow");
		setLayout(new BorderLayout());
		add(initSplitPane(leftPanel, labelingComponent));
	}

	private JSplitPane initSplitPane(JComponent left, JComponent right) {
		JSplitPane panel = new JSplitPane();
		panel.setSize(100, 100);
		panel.setOneTouchExpandable(true);
		panel.setLeftComponent(left);
		panel.setRightComponent(right);
		return panel;
	}

	@Deprecated
	public JComponent getComponent() {
		return this;
	}

	@Override
	public void close() {
		labelingComponent.close();
	}
}
