package org.neggly.macaron;

import org.newdawn.slick.Graphics;

public class ObjectPool
{
    /**
     * オブジェクトのインスタンスをあらかじめ生成し,貯めておくための配列
     */
    private static Measure[] measure;
    private static Tap[] tap;
    private static Hold[] hold;
    private static Effect[] effect;

    /**
     * 最大数の設定
     */
    private static final int MEASURE_MAX = 10;
    private static final int TAP_MAX = 50;
    private static final int HOLD_MAX = 50;
    private static final int EFFECT_MAX = 20;

    /**
     * ノーツの判定幅
     * 単位はミリ秒.
     * コンフィグから読み取った値を使用する.
     */
    private static final long[] JUDGE_RANGE_TAP =
            {Config.tapRange[0], Config.tapRange[1], Config.tapRange[2]};
    private static final long[] JUDGE_RANGE_HOLD =
            {Config.holdRange[0], Config.holdRange[1], Config.holdRange[2]};

    /**
     * そのキーが判定済みかどうかを表すフラグ
     */
    private static boolean[] keyJudged;

    /**
     * レーンごとの終了判定待ちのホールドのインデックス
     */
    private static int[] holdIndex;

    /**
     * コンストラクタ
     */
    ObjectPool()
    {
        // オブジェクトの配列を確保し,配列の要素分インスタンスを作る.
        measure = new Measure[MEASURE_MAX];
        for (int i = 0; i < MEASURE_MAX; i++)
        {
            measure[i] = new Measure();
        }
        tap = new Tap[TAP_MAX];
        for (int i = 0; i < TAP_MAX; i++)
        {
            tap[i] = new Tap();
        }
        hold = new Hold[HOLD_MAX];
        for (int i = 0; i < HOLD_MAX; i++)
        {
            hold[i] = new Hold();
        }
        effect = new Effect[EFFECT_MAX];
        for (int i = 0; i < EFFECT_MAX; i++)
        {
            effect[i] = new Effect();
        }

        keyJudged = new boolean[6];
        holdIndex = new int[6];
        for (int i = 0; i < 6; i++)
        {
            holdIndex[i] = -1;
        }
    }

    /**
     * すべてのオブジェクトの動作・描画を行う.
     */
    public void doAllObjects(Graphics g, int delta)
    {
        doObjects(g, delta, measure);
        doObjects(g, delta, tap);
        doObjects(g, delta, hold);
        doObjects(g, delta, effect);
    }

    /**
     * リストに登録されているインスタンスの動作・描画を行う.
     */
    private void doObjects(Graphics g, int delta, AbstractObject[] objects)
    {
        for (int i = 0; i < objects.length; i++)
        {
            if (objects[i].active)
            {
                objects[i].move(delta);
                objects[i].draw(g);
            }
        }
    }

    /**
     * 小節インスタンスの生成・初期化を行う.
     * 実際は配列のインスタンスを使い回す.
     *
     * @param startTime 開始時刻
     * @param bpm       BPM
     * @param scroll    移動にかける時間
     * @param msrData   含まれるノーツを表す文字列
     */
    public static void createMeasure(long startTime, float bpm,
                                     int scroll, String msrData)
    {
        for (int i = 0; i < MEASURE_MAX; i++)
        {
            if (!measure[i].active)
            {
                measure[i].activate(startTime, bpm, scroll, msrData);
                return;
            }
        }
    }

    /**
     * タップインスタンスの生成・初期化を行う.
     * 実際は配列のインスタンスを使い回す.
     *
     * @param startTime 開始時刻
     * @param scroll    移動にかける時間
     * @param pos       位置
     * @param type      種類
     */
    public static void createTap(long startTime, int scroll, int pos, int type)
    {
        for (int i = 0; i < TAP_MAX; i++)
        {
            if (!tap[i].active)
            {
                tap[i].activate(startTime, scroll, pos, type);
                return;
            }
        }
    }

    /**
     * ホールドインスタンスの生成・初期化を行う.
     * 実際は配列のインスタンスを使い回す.
     *
     * @param beginTime 開始時刻
     * @param scroll    移動にかける時間
     * @param pos       位置
     * @param type      種類
     */
    public static void beginHold(long beginTime, int scroll, int pos, int type)
    {
        for (int i = 0; i < HOLD_MAX; i++)
        {
            if (!hold[i].active)
            {
                hold[i].activate(beginTime, scroll, pos, type);
                holdIndex[pos] = i;
                return;
            }
        }
    }

    /**
     * ホールドインスタンスの終了処理を行う.
     *
     * @param endTime 終了時刻
     * @param scroll  移動にかける時間
     * @param pos     位置
     */
    public static void endHold(long endTime, int scroll, int pos)
    {
        int i = holdIndex[pos];
        if (hold[i].pos == pos && !hold[i].ended)
        {
            hold[i].setEnd(endTime, scroll);
            holdIndex[pos] = -1;
            return;
        }
    }

    /**
     * 判定エフェクトインスタンスの生成・初期化を行う.
     * 実際は配列のインスタンスを使い回す.
     *
     * @param judgement 判定結果
     * @param pos       エフェクトの位置
     */
    private static void createEffect(int judgement, int pos)
    {
        for (int i = 0; i < EFFECT_MAX; i++)
        {
            if (!effect[i].active)
            {
                effect[i].activate(judgement, pos);
                return;
            }
        }
    }

