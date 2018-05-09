package org.neggly.macaron;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * 小節の動作・描画を行うクラス.
 * AbstractObjectを継承する.
 *
 * @author negset
 */
public class Measure extends AbstractObject
{
    /**
     * 開始時刻
     */
    long startTime;
    /**
     * BPM
     */
    float bpm;
    /**
     * 移動にかける時間(単位:ミリ秒)
     */
    int scroll;
    /**
     * 一度に出現するノーツを表す文字列
     */
    private String[] notesData;
    /**
     * ノーツごとの開始時刻
     */
    private float notesStartTime;
    /**
     * ノーツの生成カウント
     */
    private int notesCnt;

    /**
     * 動作を規定する.
     * 1ループにつき1回呼ばれる.
     */
    @Override
    public void move(int delta)
    {
        y = -100 + 600 * (StatePlay.getPassTime() - startTime) / scroll;
        // 画面外に出たらインスタンスを無効にする.
        if (y > 600)
        {
            active = false;
        }

        // ノーツを生成する.
        if (notesCnt < notesData.length - 1)
        {
            if (StatePlay.getPassTime() > notesStartTime)
            {
                String[] note = notesData[notesCnt].split("-");
                for (int i = 0; i < note.length; i++)
                {
                    newNote(note[i]);
                }

                notesStartTime += 240000 / bpm / (notesData.length - 1);
                notesCnt++;
            }
        }
    }

    /**
     * 描画処理を行う.
     * 1ループにつき1回呼ばれる.
     */
    @Override
    public void draw(Graphics g)
    {
        g.setColor(Color.gray);
        g.drawLine(0, y, 800, y);
    }

    /**
     * 実質的なノーツの生成を行う.
     *
     * @param data ノーツ1つ分をあらわす文字列
     */
    private void newNote(String data)
    {
        if (data.length() == 0) return;

        int pos = Integer.parseInt(data.substring(0, 1)) - 1;

        switch (data.substring(1))
        {
            case "ta":
                ObjectPool.createTap((long) notesStartTime, scroll, pos, 0);
                break;
            case "tb":
                ObjectPool.createTap((long) notesStartTime, scroll, pos, 1);
                break;
            case "ha":
                ObjectPool.beginHold((long) notesStartTime, scroll, pos, 0);
                break;
            case "hb":
                ObjectPool.beginHold((long) notesStartTime, scroll, pos, 1);
                break;
            case "he":
                ObjectPool.endHold((long) notesStartTime, scroll, pos);
                break;
        }
    }

    /**
     * インスタンスの有効化を行う.
     * インスタンスの使いまわしをしているので,初期化処理もここで行う.
     *
     * @param startTime 開始時刻
     * @param bpm       BPM
     * @param scroll    移動にかける時間
     * @param msrData   小節に含まれるノーツを表す文字列
     */
    public void activate(long startTime, float bpm, int scroll, String msrData)
    {
        active = true;
        this.startTime = startTime;
        this.bpm = bpm;
        this.scroll = scroll;

        y = -100;

        notesData = msrData.split(",", -1);
        notesStartTime = startTime;
        notesCnt = 0;
    }
}
