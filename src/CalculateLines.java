import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import logic.EdgeDetection;
import logic.GaussianFilter;
import logic.GrayScale;
import logic.HoughLineTransform;

public class CalculateLines {

	public static int DEFAULT_IMAGE_SCALEDOWN = 3;

	public static int[][] calculateLines(String inputStringPath, int scale, boolean saveImages) throws IOException {

		BufferedImage image = null;
		image = ImageIO.read(new File(inputStringPath));

		BufferedImage scaledImage = new BufferedImage((image.getWidth() / DEFAULT_IMAGE_SCALEDOWN),
				(image.getHeight() / DEFAULT_IMAGE_SCALEDOWN),
				BufferedImage.TYPE_INT_ARGB);
		final AffineTransform at = AffineTransform.getScaleInstance((double) 1 / DEFAULT_IMAGE_SCALEDOWN,
				(double) 1 / DEFAULT_IMAGE_SCALEDOWN);
		final AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
		scaledImage = ato.filter(image, scaledImage);

		BufferedImage grayImage = GrayScale.applyGrayScale(scaledImage, GrayScale.AVERAGE, saveImages);

		BufferedImage blurredImage = GaussianFilter.applyGaussianFilter(grayImage, GaussianFilter.KERNEL3x3,
				saveImages);

		BufferedImage thresholdGradientImage = EdgeDetection.getThresholdGradient(blurredImage, EdgeDetection.SOBEL,
				EdgeDetection.PRESET_ALPHA, EdgeDetection.PRESET_BETA, saveImages);
		return HoughLineTransform.getLines(thresholdGradientImage, saveImages);
	}
}
