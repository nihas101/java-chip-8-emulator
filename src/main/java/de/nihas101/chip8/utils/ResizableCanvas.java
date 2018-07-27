package de.nihas101.chip8.utils;

import de.nihas101.chip8.hardware.memory.ScreenMemory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import static de.nihas101.chip8.hardware.memory.ScreenMemory.SCREEN_HEIGHT;
import static de.nihas101.chip8.hardware.memory.ScreenMemory.SCREEN_WIDTH;
import static de.nihas101.chip8.utils.Constants.DRAW_INTERVAL_MULTIPLIER;

/**
 * https://stackoverflow.com/questions/24533556/how-to-make-canvas-resizable-in-javafx
 */
public class ResizableCanvas extends Canvas {
    private boolean[][] memory;
    private GraphicsContext graphicsContext;
    private Paint paintOff, paintOn;

    public ResizableCanvas(ScreenMemory screenMemory) {
        setInitialColors();
        setRedrawOnSizeChange();
        this.memory = screenMemory.getMemory();
    }

    public void setupGraphicsContext() {
        this.graphicsContext = this.getGraphicsContext2D();
    }

    private void setInitialColors() {
        paintOff = new Color(0, 0, 0, 1);
        paintOn = new Color(1, 1, 1, 1);
    }

    private void setRedrawOnSizeChange() {
        widthProperty().addListener(event -> draw());
        heightProperty().addListener(event -> draw());
    }

    /**
     * Draws what is written in the {@link ScreenMemory} onto the screen
     */
    public void draw() {
        /* Calculate height and width of pixels, depending on the size of the canvas */
        double width = this.getWidth() / SCREEN_WIDTH;
        double height = this.getHeight() / SCREEN_HEIGHT;
        /* Draw Pixels */
        for (int x = 0; x < SCREEN_WIDTH; x++) drawColumn(x, width, height);
    }

    private void drawColumn(int row, double width, double height) {
        for (int y = 0; y < SCREEN_HEIGHT; y++) {
            if (!memory[row][y]) drawPixel(new Pixel(row * width, y * height, width, height, paintOff));
            else drawPixel(new Pixel(row * width, y * height, width, height, paintOn));
        }
    }

    /**
     * Draws a pixel
     *
     * @param pixel The pixel to draw
     */
    private void drawPixel(Pixel pixel) {
        graphicsContext.setFill(pixel.paint);
        graphicsContext.fillRect(pixel.x, pixel.y, pixel.width + 1, pixel.height + 1);
    }

    public Timeline setupTimeLine() {
        /* Setup keyframes to draw the canvas */
        final Duration frameDuration = Duration.ONE.multiply(DRAW_INTERVAL_MULTIPLIER);
        final KeyFrame oneFrame = new KeyFrame(frameDuration, event -> draw());
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(oneFrame);
        return timeline;
    }

    public void setMemory(ScreenMemory memory) {
        this.memory = memory.getMemory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isResizable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    public void setPaintOn(Paint paint) {
        this.paintOn = paint;
    }

    public void setPaintOff(Paint paint) {
        this.paintOff = paint;
    }
}
