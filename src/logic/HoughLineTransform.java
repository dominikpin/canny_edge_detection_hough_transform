package logic;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class HoughLineTransform {

    public static int[][] getLines(BufferedImage image, boolean saveImage) {
        int width = image.getWidth();
        int height = image.getHeight();
        AtomicInteger atom = new AtomicInteger(-1);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if ((image.getRGB(j, i) & 0xFF) == 255) {
                    findHighestPoint(i, j, atom);
                }
            }
        }
        int[][] coordinateSystemArray = new int[atom.get() + 1][(int) (200 * Math.PI) + 2];
        BufferedImage graphImage = new BufferedImage(coordinateSystemArray[0].length, coordinateSystemArray.length,
                BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if ((image.getRGB(j, i) & 0xFF) == 255) {
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
        BufferedImage houghLineTransformImage = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                houghLineTransformImage.setRGB(j, i, image.getRGB(j, i));
            }
        }
        ArrayList<Integer> verticalLines = new ArrayList<>();
        ArrayList<Integer> horizontalLines = new ArrayList<>();
        sortingArray.sort((arr1, arr2) -> Integer.compare(arr2[0], arr1[0]));
        for (int[] values : sortingArray) {
            if (values[0] < sortingArray.get(0)[0] / 3) {
                break;
            }
            double rho = (double) values[2] / 100;
            int r = values[1];
            double m = -Math.cos(rho) / Math.sin(rho);
            double c = r / Math.sin(rho);
            if (rho == 0 || m < 0.01 && m > -0.01) {
                boolean isTooClose = false;
                for (int verticalOrHorizontal : rho == 0 ? verticalLines : horizontalLines) {
                    if (verticalOrHorizontal - 20 < r && verticalOrHorizontal + 20 > r) {
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
                for (int j = 0; j < (rho == 0 ? houghLineTransformImage.getHeight()
                        : houghLineTransformImage.getWidth()); j++) {
                    if (rho == 0) {
                        houghLineTransformImage.setRGB(r, j, 0xFF0000);
                    } else {
                        int y = (int) (c + m * j);
                        if (y >= houghLineTransformImage.getHeight() || y < 0) {
                            continue;
                        }
                        houghLineTransformImage.setRGB(j, y, 0xFF0000);
                    }
                }
            }
        }
        SaveImage.saveImage(houghLineTransformImage, "hough-line-transform", saveImage);
        horizontalLines.sort((a, b) -> Integer.compare(a, b));
        verticalLines.sort((a, b) -> Integer.compare(a, b));
        return new int[][] { horizontalLines.stream().mapToInt(Integer::intValue).toArray(),
                verticalLines.stream().mapToInt(Integer::intValue).toArray() };
    }

    private static void findHighestPoint(int x, int y, AtomicInteger atom) {
        for (double theta = 0; theta < 200 * Math.PI; theta++) {
            long r = Math.round(x * Math.cos((double) theta / 100) + y * Math.sin((double) theta / 100));
            if (r > atom.get()) {
                atom.set((int) r);
            }
        }
    }

    private static void plotOnArray(int x, int y, int[][] coordinateSystemArray, BufferedImage image) {
        for (int theta = 0; theta < 200 * Math.PI; theta++) {
            int r = (int) Math.round(x * Math.cos((double) theta / 100) + y * Math.sin((double) theta / 100));
            if (r < 0) {
                continue;
            }
            int newColor = (image.getRGB(theta, image.getHeight() - 1 - r) & 0xFF) + 1;
            if (newColor > 255) {
                newColor = 255;
            }
            int newColorPixel = (newColor << 16) | (newColor << 8) | newColor;
            image.setRGB(theta, image.getHeight() - 1 - r, newColorPixel);
            coordinateSystemArray[r][theta]++;
        }
    }
}
