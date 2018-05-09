package org.neggly.macaron;

import java.io.IOException;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.loading.LoadingList;

/**
 * ロード時の処理と描画を行うクラス.
 *
 * @author negset
 */
public class Loading
{
    /**
     * リソースファイルの読み込みを行う.
     */
    public static void loadResorce()
    {
        if (LoadingList.get().getRemainingResources() > 0)
        {
            try
            {
                LoadingList.get().getNext().load();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * ロード画面の描画を行う.
     *
     * @param g 描画先
     */
    public static void draw(Graphics g)
    {
        g.setColor(Color.white);
        g.setFont(Drawer.fontR);
        g.drawString("Now Loading...", 590, 540);
    }

    /**
     * ロードが完了したか否かを返す.
     *
     * @return ロードが完了したか否か
     */
    public static boolean isFinished()
    {
        if (LoadingList.get().getRemainingResources() == 0)
        {
            return true;
        }
        return false;
    }
}
