package de.nihas101.chip8.utils;

import javafx.scene.paint.Paint;

public class Pixel {
    public final double x;
    public final double y;
    public final double width;
    public final double height;
    public final Paint paint;

    public Pixel(double x, double y, double width, double height, Paint paint) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.paint = paint;
    }
}
