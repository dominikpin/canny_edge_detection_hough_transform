package imageLineExtractor;

import java.awt.image.BufferedImage;

public class ErosionDilation {

    private static final int COLOR_BLACK = 0xFF000000;
    private static final int COLOR_WHITE = 0xFFFFFFFF;

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
