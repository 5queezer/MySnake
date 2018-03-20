package mysnake.atn_97.szfedv.szf.at.logic;

import android.graphics.Color;
import android.graphics.Point;

/**
 * Created by ATN_97 on 11.02.2016.
 */
public class GreenApple extends Apple {
    private static final int SCORE = 10;

    public GreenApple(Point fieldDimensions, Snake snake, int radius) {
        super(fieldDimensions, snake, radius, SCORE, Color.GREEN);
    }
}