    /**
     * 全てのノーツの判定を行う.
     */
    public static void judgeNotes()
    {
        // キーの判定済みフラグを倒す.
        for (int i = 0; i < keyJudged.length; i++)
        {
            keyJudged[i] = false;
        }

        // Perfect,Great,Goodの順で判定する.
        for (int judgeType = 0; judgeType < 3; judgeType++)
        {
            for (int i = 0; i < tap.length; i++)
            {
                if (tap[i].active && !tap[i].judged)
                {
                    judgeTap(i, judgeType);
                }
            }
            for (int i = 0; i < hold.length; i++)
            {
                if (hold[i].active)
                {
                    if (!hold[i].pressing)
                    {
                        judgeHoldBegin(i, judgeType);
                    }
                    else if (hold[i].ended)
                    {
                        judgeHoldEnd(i, judgeType);
                    }
                }
            }
        }
    }

    /**
     * タップの判定を行う.
     *
     * @param i         判定を行うタップインスタンスのインデックス
     * @param judgeType 判定を行う判定結果の種類
     */
    private static void judgeTap(int i, int judgeType)
    {
        long t = tap[i].startTime + tap[i].scroll;
        long lag = StatePlay.getPassTime() - t;

        if (StatePlay.autoplay)
        {
            if (lag >= 0)
            {
                tap[i].active = false;
                createEffect(0, tap[i].pos);
                StatePlay.countCombo(0);
                Drawer.playSE(tap[i].type);
            }
            return;
        }

        // 通り過ぎた(Miss判定)
        if (lag > JUDGE_RANGE_TAP[2])
        {
            tap[i].judged = true;
            createEffect(3, tap[i].pos);
            StatePlay.countCombo(3);
            return;
        }

        // キーが押されていなければ判定を行わない.
        if (!Key.isPressed(tap[i].pos))
        {
            return;
        }
        // キーが判定済みならば判定を行わない.
        if (keyJudged[tap[i].pos])
        {
            return;
        }

        if (Math.abs(lag) < JUDGE_RANGE_TAP[judgeType])
        {
            tap[i].active = false;
            createEffect(judgeType, tap[i].pos);
            StatePlay.countCombo(judgeType);
            Drawer.playSE(tap[i].type);
            keyJudged[tap[i].pos] = true;
        }
    }

    /**
     * ホールド開始の判定を行う.
     *
     * @param i         判定を行うホールドインスタンスのインデックス
     * @param judgeType 判定を行う判定結果の種類
     */
    private static void judgeHoldBegin(int i, int judgeType)
    {
        long t = hold[i].beginTime + hold[i].beginScroll;
        long lag = StatePlay.getPassTime() - t;

        if (StatePlay.autoplay)
        {
            if (lag >= 0)
            {
                hold[i].pressing = true;
                Drawer.playSE(hold[i].type);
            }
            return;
        }

        // 通り過ぎた(Miss判定)
        if (lag > JUDGE_RANGE_HOLD[2])
        {
            hold[i].active = false;
            createEffect(3, hold[i].pos);
            StatePlay.countCombo(3);
            return;
        }

        // キーが押されていなければ判定を行わない.
        if (!Key.isPressed(hold[i].pos))
        {
            return;
        }
        // キーが判定済みならば判定を行わない.
        if (keyJudged[hold[i].pos])
        {
            return;
        }

        if (Math.abs(lag) < JUDGE_RANGE_HOLD[judgeType])
        {
            hold[i].pressing = true;
            hold[i].beginJudgement = judgeType;
            Drawer.playSE(hold[i].type);
            keyJudged[hold[i].pos] = true;
        }
    }

    /**
     * ホールド終了の判定を行う.
     *
     * @param i         判定を行うホールドインスタンスのインデックス
     * @param judgeType 判定を行う判定結果の種類
     */
    private static void judgeHoldEnd(int i, int judgeType)
    {
        long t = hold[i].endTime + hold[i].endScroll;
        long lag = StatePlay.getPassTime() - t;

        if (StatePlay.autoplay)
        {
            if (lag >= 0)
            {
                hold[i].active = false;
                createEffect(0, hold[i].pos);
                StatePlay.countCombo(0);
                Drawer.playSE(2);
            }
            return;
        }

        // 遅離し(Good判定)
        if (lag > JUDGE_RANGE_HOLD[2])
        {
            hold[i].active = false;
            createEffect(2, hold[i].pos);
            StatePlay.countCombo(2);
            return;
        }

        // キーが長押し中ならば判定を行わない.
        if (Key.isDown(hold[i].pos))
        {
            return;
        }

        // 早離し(Good判定)
        if (lag < -JUDGE_RANGE_HOLD[2])
        {
            hold[i].active = false;
            createEffect(2, hold[i].pos);
            StatePlay.countCombo(2);
            return;
        }

        if (Math.abs(lag) < JUDGE_RANGE_HOLD[judgeType])
        {
            hold[i].active = false;
            // ホールド開始の判定結果と比較して,悪い方を全体の判定結果にする.
            int jm = Math.max(judgeType, hold[i].beginJudgement);
            createEffect(jm, hold[i].pos);
            StatePlay.countCombo(jm);
            Drawer.playSE(2);
        }
    }
}
