package mysnake.atn_97.szfedv.szf.at.logic;

import android.graphics.Point;
import android.util.Log;

import java.util.Random;

/**
 * Created by ATN_97 on 10.02.2016.
 */
public class GameElement {
    private static final String LOG_TAG = GamePanel.class.getSimpleName();

    protected Point location;
    protected int radius;
    protected GameElementType type;

    public enum GameElementType {
        APPLE
    }

    public GameElement(int radius) {
        location = new Point();
        this.radius = radius;
    }

    public Point getLocation() {return location;}

    public void newRandomLocation(Point fieldDimensions, Snake snake) {
        Random random = new Random();
        Point p = new Point();

        boolean valid;
        do { // suche einen leeren Platz auf dem Spielfeld
            valid = true;

            p.x = random.nextInt(fieldDimensions.x - 2) + 1;
            p.y = random.nextInt(fieldDimensions.y - 2) + 1;

            for (Cell cell : snake.getCells())
                if(cell.getLocation().equals(p))
                    valid = false;
        } while (!valid);

        Log.d(LOG_TAG, "NEW element at: " + p.x + ", " + p.y);
        location = p;
    }

    public int getRadius() {return radius;}
    public GameElementType getType() {return type; }
}
