package de.nihas101.chip8.utils;

import javafx.scene.paint.Color;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PixelTest {
    @Test
    public void pixel() {
        Pixel pixel = new Pixel(0, 0, 10, 10, new Color(0, 0, 0, 0));

        assertEquals(0, pixel.x, 0.00001);
        assertEquals(0, pixel.y, 0.00001);
        assertEquals(10, pixel.width, 0.00001);
        assertEquals(10, pixel.height, 0.00001);
        assertEquals(new Color(0, 0, 0, 0), pixel.paint);
    }

}