package logic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SaveImage {
    public static void saveImage(BufferedImage image, String fileName, boolean saveImage) {
        if (!saveImage) {
            return;
        }
        try {
            ImageIO.write(image, "png", new File("output/" + fileName + ".png"));
        } catch (IOException e) {
        }
    }
}
