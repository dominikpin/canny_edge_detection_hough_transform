package logic;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class CannyEdge {

    private static final int GRAY_MASK = 0xFF;
    private static final int COLOR_WHITE = 0xFFFFFF;
    private static final int COLOR_BLACK = 0;

    public static final int[][] SOBEL = {
            { -1, 0, 1 },
            { -2, 0, 2 },
            { -1, 0, 1 }
    };
    public static final int[][] SCHARR = {
            { -3, 0, 3 },
            { -10, 0, 10 },
            { -10, 0, 10 }
    };
    public static final double PRESET_ALPHA = 0.3;
    public static final double PRESET_BETA = 0.5;

    public static BufferedImage getCannyEdge(BufferedImage image, int[][] filter, double alpha, double beta) {
        int width = image.getWidth();
        int height = image.getHeight();
        double[][] angleTable = new double[height][width];
        AtomicInteger maxGradient = new AtomicInteger(-1);
        BufferedImage gradientImage = calculateGradientAndAngle(image, filter, angleTable, maxGradient);
        BufferedImage cannyEdge = makeCannyEdge(gradientImage, angleTable);

        hysteresisThreshold(cannyEdge, maxGradient, alpha, beta);
        return cannyEdge;
    }

    public static BufferedImage calculateGradientAndAngle(BufferedImage image, int[][] filter, double[][] angleTable,
            AtomicInteger maxGradient) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage gradientImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                int Gx = 0;
                for (int y = 0; y < filter.length; y++) {
                    for (int x = 0; x < filter.length; x++) {
                        Gx += filter[y][x] * (image.getRGB(j + y - 1, i + x - 1) & GRAY_MASK);
                    }
                }
                int Gy = 0;
                for (int y = 0; y < filter.length; y++) {
                    for (int x = 0; x < filter.length; x++) {
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
                double angle = Math.atan2(Gy, Gx) * 180 / Math.PI;
                if (angle < 0) {
                    angle += 180;
                }
                angleTable[i][j] = angle;
            }
        }

        SaveImage.saveImage(gradientImage, "gradient");
        return gradientImage;
    }

    public static BufferedImage makeCannyEdge(BufferedImage image, double[][] angleTable) {

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
        SaveImage.saveImage(cannyEdge, "canny_edge");
        return cannyEdge;
    }

    public static void hysteresisThreshold(BufferedImage image, AtomicInteger maxGradient, double alpha, double beta) {
        int width = image.getWidth();
        int height = image.getHeight();
        int maxThreshold = (int) Math.round(alpha * maxGradient.get());
        int minThreshold = (int) Math.round(beta * maxThreshold);
        ArrayList<int[]> weakPixels = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int amplitude = image.getRGB(j, i) & GRAY_MASK;
                if (amplitude <= minThreshold) {
                    image.setRGB(j, i, COLOR_BLACK);
                } else if (amplitude >= maxThreshold) {
                    image.setRGB(j, i, COLOR_WHITE);
                } else {
                    weakPixels.add(new int[] { j, i });
                }
            }
        }

        for (int[] weakPixel : weakPixels) {
            int a = -1;
            int b = 1;
            int c = -1;
            int d = 1;
            int y = weakPixel[0];
            int x = weakPixel[1];
            if (y == 0) {
                a = 0;
            }
            if (y == height) {
                b = 0;
            }
            if (x == 0) {
                c = 0;
            }
            if (x == width) {
                d = 0;
            }
            outer: for (int i = a; i < b; i++) {
                for (int j = c; j < d; j++) {
                    if (i == 0 && j == 0) {
                        continue;
                    }
                    if (image.getRGB(y + i, x + j) == 255) {
                        image.setRGB(y, x, COLOR_WHITE);
                        break outer;
                    }
                }
                image.setRGB(y, x, COLOR_BLACK);
            }
        }
        SaveImage.saveImage(image, "hysteresis");
    }
}
