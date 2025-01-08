package imageLineExtractor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SaveImage {

    /**
     * Saves a {@code BufferedImage} to the specified file if saving is enabled.
     *
     * This method writes the provided image to a file in PNG format, storing it in
     * the "output" directory with the specified file name. If the {@code saveImage}
     * flag is set to {@code false}, the method will exit without saving the image.
     *
     * @param image     The {@code BufferedImage} to be saved.
     * @param fileName  A {@code String} representing the name of the file
     *                  (excluding the extension) under which the image will be
     *                  saved.
     * @param saveImage A {@code boolean} flag that determines whether the image
     *                  should be saved. If {@code false}, the method performs no
     *                  action.
     */
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
