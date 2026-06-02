package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;

/**
 * Unit tests for {@link ImageWriter}.
 * Tests grid drawing by writing pixels and exporting the result to an image file.
 *
 * @author David &amp; Yehuda
 */
public class ImageWriterTests {

    /**
     * Default constructor to satisfy JavaDoc generation tools.
     */
    public ImageWriterTests() {
    }

    /**
     * Image width in pixels.
     */
    static final int WIDTH = 800;

    /**
     * Image height in pixels.
     */
    static final int HEIGHT = 500;

    /**
     * Grid cell size in pixels.
     */
    static final int SQUARE_SIZE = 50;

    /**
     * Grid line color.
     */
    static final Color RED = new Color(255, 0, 0);

    /**
     * Grid background color.
     */
    static final Color YELLOW = new Color(255, 255, 0);

    /**
     * Image writer used by the test.
     */
    ImageWriter image = new ImageWriter(WIDTH, HEIGHT);

    /**
     * Tests {@link ImageWriter#writePixel(int, int, Color)} and
     * {@link ImageWriter#writeToImage(String)}.
     * Creates a red grid on a yellow background and saves the image as
     * {@code testImageWriter.png}.
     */
    @Test
    void testImageWriter() {

        for (int j = 0; j < HEIGHT; j++) {
            for (int i = 0; i < WIDTH; i++) {
                image.writePixel(i, j, (i % SQUARE_SIZE == 0 || j % SQUARE_SIZE == 0) ? RED : YELLOW);

            }

        }
        image.writeToImage("testImageWriter");

    }

}
