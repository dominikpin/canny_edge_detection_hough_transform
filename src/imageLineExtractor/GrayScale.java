package imageLineExtractor;

import java.awt.image.BufferedImage;

public class GrayScale {

    private static final int COLOR_MASK = 0xFF;

    /**
     * A grayscale conversion function that computes the average of the red, green,
     * and blue color channels.
     * 
     * This method provides a basic grayscale representation where all channels
     * contribute equally to the final intensity.
     */
    public static final GrayScaleFunction AVERAGE = (red, green, blue) -> (red + green + blue) / 3;

    /**
     * A grayscale conversion function that computes the luminosity of a color
     * using a weighted sum of the red, green, and blue channels.
     * 
     * The weights (0.21 for red, 0.72 for green, and 0.07 for blue) approximate
     * human perception, giving more prominence to the green channel and less
     * to blue and red. This method produces a more visually accurate grayscale
     * image.
     */
    public static final GrayScaleFunction LUMINOSITY = (red, green,
            blue) -> (int) (0.21 * red + 0.72 * green + 0.07 * blue);

    /**
     * A grayscale conversion function that calculates the lightness by averaging
     * the maximum and minimum intensity values of the red, green, and blue
     * channels.
     * 
     * This method emphasizes the extremes of the color channels, resulting in
     * a balanced grayscale representation that reflects the lightest and darkest
     * components.
     */
    public static final GrayScaleFunction LIGHTNESS = (red, green,
            blue) -> (Math.max(red, Math.max(green, blue)) + Math.min(red, Math.min(green, blue))) / 2;

    /**
     * A grayscale conversion function that uses only the red channel for the
     * grayscale intensity.
     * 
     * This method effectively discards the green and blue channels, producing
     * a grayscale image that reflects the intensity of the red channel alone.
     */
    public static final GrayScaleFunction RED_CHANNEL_ONLY = (red, green, blue) -> red;

    /**
     * A grayscale conversion function that uses only the green channel for the
     * grayscale intensity.
     * 
     * This method effectively discards the red and blue channels, producing
     * a grayscale image that reflects the intensity of the green channel alone.
     */
    public static final GrayScaleFunction GREEN_CHANNEL_ONLY = (red, green, blue) -> green;

    /**
     * A grayscale conversion function that uses only the blue channel for the
     * grayscale intensity.
     * 
     * This method effectively discards the red and green channels, producing
     * a grayscale image that reflects the intensity of the blue channel alone.
     */
    public static final GrayScaleFunction BLUE_CHANNEL_ONLY = (red, green, blue) -> blue;

    /**
     * Applies a grayscale conversion to a {@code BufferedImage} using a specified
     * grayscale function.
     * 
     * This method processes each pixel in the input image, converting it to
     * grayscale based on the provided {@code GrayScaleFunction}. The grayscale
     * function determines how the red, green, and blue channels are combined to
     * produce the final intensity value for each pixel.
     * 
     * @param image     The {@code BufferedImage} to be processed.
     * @param function  The {@code GrayScaleFunction} that defines the grayscale
     *                  conversion logic.
     * @param saveImage A {@code boolean} flag that determines whether the processed
     *                  image should be saved.
     * @return A {@code BufferedImage} where all pixels have been converted to
     *         grayscale.
     */
    public static BufferedImage applyGrayScale(BufferedImage image, GrayScaleFunction function, boolean saveImage) {
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
        SaveImage.saveImage(grayImage, "grayscale", saveImage);
        return grayImage;
    }
}