package logic;

import java.awt.image.BufferedImage;

public class GrayScale {

    private static final int COLOR_MASK = 0xFF;

    public static final GrayScaleFunction AVERAGE = (red, green, blue) -> (red + green + blue) / 3;

    public static final GrayScaleFunction LUMINOSITY = (red, green,
            blue) -> (int) (0.21 * red + 0.72 * green + 0.07 * blue);

    public static final GrayScaleFunction LIGHTNESS = (red, green,
            blue) -> (Math.max(red, Math.max(green, blue)) + Math.min(red, Math.min(green, blue))) / 2;

    public static final GrayScaleFunction RED_CHANNEL_ONLY = (red, green, blue) -> red;

    public static final GrayScaleFunction GREEN_CHANNEL_ONLY = (red, green, blue) -> green;

    public static final GrayScaleFunction BLUE_CHANNEL_ONLY = (red, green, blue) -> blue;

    public static BufferedImage applyGrayScale(BufferedImage image, GrayScaleFunction function) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgb = image.getRGB(j, i);
                int red = (rgb >> 16) & COLOR_MASK;
                int green = (rgb >> 8) & COLOR_MASK;
                int blue = (rgb) & COLOR_MASK;
                int gray = function.apply(red, green, blue);
                int pixel = (gray << 16) | (gray << 8) | gray;
                grayImage.setRGB(j, i, pixel);
            }
        }
        return grayImage;
    }
}