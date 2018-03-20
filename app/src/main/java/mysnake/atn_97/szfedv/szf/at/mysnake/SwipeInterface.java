package mysnake.atn_97.szfedv.szf.at.mysnake;

import android.view.View;

/**
 * Created by ATN_97 on 10.02.2016.
 */
public interface SwipeInterface {
    public void bottom2top(View v);
    public void top2bottom(View v);
    public void left2right(View v);
    public void right2left(View v);
    public void onClick(View v, int x, int y);
}
