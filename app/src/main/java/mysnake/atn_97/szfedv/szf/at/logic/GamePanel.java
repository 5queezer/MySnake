package mysnake.atn_97.szfedv.szf.at.logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayDeque;
import java.util.Random;

import mysnake.atn_97.szfedv.szf.at.mysnake.ActivitySwipeDetector;
import mysnake.atn_97.szfedv.szf.at.mysnake.R;
import mysnake.atn_97.szfedv.szf.at.mysnake.SwipeInterface;

/**
 * Created by ATN_97 on 10.02.2016.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback, SwipeInterface {
    private static final String LOG_TAG = GamePanel.class.getSimpleName();

    private static final int RED_APPLE_PERCENTAGE = 20;

    private Context context;
    private MainThread thread;
    private Paint paint;
    private int tickCounter;
    private ArrayDeque<Direction> directionsQueue;

    private Point fieldDimensions;
    private int cellsDiameter, cellsRadius;

    private Snake snake;
    private Apple apple;

    private Bitmap  borderCell, snakeCell, snakeShieldedCell, greenAppleCell;


    public GamePanel(Context context) {
        super(context);

        // speichere context (um Highscore zu speichern)
        this.context = context;

        // füge den Callback (this) zum surface holder, um events zu empfangen
        getHolder().addCallback(this);

        // set on touch listener
        setOnTouchListener(new ActivitySwipeDetector(this));

        // create paint
        paint = new Paint();

        // create directions queue
        directionsQueue = new ArrayDeque<Direction>();

        // load bitmaps
        loadBitmaps();

        // make the GamePanel focusable so it can handle events
        setFocusable(true);
    }


    private void loadBitmaps() {
        borderCell = BitmapFactory.decodeResource(getResources(), R.drawable.border_cell );
        snakeCell = BitmapFactory.decodeResource(getResources(), R.drawable.snake_cell);
        greenAppleCell = BitmapFactory.decodeResource(getResources(), R.drawable.green_apple_cell);
    }

    public void initGame() {
        tickCounter = 0;

        directionsQueue.clear();

        Log.d("SnakeView", "View width: " + getWidth());
        Log.d("SnakeView", "View height: " + getHeight());

        int fieldWidth = 20;
        cellsDiameter = getWidth() / fieldWidth;
        cellsRadius = cellsDiameter / 2;
        int fieldHeight = getHeight() / cellsDiameter;
        fieldDimensions = new Point(fieldWidth, fieldHeight);

        Log.d("MainActivity", "Cell diameter: "+ cellsDiameter);
        Log.d("MainActivity", "Field Dimensions: " + fieldWidth + "x" + fieldHeight);

        snake = new Snake(cellsRadius, (borderCell != null && snakeCell != null && greenAppleCell != null));

        // create apple
        generateNewApple();

        // reset highScoreUpdated flag
        // highScoreUpdated = false;

        // load high score
        // SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        // highScore = sharedPref.getLong(highScoreKey, 0);

        // mache neuen Thread und starte die Spielschleife
        thread = new MainThread(getHolder(), this);
        thread.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // initialize game
        initGame();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void bottom2top(View v) {
        if (snake.getDirection().isHorizontal())
            snake.setDirection(Direction.UP);
    }

    @Override
    public void top2bottom(View v) {
        if (snake.getDirection().isHorizontal())
            snake.setDirection(Direction.DOWN);
    }

    @Override
    public void left2right(View v) {
        if (snake.getDirection().isVertical())
            snake.setDirection(Direction.RIGHT);
    }

    @Override
    public void right2left(View v) {
        if (snake.getDirection().isVertical())
            snake.setDirection(Direction.LEFT);
    }

    private void generateNewApple() {
        // TODO: Randomize Apple colors
        apple = new GreenApple(fieldDimensions, snake, cellsRadius);
    }

    private void checkIfSnakeHitAnyWall() {
        // get snake head location
        Point head = snake.getHead().getLocation();

        switch (snake.getDirection()) {
            case UP:
                if (head.y <= 1)
                    snake.kill();
                break;
            case DOWN:
                if (head.y >= fieldDimensions.y - 2)
                    snake.kill();
                break;
            case LEFT:
                if (head.x <= 1)
                    snake.kill();
                break;
            case RIGHT:
                if (head.x >= fieldDimensions.x - 2)
                    snake.kill();
                break;
        }

        // TODO: if (snake.isDead() && snake.hasShield())...
    }

    public void checkIfSnakeAteApple() {
        if (snake.ate(apple)) {
            Log.d("Snake", "Apple has been eaten");

            // increase snake size
            snake.incSize();

            // set speed needs to be incremented flag
            snake.enableSpeedNeedsToBeIncrementedFlag();

            // update score
            snake.incScore(apple.getScore());

            // update high score
            //if (snake.getScore() > highScore)
            //    highScore = snake.getScore();

            // mache neuen Apfel
            generateNewApple();
        }
    }


    /**
     * Spiel update
     */
    public void update() {
        // inkrement tick zähler
        tickCounter++;

        // TODO: update clock counter
        //if(tickCounter % MainThread.getFps() == 0)
        //    snake.updateClock();

        // update snake
        if (tickCounter % snake.getMoveDelay() == 0) {
            if (snake.speedNeedsToBeIncremented())
                snake.increaseSpeed();

            // set snake direction
            boolean done = false;
            while(!directionsQueue.isEmpty() && !done) {
                Direction direction = directionsQueue.poll();

                switch (direction) {
                    case UP:
                    case DOWN:
                        if (snake.isMovingHorizontally()) {
                            snake.setDirection(direction);
                            Log.d( LOG_TAG, "Consumed direction " + direction.toString() + " from queue");
                            done = true;
                        }
                        break;
                    case RIGHT:
                    case LEFT:
                        if (snake.isMovingVertically()) {
                            snake.setDirection(direction);
                            Log.d(LOG_TAG, "Consumed direction " + direction.toString() + " from queue");
                            done = true;
                        }
                        break;
                }
            }

            // prüfe, ob die Schlange in die Wand gefahren ist
            checkIfSnakeHitAnyWall();

            // prüfe, ob die Schlange noch lebt
            if (!snake.isDead()) {
                // bewege die Schlange
                snake.move();

                // prüfen, ob die Schlange Äpfel gegessen hat
                checkIfSnakeAteApple();

                // TODO: update special element
                // updateSpecialElement();
            } else {
                // if high score hasn't been updated
//                if (!highScoreUpdated) {
//                    Log.d(TAG, "Updating high score");
//
//                    saveHighScore();
//                    highScoreUpdated = true;
//                }
            }
        }
    }

    @Override
    public void onClick(View v, int x, int y) {
        // if snake is dead
        if (snake.isDead()) {
            Log.d(LOG_TAG, "Starte neues Spiel");
            initGame();
        } else {
            Direction direction = directionsQueue.isEmpty() ? snake.getDirection() : directionsQueue.getLast();

            if (direction.isHorizontal()) {
                if (y < snake.getHead().getLocation().y * cellsDiameter) {
                    direction = Direction.UP;
                    Log.d(LOG_TAG, "Füge Richtung UP zur Queue hinzu");
                } else {
                    direction = Direction.DOWN;
                    Log.d(LOG_TAG, "Füge Richtung DOWN zur Queue hinzu");
                }
            } else {
                if (x < snake.getHead().getLocation().x * cellsDiameter) {
                    direction = Direction.LEFT;
                    Log.d(LOG_TAG, "Füge Richtung LEFT zur Queue hinzu");
                } else {
                    direction = Direction.RIGHT;
                    Log.d(LOG_TAG, "Füge Richtung RIGHT zur Queue hinzu");
                }
            }

            directionsQueue.add(direction);
        }
    }

    public void render (Canvas canvas) {
        // zeichne background
        drawBackground(canvas);

        // zeichne Bereichsgrenzen
        drawBoardLimits(canvas);

        // zeichne Apfel
        drawApple(canvas);

        // draw special element
        // drawSpecialElement(canvas);

        // draw snake
        drawSnake(canvas);

        // display score
        // drawScore(canvas);

        // wenn Schlange tot ist
        if (snake.isDead())
            drawGameOverMessage(canvas);
    }

    private void drawBackground(Canvas canvas) {
        int bgColor = snake.isDead() ? Color.rgb(204, 0, 71) : Color.rgb(44, 84, 158);
        paint.setColor(bgColor);
        canvas.drawRect(0, 0, fieldDimensions.x * cellsDiameter, fieldDimensions.y * cellsDiameter, paint);
    }

    private void drawCell(Canvas canvas, Point p, Bitmap bitmap) {
        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        int x = p.x * cellsDiameter;
        int y = p.y * cellsDiameter;
        Rect dst = new Rect(x,y, x + cellsDiameter, y + cellsDiameter);

        // zeichne Bitmap
        canvas.drawBitmap(bitmap, src, dst, paint);
    }

    private void drawBoardLimits(Canvas canvas) {
        // obere und untere grenze
        for (int i = 0; i < fieldDimensions.x; i++) {
            drawCell(canvas, new Point(i,0), borderCell);
            drawCell(canvas, new Point(i, fieldDimensions.y - 1), borderCell);
        }

        // linke und rechte grenze
        for (int i = 0; i < fieldDimensions.y; i++) {
            drawCell(canvas, new Point(0, i), borderCell);
            drawCell(canvas, new Point(fieldDimensions.x - 1, i), borderCell);
        }
    }

    private void drawApple(Canvas canvas) {
        Bitmap bitmap;

        switch (apple.getColor()) {
            case Color.GREEN:
                bitmap = greenAppleCell;
                break;
            // TODO: restliche Farben einfügen
            default:
                bitmap = borderCell;
                break;
        }

        drawCell(canvas, apple.getLocation(), bitmap);
    }

    // TODO: private void drawSpecialElement(Canvas canvas)

    private void drawSnake(Canvas canvas) {
        if (snake.isUsingBitmaps()) {
            // TODO: Shield einfügen
            /*
            if (snake.hasShield())
                drawCell(canvas, cell.getLocation(), snakeShieldedCell);
            else
            */
            for (Cell cell : snake.getCells())
                drawCell(canvas, cell.getLocation(), snakeCell);
        } else {
            paint.setColor(Color.BLACK);

            for (Cell cell : snake.getCells()) {
                Point p = cell.getLocation();

                int x = cellsRadius + p.x + cellsDiameter;
                int y = cellsRadius + p.y + cellsDiameter;
                canvas.drawCircle(x, y, cellsRadius, paint);
            }
        }
    }

    private void drawGameOverMessage(Canvas canvas) {
        String[] text = new String[]{ getResources().getString(R.string.gameover), getResources().getString(R.string.gameover_klick)};

        int textSize = 3 * cellsDiameter / 2;
        int leftPadding = cellsDiameter + textSize / 4;
        int topPadding = getHeight() / 2 - text.length * textSize;

        for ( int i = 0; i < text.length; i++ ) {
            paint.setTextSize(textSize);
            paint.setColor(Color.YELLOW);
            canvas.drawText(text[i], leftPadding, topPadding + (i + 1) * textSize, paint);
        }
    }
}
