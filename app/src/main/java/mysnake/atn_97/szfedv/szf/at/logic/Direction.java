package mysnake.atn_97.szfedv.szf.at.logic;

/**
 * Created by ATN_97 on 11.02.2016.
 */
public enum Direction {
    RIGHT(0), DOWN(1), LEFT(2), UP(3);
    private Direction(int value) {this.value = value;}

    private final int value;
    public int getValue() {return value;}

    public boolean isHorizontal() { return value == 0 || value == 2; }
    public boolean isVertical() { return !isHorizontal(); }

    @Override
    public String toString() {
        switch (value) {
            case 0:
                return "RIGHT";
            case 1:
                return "DOWN";
            case 2:
                return "LEFT";
            case 3:
                return "UP";
        }
        return "";
    }
}
