
package net.imglib2.labkit.actions;

import io.scif.img.ImgSaver;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.ImgView;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.labkit.Extensible;
import net.imglib2.labkit.MenuBar;
import net.imglib2.labkit.models.Holder;
import net.imglib2.labkit.models.SegmentationItem;
import net.imglib2.labkit.utils.LabkitUtils;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.NumericType;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Matthias Arzt
 */
public class SegmentationExportAction extends AbstractFileIoAction {

	private final Extensible extensible;

	public SegmentationExportAction(Extensible extensible,
		Holder<SegmentationItem> selectedSegmenter)
	{
		super(extensible, AbstractFileIoAction.TIFF_FILTER);
		this.extensible = extensible;
		addMenuItems(selectedSegmenter, item -> item.results().segmentation(),
			"Segmentation Result");
		addMenuItems(selectedSegmenter, item -> item.results().prediction(),
			"Probability Map");
	}

	private <T extends NumericType<T> & NativeType<T>> void addMenuItems(
		Holder<SegmentationItem> selectedSegmenter,
		Function<SegmentationItem, RandomAccessibleInterval<T>> predictionFactory,
		String title)
	{
		Supplier<RandomAccessibleInterval<T>> selectedResult =
			() -> predictionFactory.apply(selectedSegmenter.get());
		initSaveAction(MenuBar.SEGMENTER_MENU, "Save " + title + " ...", 200,
			getSaveAction(selectedResult), "");
		extensible.addMenuItem(MenuBar.SEGMENTER_MENU, "Show " + title +
			" in ImageJ", 201, ignore -> getShowAction(selectedResult).run(), null,
			"");
		extensible.addMenuItem(SegmentationItem.SEGMENTER_MENU, "Save " + title +
			" ...", 202, item -> openDialogAndThen("Save " + title + " ...",
				JFileChooser.OPEN_DIALOG, getSaveAction(() -> predictionFactory.apply(
					item))), null, null);
		extensible.addMenuItem(SegmentationItem.SEGMENTER_MENU, "Show " + title +
			" in ImageJ", 203, item -> getShowAction(() -> predictionFactory.apply(
				item)).run(), null, null);
	}

	private <T extends NumericType<T> & NativeType<T>> Runnable getShowAction(
		Supplier<RandomAccessibleInterval<T>> supplier)
	{
		return () -> {
			ExecutorService executer = Executors.newSingleThreadExecutor();
			executer.submit(() -> {
				ImageJFunctions.show(LabkitUtils.populateCachedImg(supplier.get(),
					extensible.progressConsumer()));
			});
		};
	}

	private <T extends Type<T>> Action getSaveAction(
		Supplier<RandomAccessibleInterval<T>> supplier)
	{
		return filename -> {
			ImgSaver saver = new ImgSaver();
			saver.saveImg(filename, ImgView.wrap(supplier.get(), null));
		};
	}

}