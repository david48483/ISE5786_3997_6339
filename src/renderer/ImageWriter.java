package renderer;

import primitives.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Writes rendered pixel colors to an image file.
 * <p>
 * This class wraps a {@link BufferedImage}, supports writing individual pixels,
 * and exports the final image as a PNG file.
 *
 * @author Dan Zilberstein
 */
final class ImageWriter {
    /**
     * Output directory for generated images, relative to the working directory.
     */
    private static final String FOLDER_PATH = System.getProperty("user.dir") + "/images";

    /**
     * Internal image buffer.
     */
    private final BufferedImage _image;

    /**
     * Creates an image writer with the given pixel resolution.
     *
     * @param nX horizontal resolution in pixels
     * @param nY vertical resolution in pixels
     * @throws IllegalArgumentException if {@code nX} or {@code nY} is not positive
     */
    ImageWriter(int nX, int nY) {
        if (nX <= 0 || nY <= 0)
            throw new IllegalArgumentException("Image resolution must be positive");
        _image = new BufferedImage(nX, nY, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * Writes the buffered image to a PNG file in the output directory.
     *
     * @param fileName output file name without the {@code .png} extension
     * @throws IllegalStateException if the image cannot be written
     */
    void writeToImage(String fileName) {
        try {
            File folder = new File(FOLDER_PATH);
            if (!folder.exists() && !folder.mkdirs()) {
                throw new IllegalStateException("Could not create output directory: " + FOLDER_PATH);
            }
            File file = new File(folder, fileName + ".png");
            ImageIO.write(_image, "png", file);
        } catch (IOException e) {
            throw new IllegalStateException("I/O error while writing image to " + FOLDER_PATH, e);
        }
    }

    /**
     * Writes a color value to the specified pixel.
     *
     * @param xIndex pixel column index
     * @param yIndex pixel row index
     * @param color color to write
     */
    void writePixel(int xIndex, int yIndex, Color color) {
        _image.setRGB(xIndex, yIndex, color.getColor().getRGB());
    }

}
