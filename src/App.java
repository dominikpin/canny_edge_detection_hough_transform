import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import logic.GaussianFilter;
import logic.EdgeDetection;
import logic.GrayScale;
import logic.HoughLineTransform;

public class App {

    private static final int imageScale = 1;

    public static void main(String[] args) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("input1.png"));
        } catch (IOException e) {
        }

        BufferedImage scaledImage = new BufferedImage((image.getWidth() / imageScale), (image.getHeight() / imageScale),
                BufferedImage.TYPE_INT_ARGB);
        final AffineTransform at = AffineTransform.getScaleInstance((double) 1 / imageScale, (double) 1 / imageScale);
        final AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
        scaledImage = ato.filter(image, scaledImage);

        BufferedImage grayImage = GrayScale.applyGrayScale(scaledImage, GrayScale.AVERAGE);

        BufferedImage blurredImage = GaussianFilter.applyGaussianFilter(grayImage, GaussianFilter.KERNEL3x3);

        BufferedImage thresholdGradientImage = EdgeDetection.getThresholdGradient(blurredImage, EdgeDetection.SOBEL,
                EdgeDetection.PRESET_ALPHA, EdgeDetection.PRESET_BETA);

        for (int[] coordinates : HoughLineTransform.getLines(thresholdGradientImage)) {
            for (int coordinate : coordinates) {
                System.out.print(coordinate + " ");
            }
            System.out.println();
        }
    }
}
