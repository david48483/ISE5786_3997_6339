package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;

public class ImageWriterTests {

    static final int WIDTH = 800;
    static final int HEIGHT = 500;
    static final int SQUARE_SIZE = 50;
    static final Color RED = new Color(255, 0, 0);
    static final Color YELLOW = new Color(255, 255, 0);

    ImageWriter image = new ImageWriter(WIDTH, HEIGHT);

    @Test
    void testImageWriter() {

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                image.writePixel(i, j, (i % SQUARE_SIZE == 0 || j % SQUARE_SIZE == 0) ? Color.BLACK : YELLOW);

            }

        }
        image.writeToImage("testImageWriter");

    }

}
