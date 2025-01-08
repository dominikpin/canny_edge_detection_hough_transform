package imageLineExtractor;

import java.awt.image.BufferedImage;

public class ErosionDilation {

    private static final int COLOR_BLACK = 0xFF000000;
    private static final int COLOR_WHITE = 0xFFFFFFFF;

    /**
     * Enhances relevant features from a thresholded gradient {@code BufferedImage}.
     * 
     * This method applies morphological operations, such as erosion and dilation,
     * to a thresholded gradient image. These operations help refine and enhance
     * the edges or significant features, making them more prominent for further
     * analysis or processing. The result is a binary image where features have been
     * enhanced through morphological transformations.
     *
     * @param image     The {@code BufferedImage} to be processed.
     * @param saveImage A {@code boolean} flag that determines whether the processed
     *                  image should be saved.
     * @return a binary {@code BufferedImage} that has been eroded and dilated to
     *         enhance relevant features.
     */
    public static BufferedImage erosionAndDilation(BufferedImage image, boolean saveImage) {
        int height = image.getHeight();
        int width = image.getWidth();
        BufferedImage eroded = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 1; i < height - 1; i++) {
            outer: for (int j = 1; j < width - 1; j++) {
                if (image.getRGB(j, i) == COLOR_BLACK) {
                    continue;
                }
                for (int offset1 = -1; offset1 <= 1; offset1++) {
                    for (int offset2 = -1; offset2 <= 1; offset2++) {
                        if (image.getRGB(j + offset2, i + offset1) == COLOR_BLACK) {
                            continue outer;
                        }
                    }
                }
                eroded.setRGB(j, i, COLOR_WHITE);
            }
        }
        SaveImage.saveImage(eroded, "eroded", saveImage);
        BufferedImage dilate = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 1; i < height - 1; i++) {
            outer: for (int j = 1; j < width - 1; j++) {
                for (int offset1 = -1; offset1 <= 1; offset1++) {
                    for (int offset2 = -1; offset2 <= 1; offset2++) {
                        if (eroded.getRGB(j + offset2, i + offset1) == COLOR_WHITE) {
                            dilate.setRGB(j, i, COLOR_WHITE);
                            continue outer;
                        }
                    }
                }
            }
        }
        SaveImage.saveImage(dilate, "dilate", saveImage);
        return dilate;
    }
}
