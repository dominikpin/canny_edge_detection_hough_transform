import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import imageLineExtractor.LineCalculator;
import imageLineExtractor.SaveImage;

public class Main {
    public static void main(String[] args) throws IOException {
        final long startTime = System.currentTimeMillis();
        String input = "input/input1.png";
        BufferedImage image = ImageIO.read(new File(input));
        int[][] linesCoords = LineCalculator.calculateLines(image, 1, true);
        for (int i = 0; i < 2; i++) {
            System.out.print((i == 0 ? "horizontal" : "vertical") + "lines: ");
            for (int line : linesCoords[i]) {
                for (int j = 0; j < (i == 0 ? image.getWidth() : image.getHeight()); j++) {
                    image.setRGB(i == 0 ? j : line, i == 0 ? line : j, 0xFFFF0000);
                }
                System.out.print(line + " ");
            }
            System.out.println();
        }
        SaveImage.saveImage(image, "output", true);
        System.out.println(LineCalculator.getSquareSize(linesCoords));
        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + ((float) (endTime - startTime)) / 1000);
    }
}
