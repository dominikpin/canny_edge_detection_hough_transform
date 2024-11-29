package logic;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class HoughLineTransform {

    public static void getLines(BufferedImage image) {
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
        ArrayList<int[]> sortingArray = new ArrayList<>();
        for (int i = 0; i < coordinateSystemArray.length; i++) {
            for (int j = 0; j < coordinateSystemArray[i].length; j++) {
                if (coordinateSystemArray[i][j] > 0) {
                    sortingArray.add(new int[] { coordinateSystemArray[i][j], i, j });
                }
            }
        }
        sortingArray.sort(Comparator.comparingInt(arr -> arr[0]));
        for (int i = 1; i < 100; i++) {
            int[] values = sortingArray.get(sortingArray.size() - i);
            double rho = (double) values[2] / 100;
            int r = values[1];
            double m = -Math.cos(rho) / Math.sin(rho);
            double c = r / Math.sin(rho);
            System.out.printf("%d %f %d %f %f\n", values[0], rho, r, m, c);
        }

        SaveImage.saveImage(graphImage, "graph");
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
            int newColor = (image.getRGB(theta, image.getHeight() - 1 - r) & 0xFF) + 3;
            if (newColor > 255) {
                newColor = 255;
            }
            int newColorPixel = (newColor << 16) | (newColor << 8) | newColor;
            image.setRGB(theta, image.getHeight() - 1 - r, newColorPixel);
            coordinateSystemArray[r][theta]++;
        }
    }
}
