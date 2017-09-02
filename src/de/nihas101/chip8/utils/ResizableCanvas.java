package de.nihas101.chip8.utils;

import de.nihas101.chip8.hardware.memory.ScreenMemory;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import static de.nihas101.chip8.hardware.memory.ScreenMemory.SCREEN_HEIGHT;
import static de.nihas101.chip8.hardware.memory.ScreenMemory.SCREEN_WIDTH;

/**
 * https://stackoverflow.com/questions/24533556/how-to-make-canvas-resizable-in-javafx
 */
public class ResizableCanvas extends Canvas {
    private boolean[][] memory;

    public ResizableCanvas(ScreenMemory screenMemory) {
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
        GraphicsContext graphicsContext = this.getGraphicsContext2D();
        double width = this.getWidth()/SCREEN_WIDTH;
        double height = this.getHeight()/SCREEN_HEIGHT;
        graphicsContext.clearRect(0, 0, this.getWidth(), this.getHeight());

        for(int x=0 ; x < SCREEN_WIDTH ; x++)
            for (int y = 0; y < SCREEN_HEIGHT; y++)
                if (!memory[x][y]) graphicsContext.fillRect(x * width, y * height, width, height);

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
}
