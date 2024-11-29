package logic;

import java.awt.image.BufferedImage;

public class GaussianFilter {

    private static final int GRAY_MASK = 0xFF;
    public static final int[][] KERNEL3x3 = {
            { 1, 2, 1 },
            { 2, 4, 2 },
            { 1, 2, 1 }
    };
    public static final int[][] KERNEL5x5 = {
            { 1, 4, 7, 4, 1 },
            { 4, 16, 26, 16, 4 },
            { 7, 26, 41, 26, 7 },
            { 4, 16, 26, 16, 4 },
            { 1, 4, 7, 4, 1 }
    };
    public static final int[][] KERNEL7x7 = {
            { 0, 0, 1, 2, 1, 0, 0 },
            { 0, 3, 13, 22, 13, 3, 0 },
            { 1, 13, 59, 97, 59, 13, 1 },
            { 2, 22, 97, 159, 97, 22, 2 },
            { 1, 13, 59, 97, 59, 13, 1 },
            { 0, 3, 13, 22, 13, 3, 0 },
            { 0, 0, 1, 2, 1, 0, 0 },
    };

    public static BufferedImage applyGaussianFilter(BufferedImage image, int[][] kernel) {
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
        SaveImage.saveImage(blurredImage, "blurred");
        return blurredImage;
    }
}
