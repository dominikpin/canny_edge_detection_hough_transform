import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import logic.GaussianFilter;
import logic.CannyEdge;
import logic.GrayScale;
import logic.HoughLineTransform;

public class App {

    private static final int imageScale = 1;

    public static void main(String[] args) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("input.png"));
        } catch (IOException e) {
        }

        BufferedImage scaledImage = new BufferedImage((image.getWidth() / imageScale), (image.getHeight() / imageScale),
                BufferedImage.TYPE_INT_ARGB);
        final AffineTransform at = AffineTransform.getScaleInstance((double) 1 / imageScale, (double) 1 / imageScale);
        final AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
        scaledImage = ato.filter(image, scaledImage);

        BufferedImage grayImage = GrayScale.applyGrayScale(scaledImage, GrayScale.AVERAGE);

        BufferedImage blurredImage = GaussianFilter.applyGaussianFilter(grayImage, GaussianFilter.KERNEL5x5);

        BufferedImage cannyEdge = CannyEdge.getCannyEdge(blurredImage, CannyEdge.SOBEL, CannyEdge.PRESET_ALPHA,
                CannyEdge.PRESET_BETA);

        HoughLineTransform.getLines(cannyEdge);
    }
}
