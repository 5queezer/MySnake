package mysnake.atn_97.szfedv.szf.at.mysnake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import mysnake.atn_97.szfedv.szf.at.logic.GamePanel;
import mysnake.atn_97.szfedv.szf.at.logic.MainThread;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private GamePanel gamePanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // titelzeile abdrehen
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        // vollbild einschalten
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 0xFFFF);

        gamePanel = new GamePanel(this);
        setContentView(gamePanel);

        Log.d(LOG_TAG, "View added");
    }

    @Override
    public void onRestart() {
        Log.d(LOG_TAG, "onRestart()");
        if (gamePanel != null)
            gamePanel.initGame();

        super.onRestart();
    }

    @Override
    protected void onPause() {
        Log.d(LOG_TAG, "onPause()");
        MainThread.setRunning(false);
        super.onPause();
    }
}
