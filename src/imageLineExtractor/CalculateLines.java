package imageLineExtractor;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CalculateLines {

	/**
	 * The default scale factor for image processing.
	 *
	 * This constant defines the default value for scaling down an image during
	 * processing. A value of {@code 1} indicates no scaling, meaning the image will
	 * be processed at its original size. This is used to maintain consistent
	 * performance and accuracy in image analysis tasks.
	 */
	public static final int DEFAULT_IMAGE_SCALEDOWN = 1;

	/**
	 * Extracts horizontal and vertical lines from a {@code BufferedImage}.
	 *
	 * This method processes an image to detect and extract the coordinates
	 * of horizontal and vertical lines. The operation can be scaled down for
	 * performance optimization and optionally saves intermediate step images.
	 *
	 * @param pathname   The file path to the input image as a {@code String}.
	 * @param scale      A {@code int} value specifying the scale factor for
	 *                   reducing the image size during processing. The scale must
	 *                   be 1 or greater.
	 * @param saveImages A {@code boolean} flag indicating whether intermediate
	 *                   images generated during processing should be saved.
	 * @return a 2D {@code int} array where:
	 *         - The first sub-array contains the coordinates of horizontal lines.
	 *         - The second sub-array contains the coordinates of vertical lines.
	 * @throws IOException If an I/O error occurs while reading the image.
	 */

	public static int[][] calculateLines(String pathname, int scale, boolean saveImages) throws IOException {

		BufferedImage image = ImageIO.read(new File(pathname));

		BufferedImage scaledImage = new BufferedImage((image.getWidth() / scale), (image.getHeight() / scale),
				BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = AffineTransform.getScaleInstance((double) 1 / scale, (double) 1 / scale);
		AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
		scaledImage = ato.filter(image, scaledImage);

		BufferedImage grayImage = GrayScale.applyGrayScale(scaledImage, GrayScale.AVERAGE, saveImages);

		BufferedImage blurredImage = GaussianFilter.applyGaussianFilter(grayImage, GaussianFilter.KERNEL3x3,
				saveImages);

		BufferedImage thresholdGradientImage = EdgeDetection.getThresholdGradient(blurredImage, EdgeDetection.SOBEL,
				EdgeDetection.PRESET_ALPHA, EdgeDetection.PRESET_BETA, saveImages);

		BufferedImage eroded = ErosionDilation.erosionAndDilation(thresholdGradientImage, saveImages);

		int[][] linesCoords = HoughLineTransform.getLines(eroded, scale, saveImages);
		for (int[] lines : linesCoords) {
			for (int i = 0; i < lines.length; i++) {
				lines[i] *= scale;
			}
		}
		return linesCoords;
	}
}
