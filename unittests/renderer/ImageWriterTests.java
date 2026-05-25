package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;

/**
 * Unit tests for {@link ImageWriter} class.
 * The tests verify:
 * <ul>
 * <li>{@link ImageWriter#writePixel(int, int, Color)}</li>
 * <li>{@link ImageWriter#writeToImage(String)}</li>
 * </ul>
 * Tests create a grid pattern on the image and save it to a file for visual verification
 */

public class ImageWriterTests {

    static final int WIDTH = 800;
    static final int HEIGHT = 500;
    static final int SQUARE_SIZE = 50;
    static final Color RED = new Color(255, 0, 0);
    static final Color YELLOW = new Color(255, 255, 0);

    ImageWriter image = new ImageWriter(WIDTH, HEIGHT);

    /**
     * Test for {@link ImageWriter#writePixel(int, int, Color)} and {@link ImageWriter#writeToImage(String)}.
     * <p>
     * This test creates a grid pattern on the image and saves it to a file named "testImageWriter.png".
     * The pattern consists of red grid lines on a yellow background, with cells defined by SQUARE_SIZE.
     */
    @Test
    void testImageWriter() {

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                image.writePixel(i, j, (i % SQUARE_SIZE == 0 || j % SQUARE_SIZE == 0) ? RED : YELLOW);

            }

        }
        image.writeToImage("testImageWriter");

    }

}
