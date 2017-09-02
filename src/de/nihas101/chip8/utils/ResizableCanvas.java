package de.nihas101.chip8.utils;

import de.nihas101.chip8.hardware.memory.ScreenMemory;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import static de.nihas101.chip8.hardware.memory.ScreenMemory.SCREEN_HEIGHT;
import static de.nihas101.chip8.hardware.memory.ScreenMemory.SCREEN_WIDTH;

/**
 * https://stackoverflow.com/questions/24533556/how-to-make-canvas-resizable-in-javafx
 */
public class ResizableCanvas extends Canvas {
    private boolean[][] memory;
    private GraphicsContext graphicsContext;
    private Paint paintOff, paintOn;

    public ResizableCanvas(ScreenMemory screenMemory) {
        /* Set initial colors */
        this.graphicsContext = this.getGraphicsContext2D();
        paintOff = new Color(0,0,0,1);
        paintOn = new Color(1,1,1,1);
        // Redraw canvas when size changes.
        widthProperty().addListener(event -> draw());
        heightProperty().addListener(event -> draw());
        this.memory = screenMemory.getMemory();
    }

    /**
     * Draws what is written in the {@link ScreenMemory} onto the screen
     */
    public void draw() {
        /* Clear screen */
        //GraphicsContext graphicsContext = this.getGraphicsContext2D();
        double width = this.getWidth()/SCREEN_WIDTH;
        double height = this.getHeight()/SCREEN_HEIGHT;
        graphicsContext.clearRect(0, 0, this.getWidth(), this.getHeight());

        for(int x=0 ; x < SCREEN_WIDTH ; x++) {
            for (int y = 0; y < SCREEN_HEIGHT; y++) {
                if (!memory[x][y]) draw(x * width, y * height, width, height, paintOff);
                else               draw(x * width, y * height, width, height, paintOn );
            }
        }
    }

    private void draw(double x, double y, double w, double h, Paint paint){
        graphicsContext.setFill(paint);
        graphicsContext.fillRect(x, y, w + 1, h + 1);
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

    public void setPaintOn(Paint paint){
        this.paintOn = paint;
    }

    public void setPaintOff(Paint paint){
        this.paintOff = paint;
    }
}
