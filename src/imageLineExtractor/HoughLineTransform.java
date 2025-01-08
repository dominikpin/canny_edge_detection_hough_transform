package imageLineExtractor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class HoughLineTransform {

    private static final int GREY_MASK = 0xFF;
    private static final int COLOR_RED = 0xFF0000;
    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static int upDownLeftRight = 60;

    /**
     * Extracts horizontal and vertical lines from a {@code BufferedImage}.
     *
     * This method processes an image to detect and extract the coordinates of
     * horizontal and vertical lines. It maps all white pixels onto a polar
     * coordinate system and identifies regions where lines converge. Only the most
     * relevant horizontal and vertical lines are extracted.
     * 
     * @param image     The {@code BufferedImage} to be processed.
     * @param scale     A {@code int} value specifying the scale factor for
     *                  reducing the image size during processing. The scale must
     *                  be 1 or greater.
     * @param saveImage A {@code boolean} flag that determines whether the processed
     *                  image should be saved.
     * @return a 2D {@code int} array where:
     *         - The first sub-array contains the coordinates of horizontal lines.
     *         - The second sub-array contains the coordinates of vertical lines.
     */
    public static int[][] getLines(BufferedImage image, int scale, boolean saveImage) {
        upDownLeftRight /= scale;
        ArrayList<int[]> sortingArray = makePolarCoordinatesGraph(findHighestPoint(image), image, saveImage);
        int[][] horizontalVerticalLines = findHorizontalAndVerticalLines(sortingArray, image, saveImage);
        return horizontalVerticalLines;
    }

    private static int findHighestPoint(BufferedImage image) {
        int highestPoint = -1;
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                if (image.getRGB(j, i) == COLOR_WHITE) {
                    for (double theta = 0; theta < 200 * Math.PI; theta++) {
                        long r = Math.round(i * Math.cos((double) theta / 100) + j * Math.sin((double) theta / 100));
                        if (r > highestPoint) {
                            highestPoint = (int) r;
                        }
                    }
                }
            }
        }
        return highestPoint;
    }

    private static void plotOnArray(int x, int y, int[][] coordinateSystemArray, BufferedImage image) {
        for (int theta = 0; theta < 200 * Math.PI; theta++) {
            int r = (int) Math.round(x * Math.cos((double) theta / 100) + y * Math.sin((double) theta / 100));
            if (r < 0) {
                continue;
            }
            int newColor = (image.getRGB(theta, image.getHeight() - 1 - r) & GREY_MASK) + 1;
            if (newColor > 255) {
                newColor = 255;
            }
            int newColorPixel = (newColor << 16) | (newColor << 8) | newColor;
            image.setRGB(theta, image.getHeight() - 1 - r, newColorPixel);
            coordinateSystemArray[r][theta]++;
        }
    }

    private static ArrayList<int[]> makePolarCoordinatesGraph(int highestPoint, BufferedImage image,
            boolean saveImage) {
        int[][] coordinateSystemArray = new int[highestPoint + 1][(int) (200 * Math.PI) + 2];
        BufferedImage graphImage = new BufferedImage(coordinateSystemArray[0].length, coordinateSystemArray.length,
                BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                if (image.getRGB(j, i) == COLOR_WHITE) {
                    plotOnArray(j, i, coordinateSystemArray, graphImage);
                }
            }
        }
        SaveImage.saveImage(graphImage, "graph", saveImage);
        ArrayList<int[]> sortingArray = new ArrayList<>();
        for (int i = 0; i < coordinateSystemArray.length; i++) {
            for (int j = 0; j < coordinateSystemArray[i].length; j++) {
                if (coordinateSystemArray[i][j] > 0) {
                    sortingArray.add(new int[] { coordinateSystemArray[i][j], i, j });
                }
            }
        }
        return sortingArray;
    }

    private static int[][] findHorizontalAndVerticalLines(ArrayList<int[]> sortingArray, BufferedImage image,
            boolean saveImage) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage houghLineTransformImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                houghLineTransformImage.setRGB(j, i, image.getRGB(j, i));
            }
        }
        ArrayList<Integer> verticalLines = new ArrayList<>();
        ArrayList<Integer> horizontalLines = new ArrayList<>();
        sortingArray.sort((arr1, arr2) -> Integer.compare(arr2[0], arr1[0]));
        for (int[] values : sortingArray) {
            if (values[0] < sortingArray.get(0)[0] / 8) {
                break;
            }
            double rho = (double) values[2] / 100;
            int r = values[1];
            double m = -Math.cos(rho) / Math.sin(rho);
            double c = r / Math.sin(rho);
            if (rho == 0 || m < 0.01 && m > -0.01) {
                boolean isTooClose = false;
                for (int verticalOrHorizontal : rho == 0 ? verticalLines : horizontalLines) {
                    if (verticalOrHorizontal - upDownLeftRight < r && verticalOrHorizontal + upDownLeftRight > r) {
                        isTooClose = true;
                        break;
                    }
                }
                if (isTooClose) {
                    continue;
                }
                if (rho == 0) {
                    verticalLines.add(r);
                } else {
                    horizontalLines.add((int) c);
                }
                for (int j = 0; j < (rho == 0 ? height : width); j++) {
                    if (rho == 0) {
                        houghLineTransformImage.setRGB(r, j, COLOR_RED);
                    } else {
                        int y = (int) (c + m * j);
                        if (y >= height || y < 0) {
                            continue;
                        }
                        houghLineTransformImage.setRGB(j, y, COLOR_RED);
                    }
                }
            }
        }
        horizontalLines.sort((a, b) -> Integer.compare(a, b));
        verticalLines.sort((a, b) -> Integer.compare(a, b));
        SaveImage.saveImage(houghLineTransformImage, "hough-line-transform", saveImage);
        return new int[][] { horizontalLines.stream().mapToInt(Integer::intValue).toArray(),
                verticalLines.stream().mapToInt(Integer::intValue).toArray() };
    }
}
