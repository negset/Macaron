package org.neggly.macaron;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

/**
 * 動作・描画を行うクラス.
 * AbstractObjectを継承する.
 *
 * @author negset
 */
public class Tap extends AbstractObject
{
    /**
     * 描画する画像
     */
    private Image tap;
    /**
     * 開始時刻
     */
    long startTime;
    /**
     * 位置(0~5)
     */
    int pos;
    /**
     * 種類(0または1)
     */
    int type;
    /**
     * 移動にかける時間(単位:ミリ秒)
     */
    int scroll;
    /**
     * 判定済みかどうかのフラグ
     */
    boolean judged;

    /**
     * 動作を規定する.
     * 1ループにつき1回呼ばれる.
     */
    public void move(int delta)
    {
        y = -100 + 600 * (StatePlay.getPassTime() - startTime) / scroll;

        //画面外に出たらインスタンスを無効化する.
        if (y > 600 + tap.getHeight())
        {
            active = false;
        }
    }

    /**
     * 描画処理を行う.
     * 1ループにつき1回呼ばれる.
     */
    public void draw(Graphics g)
    {
        tap.draw(x - tap.getWidth() / 2, y - tap.getHeight() / 2);
    }

    /**
     * インスタンスの有効化を行う.
     * インスタンスの使い回しをしているので,初期化処理もここで行う.
     *
     * @param startTime 開始時刻
     * @param scroll    移動にかける時間
     * @param pos       位置
     * @param type      種類
     */
    public void activate(long startTime, int scroll, int pos, int type)
    {
        active = true;
        this.startTime = startTime;
        this.scroll = scroll;
        this.pos = pos;
        this.type = type;

        switch (pos)
        {
            case 0:
                x = 100;
                break;
            case 1:
                x = 200;
                break;
            case 2:
                x = 300;
                break;
            case 3:
                x = 500;
                break;
            case 4:
                x = 600;
                break;
            case 5:
                x = 700;
                break;
        }
        y = -100;

        if (type == 0)
        {
            tap = Drawer.tap_a;
        }
        else
        {
            tap = Drawer.tap_b;
        }

        judged = false;
    }
}
