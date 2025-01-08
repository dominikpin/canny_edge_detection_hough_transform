package imageLineExtractor;

import java.awt.image.BufferedImage;

public class GaussianFilter {

    private static final int GRAY_MASK = 0xFF;

    /**
     * A 3x3 kernel for basic image smoothing or blurring.
     * Provides a mild smoothing effect.
     */
    public static final int[][] KERNEL3x3 = {
            { 1, 2, 1 },
            { 2, 4, 2 },
            { 1, 2, 1 }
    };

    /**
     * A 5x5 kernel for moderate image smoothing or blurring.
     * Offers stronger smoothing compared to the 3x3 kernel.
     */
    public static final int[][] KERNEL5x5 = {
            { 1, 4, 7, 4, 1 },
            { 4, 16, 26, 16, 4 },
            { 7, 26, 41, 26, 7 },
            { 4, 16, 26, 16, 4 },
            { 1, 4, 7, 4, 1 }
    };

    /**
     * A 7x7 kernel for aggressive image smoothing or blurring.
     * Ideal for reducing significant noise or softening details.
     */
    public static final int[][] KERNEL7x7 = {
            { 0, 0, 1, 2, 1, 0, 0 },
            { 0, 3, 13, 22, 13, 3, 0 },
            { 1, 13, 59, 97, 59, 13, 1 },
            { 2, 22, 97, 159, 97, 22, 2 },
            { 1, 13, 59, 97, 59, 13, 1 },
            { 0, 3, 13, 22, 13, 3, 0 },
            { 0, 0, 1, 2, 1, 0, 0 }
    };

    /**
     * Applies a Gaussian filter to a {@code BufferedImage} for image smoothing or
     * blurring.
     * 
     * This method performs a convolution operation on the input image using the
     * provided Gaussian kernel, which smooths or blurs the image by averaging pixel
     * values with respect to their neighbors. The result is a softened version of
     * the image, useful for noise reduction or preparing the image for further
     * processing.
     *
     * @param image     The {@code BufferedImage} to be processed.
     * @param kernel    The {@code int[][]} kernel to be used for the Gaussian
     *                  filter operation, typically a 3x3, 5x5, or larger kernel.
     * @param saveImage A {@code boolean} flag that determines whether the processed
     *                  image should be saved.
     * @return the processed {@code BufferedImage} after applying the Gaussian
     *         filter.
     */
    public static BufferedImage applyGaussianFilter(BufferedImage image, int[][] kernel, boolean saveImage) {
        int width = image.getWidth();
        int height = image.getHeight();
        int offset = kernel.length / 2;
        int kernelSum = 0;
        for (int i = 0; i < kernel.length; i++) {
            for (int j = 0; j < kernel.length; j++) {
                kernelSum += kernel[i][j];
            }
        }
        BufferedImage blurredImage = new BufferedImage(image.getWidth() - 2 * offset, image.getHeight() - 2 * offset,
                BufferedImage.TYPE_BYTE_GRAY);
        for (int i = offset; i < height - offset; i++) {
            for (int j = offset; j < width - offset; j++) {
                int gaussianSum = 0;
                for (int y = -offset; y <= offset; y++) {
                    for (int x = -offset; x <= offset; x++) {
                        int pixelValue = image.getRGB(j + x, i + y) & GRAY_MASK;
                        gaussianSum += pixelValue * kernel[y + offset][x + offset];
                    }
                }
                int gaussianValue = gaussianSum / kernelSum;
                int gaussianPixel = (gaussianValue << 16) | (gaussianValue << 8) | gaussianValue;
                blurredImage.setRGB(j - offset, i - offset, gaussianPixel);
            }
        }
        SaveImage.saveImage(blurredImage, "blurred", saveImage);
        return blurredImage;
    }
}
