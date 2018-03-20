package mysnake.atn_97.szfedv.szf.at.logic;

import android.graphics.Point;

/**
 * Created by ATN_97 on 11.02.2016.
 */
public class Cell extends GameElement {
    public Cell(int x, int y, int radius) {
        super(radius);
        location = new Point(x,y);
    }

    public Cell(Cell cell) {
        super(cell.getRadius());
        location = new Point(cell.location);
    }
}
