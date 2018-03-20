package mysnake.atn_97.szfedv.szf.at.logic;

import android.graphics.Canvas;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

/**
 * Created by ATN_97 on 10.02.2016.
 */
public class MainThread extends Thread {
    private static final String LOG_TAG = MainThread.class.getSimpleName();

    // fps
    private final static int FPS = 50;
    public static int getFps() {return FPS; }

    // maximale anzahl der frames, die übersprungen werden
    private final static int MAX_FRAME_SKIPS = 5;

    // dauer eines frames
    private final static int FRAME_PERIOD = 1000 / FPS;

    // Surface holder that can access the physical surface
    private final SurfaceHolder surfaceHolder;

    // Das View, das auf Eingaben reagiert und die Oberfläche zeichnet
    private GamePanel gamePanel;

    // flag für den Status
    private static boolean running;
    public static void setRunning(boolean running) {
        MainThread.running = running;
    }

    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
        running = true;
    }

    @Override
    public void run() {
        Log.d(LOG_TAG, "Starte die Spiel-Schleife");
        Canvas canvas;

        // Anfangszeit, Zykluszeit
        long beginTime, timeDiff;

        // ms to sleep (<0 falls Rückstand)
        int sleepTime;

        // übersprungene Frames
        int framesSkipped;


        while (running) {
            canvas = null;

            // versuche das canvas zu sperren, um zu zeichnen
            try {
                canvas = this.surfaceHolder.lockCanvas();

                synchronized (surfaceHolder) {
                    beginTime = System.currentTimeMillis();

                    // reset the frames skipped
                    framesSkipped = 0;

                    // update game state
                    this.gamePanel.update();

                    // zeichne Status auf den Schirm: bildet Canvas auf Panel ab
                    this.gamePanel.render(canvas);

                    // Berechne Zykluszeit
                    timeDiff = System.currentTimeMillis() - beginTime;

                    // berechne Pausenzeit
                    sleepTime = (int) (FRAME_PERIOD - timeDiff);

                    if (sleepTime > 0) {
                        try {
                            // pausiere den Thread für eine kurze Zeit, sehr gut für Stromverbrauch
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    // wir müssen verlorene frames aufholen
                    while(sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                        // update ohne rendern
                        this.gamePanel.update();

                        // addiere Framedauer, um zu prüfen, ob es im nächsten Frame vorkommt
                        sleepTime += FRAME_PERIOD;
                        framesSkipped++;
                    }

                    if (framesSkipped > 0)
                        Log.v(LOG_TAG, "Skipped " + framesSkipped + "frames");
                }
            } finally {
                // für den Fall einer Exception wird das Surface nicht in einem undefinierten Zustand überlassen
                if (canvas != null)
                    surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }

    }
}
