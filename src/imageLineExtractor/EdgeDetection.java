package imageLineExtractor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class EdgeDetection {

    private static final int GRAY_MASK = 0xFF;
    private static final int COLOR_WHITE = 0xFFFFFF;
    private static final int COLOR_BLACK = 0;

    /**
     * The Sobel operator kernel for edge detection.
     *
     * This 3x3 integer matrix represents the Sobel kernel used for image edge
     * detection. It emphasizes horizontal or vertical edges by applying convolution
     * to an image.
     */
    public static final int[][] SOBEL = {
            { -1, 0, 1 },
            { -2, 0, 2 },
            { -1, 0, 1 }
    };

    /**
     * The Scharr operator kernel for edge detection.
     *
     * This 3x3 integer matrix represents the Scharr kernel, an alternative to the
     * Sobel operator. It is designed to provide better gradient approximation,
     * particularly in images with higher noise or smaller features.
     */
    public static final int[][] SCHARR = {
            { -3, 0, 3 },
            { -10, 0, 10 },
            { -3, 0, 3 }
    };

    /**
     * The default upper threshold value for thresholding operations.
     *
     * This constant defines the preset upper threshold ({@code 0.3}) used in image
     * processing tasks. Pixels with values above this threshold are typically
     * classified as part of an edge or significant feature.
     */
    public static final double PRESET_ALPHA = 0.3;

    /**
     * The default lower threshold value for thresholding operations.
     *
     * This constant defines the preset lower threshold ({@code 0.5}) used in image
     * processing tasks. Pixels with values below this threshold are typically
     * ignored or classified as background noise.
     */
    public static final double PRESET_BETA = 0.5;

    /**
     * Converts a grayscaled {@code BufferedImage} to a thresholded gradient image.
     * 
     * This method computes the gradient of a grayscaled image using the specified
     * filter and applies thresholding based on the provided alpha (upper threshold)
     * and beta (lower threshold) values. Additionally, the method excludes very
     * bright pixels (which correspond to symbols) to focus on relevant features.
     * The result is a binary image highlighting edges or significant features based
     * on the gradient and thresholding criteria.
     *
     * @param image     The {@code BufferedImage} to be processed.
     * @param filter    The {@code int[][]} filter to be used for gradient
     *                  calculation.
     * @param alpha     The upper threshold value for the gradient. Pixels with a
     *                  gradient value above this threshold are considered part of
     *                  the significant features.
     * @param beta      The lower threshold value for the gradient. Pixels with a
     *                  gradient value below this threshold are ignored or
     *                  considered as background.
     * @param saveImage A {@code boolean} flag that determines whether the processed
     *                  image should be saved.
     * @return A binary {@code BufferedImage} that has been thresholded based on the
     *         gradient.
     */
    public static BufferedImage getThresholdGradient(BufferedImage image, int[][] filter, double alpha, double beta,
            boolean saveImage) {
        AtomicInteger maxGradient = new AtomicInteger(-1);
        BufferedImage gradientImage = calculateGradientAndAngle(image, filter, null, maxGradient, saveImage);
        BufferedImage thresholdGradient = thresholdGradient(gradientImage, maxGradient, alpha, beta, saveImage);
        return thresholdGradient;
    }

    private static BufferedImage calculateGradientAndAngle(BufferedImage image, int[][] filter, double[][] angleTable,
            AtomicInteger maxGradient, boolean saveImage) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage gradientImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 1; i < height - 1; i++) {
            outer: for (int j = 1; j < width - 1; j++) {
                int Gx = 0;
                int Gy = 0;
                for (int y = 0; y < filter.length; y++) {
                    for (int x = 0; x < filter.length; x++) {
                        if ((image.getRGB(j + y - 1, i + x - 1) & GRAY_MASK) > 100) {
                            gradientImage.setRGB(j, i, 0);
                            if (angleTable != null) {
                                angleTable[i][j] = 0;
                            }
                            continue outer;
                        }
                        Gx += filter[y][x] * (image.getRGB(j + y - 1, i + x - 1) & GRAY_MASK);
                        Gy += filter[x][y] * (image.getRGB(j + y - 1, i + x - 1) & GRAY_MASK);
                    }
                }
                double G = Math.sqrt(Math.pow(Gx, 2) + Math.pow(Gy, 2));
                int roundedG = (int) Math.round(G);
                if (roundedG > maxGradient.get()) {
                    maxGradient.set(roundedG);
                }
                int gradientRgb = roundedG << 16 | roundedG << 8 | roundedG;
                gradientImage.setRGB(j, i, gradientRgb);
                if (angleTable == null) {
                    continue;
                }
                double angle = Math.atan2(Gy, Gx) * 180 / Math.PI;
                if (angle < 0) {
                    angle += 180;
                }
                angleTable[i][j] = angle;
            }
        }
        SaveImage.saveImage(gradientImage, "gradient", saveImage);
        return gradientImage;
    }

    private static BufferedImage thresholdGradient(BufferedImage image, AtomicInteger maxGradient, double alpha,
            double beta, boolean saveImage) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage thresholdGradient = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        int minThreshold = (int) Math.round(beta * alpha * maxGradient.get());
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int amplitude = image.getRGB(j, i) & GRAY_MASK;
                if (amplitude > minThreshold) {
                    thresholdGradient.setRGB(j, i, COLOR_WHITE);
                }
            }
        }
        SaveImage.saveImage(thresholdGradient, "threshold-gradient", saveImage);
        return thresholdGradient;
    }

    /**
     * Converts a grayscaled {@code BufferedImage} to a Canny edge image.
     * 
     * This method applies a gradient calculation and thresholds the image using the
     * Canny edge detection algorithm, based on the provided filter and threshold
     * values (alpha and beta). The result is a binary image highlighting the edges
     * of significant features. Note that this function is deprecated, and it is
     * recommended to use the {@link #getThresholdGradient} method for a more
     * efficient implementation.
     *
     * @param image     The {@code BufferedImage} to be processed.
     * @param filter    The {@code int[][]} filter to be used for gradient
     *                  calculation.
     * @param alpha     The upper threshold value for the gradient. Pixels with a
     *                  gradient value above this threshold are considered part of
     *                  the significant features.
     * @param beta      The lower threshold value for the gradient. Pixels with a
     *                  gradient value below this threshold are ignored or
     *                  considered as background.
     * @param saveImage A {@code boolean} flag that determines whether the processed
     *                  image should be saved.
     * @return A binary {@code BufferedImage} that has been thresholded based on the
     *         gradient.
     * @deprecated Use {@link #getThresholdGradient} instead for improved
     *             performance.
     */
    @Deprecated
    public static BufferedImage getCannyEdge(BufferedImage image, int[][] filter, double alpha, double beta,
            boolean saveImage) {
        int width = image.getWidth();
        int height = image.getHeight();
        double[][] angleTable = new double[height][width];
        AtomicInteger maxGradient = new AtomicInteger(-1);
        BufferedImage gradientImage = calculateGradientAndAngle(image, filter, angleTable, maxGradient, saveImage);
        BufferedImage cannyEdge = makeCannyEdge(gradientImage, angleTable, saveImage);
        BufferedImage hysteresisThreshold = hysteresisThreshold(cannyEdge, maxGradient, alpha, beta, saveImage);
        return hysteresisThreshold;
    }

    @Deprecated
    private static BufferedImage makeCannyEdge(BufferedImage image, double[][] angleTable, boolean saveImage) {

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage cannyEdge = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                int q = -1;
                int r = -1;

                if (0 <= angleTable[i][j] || angleTable[i][j] < 22.5 || 157.5 <= angleTable[i][j]
                        || angleTable[i][j] < 180) {
                    q = image.getRGB(j, i + 1) & GRAY_MASK;
                    r = image.getRGB(j, i - 1) & GRAY_MASK;
                } else if (22.5 <= angleTable[i][j] || angleTable[i][j] < 67.5) {
                    q = image.getRGB(j + 1, i - 1) & GRAY_MASK;
                    r = image.getRGB(j - 1, i + 1) & GRAY_MASK;
                } else if (67.5 <= angleTable[i][j] || angleTable[i][j] < 112.5) {
                    q = image.getRGB(j + 1, i) & GRAY_MASK;
                    r = image.getRGB(j - 1, i) & GRAY_MASK;
                } else if (122.5 <= angleTable[i][j] || angleTable[i][j] < 157.5) {
                    q = image.getRGB(j - 1, i - 1) & GRAY_MASK;
                    r = image.getRGB(j + 1, i + 1) & GRAY_MASK;
                }

                if ((image.getRGB(j, i) & GRAY_MASK) >= q && (image.getRGB(j, i) & GRAY_MASK) >= r) {
                    cannyEdge.setRGB(j, i, image.getRGB(j, i));
                } else {
                    cannyEdge.setRGB(j, i, COLOR_BLACK);
                }
            }
        }
        SaveImage.saveImage(cannyEdge, "canny-edge", saveImage);
        return cannyEdge;
    }

    @Deprecated
    private static BufferedImage hysteresisThreshold(BufferedImage image, AtomicInteger maxGradient, double alpha,
            double beta, boolean saveImage) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage hysteresisThreshold = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        int maxThreshold = (int) Math.round(alpha * maxGradient.get());
        int minThreshold = (int) Math.round(beta * maxThreshold);
        ArrayList<int[]> weakPixels = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int amplitude = image.getRGB(j, i) & GRAY_MASK;
                if (amplitude <= minThreshold) {
                    hysteresisThreshold.setRGB(j, i, COLOR_BLACK);
                } else if (amplitude >= maxThreshold) {
                    hysteresisThreshold.setRGB(j, i, COLOR_WHITE);
                } else {
                    weakPixels.add(new int[] { j, i });
                }
            }
        }

        for (int[] weakPixel : weakPixels) {
            int y = weakPixel[0];
            int x = weakPixel[1];
            int a = y != 0 ? -1 : 0;
            int b = y != height ? 1 : 0;
            int c = x != 0 ? -1 : 0;
            int d = x != width ? 1 : 0;
            outer: for (int i = a; i < b; i++) {
                for (int j = c; j < d; j++) {
                    if (i == 0 && j == 0) {
                        continue;
                    }
                    if (hysteresisThreshold.getRGB(y + i, x + j) == 255) {
                        hysteresisThreshold.setRGB(y, x, COLOR_WHITE);
                        break outer;
                    }
                }
                hysteresisThreshold.setRGB(y, x, COLOR_BLACK);
            }
        }
        SaveImage.saveImage(hysteresisThreshold, "hysteresis-threshold", saveImage);
        return hysteresisThreshold;
    }
}
