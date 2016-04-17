package com.negset.macaron;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;

/**
 * オブジェクトの生成・動作・描画を行うクラス.
 * ノーツの判定処理もここで行う.
 *
 * @author negset
 */
public class ObjectPool
{
	/** オブジェクトのインスタンスをあらかじめ生成し,貯めておくための配列 */
	private static Measure[] measure;
	private static Tap[] tap;
	private static Hold[] hold;
	private static Effect[] effect;

	/** 有効化されたインスタンスの配列におけるインデックスを登録するリスト */
	private static ArrayList<Integer> activeMeasure;
	private static ArrayList<Integer> activeTap;
	private static ArrayList<Integer> activeHold;
	private static ArrayList<Integer> activeEffect;

	/** 最大数の設定 */
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

	/** そのキーが判定済みかどうかを表すフラグ */
	private static boolean[] keyJudged;

	/**
	 * コンストラクタ
	 */
	ObjectPool()
	{
		// オブジェクトの配列を確保し,配列の要素分インスタンスを作る.
		measure = new Measure[MEASURE_MAX];
		for(int i = 0; i < MEASURE_MAX; i++)
		{
			measure[i] = new Measure(i);
		}
		tap = new Tap[TAP_MAX];
		for(int i = 0; i < TAP_MAX; i++)
		{
			tap[i] = new Tap(i);
		}
		hold = new Hold[HOLD_MAX];
		for(int i = 0; i < HOLD_MAX; i++)
		{
			hold[i] = new Hold(i);
		}
		effect = new Effect[EFFECT_MAX];
		for(int i = 0; i < EFFECT_MAX; i++)
		{
			effect[i] = new Effect(i);
		}

		// インデックスリストを初期化する.
		activeMeasure = new ArrayList<Integer>();
		activeTap = new ArrayList<Integer>();
		activeHold = new ArrayList<Integer>();
		activeEffect = new ArrayList<Integer>();

		keyJudged = new boolean[6];
	}

	/**
	 * すべてのオブジェクトの動作・描画を行う.
	 */
	public void doAllObject(Graphics g, int delta)
	{
		doObject(g, delta, measure, activeMeasure);
		doObject(g, delta, tap, activeTap);
		doObject(g, delta, hold, activeHold);
		doObject(g, delta, effect, activeEffect);
	}

	/**
	 * リストに登録されているインスタンスの動作・描画を行う.
	 */
	private void doObject(Graphics g, int delta,
			AbstractObject[] object, ArrayList<Integer> array)
	{
		for (int i = 0; i < array.size(); i++)
		{
			int n = array.get(i);
			object[n].move(delta);
			object[n].draw(g);
		}
	}

	/**
	 * 小節インスタンスの生成・初期化を行う.
	 * 実際は配列のインスタンスを使い回す.
	 *
	 * @param startTime 開始時刻
	 * @param bpm BPM
	 * @param scroll 移動にかける時間
	 * @param msrData 含まれるノーツを表す文字列
	 */
	public static void createMeasure(long startTime, float bpm,
			int scroll, String msrData)
	{
		for (int i = 0; i < MEASURE_MAX; i++)
		{
			if (activeMeasure.indexOf(i) == -1)
			{
				measure[i].activate(startTime, bpm, scroll, msrData);
				activeMeasure.add(i);
				return;
			}
		}
	}

	/**
	 * 小節インスタンスを廃棄する.
	 * 実際はインスタンスを無効化する.
	 *
	 * @param index インスタンスの配列におけるインデックス
	 */
	public static void destroyMeasure(int index)
	{
		activeMeasure.remove(activeMeasure.indexOf(index));
	}

	/**
	 * タップインスタンスの生成・初期化を行う.
	 * 実際は配列のインスタンスを使い回す.
	 *
	 * @param startTime 開始時刻
	 * @param scroll 移動にかける時間
	 * @param pos 位置
	 * @param type 種類
	 */
	public static void createTap(long startTime, int scroll, int pos, int type)
	{
		for (int i = 0; i < TAP_MAX; i++)
		{
			if (activeTap.indexOf(i) == -1)
			{
				tap[i].activate(startTime, scroll, pos, type);
				activeTap.add(i);
				return;
			}
		}
	}

	/**
	 * タップインスタンスを廃棄する.
	 * 実際はインスタンスを無効化する.
	 *
	 * @param index インスタンスの配列におけるインデックス
	 */
	public static void destroyTap(int index)
	{
		activeTap.remove(activeTap.indexOf(index));
	}

	/**
	 * ホールドインスタンスの生成・初期化を行う.
	 * 実際は配列のインスタンスを使い回す.
	 *
	 * @param beginTime 開始時刻
	 * @param scroll 移動にかける時間
	 * @param pos 位置
	 * @param type 種類
	 */
	public static void beginHold(long beginTime, int scroll, int pos, int type)
	{
		for (int i = 0; i < HOLD_MAX; i++)
		{
			if (activeHold.indexOf(i) == -1)
			{
				hold[i].activate(beginTime, scroll, pos, type);
				activeHold.add(i);
				return;
			}
		}
	}

	/**
	 * ホールドインスタンスの終了処理を行う.
	 *
	 * @param endTime 終了時刻
	 * @param scroll 移動にかける時間
	 * @param pos 位置
	 */
	public static void endHold(long endTime, int scroll, int pos)
	{
		for (int i = 0; i < activeHold.size(); i++)
		{
			int n = activeHold.get(i);
			if (hold[n].pos == pos && !hold[n].ended)
			{
				hold[n].setEnd(endTime, scroll);
				return;
			}
		}
	}

	/**
	 * ホールドインスタンスを廃棄する.
	 * 実際はインスタンスを無効化する.
	 *
	 * @param index インスタンスの配列におけるインデックス
	 */
	public static void destroyHold(int index)
	{
		activeHold.remove(activeHold.indexOf(index));
	}

	/**
	 * 判定エフェクトインスタンスの生成・初期化を行う.
	 * 実際は配列のインスタンスを使い回す.
	 *
	 * @param judgement 判定結果
	 * @param pos エフェクトの位置
	 */
	private static void createEffect(int judgement, int pos)
	{
		for (int i = 0; i < EFFECT_MAX; i++)
		{
			if (activeEffect.indexOf(i) == -1)
			{
				effect[i].activate(judgement, pos);
				activeEffect.add(i);
				return;
			}
		}
	}

	/**
	 * 判定エフェクトインスタンスを廃棄する.
	 * 実際はインスタンスを無効化する.
	 *
	 * @param index インスタンスの配列におけるインデックス
	 */
	public static void destroyEffect(int index)
	{
		activeEffect.remove(activeEffect.indexOf(index));
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
			for (int i = 0; i < activeTap.size(); i++)
			{
				int n = activeTap.get(i);
				if (!tap[n].judged)
				{
					judgeTap(n, judgeType);
				}
			}
			for (int i = 0; i < activeHold.size(); i++)
			{
				int n = activeHold.get(i);
				if (!hold[n].pressing)
				{
					judgeHoldBegin(n, judgeType);
				}
				else if (hold[n].ended)
				{
					judgeHoldEnd(n, judgeType);
				}
			}
		}
	}

	/**
	 * タップの判定を行う.
	 *
	 * @param i 判定を行うタップインスタンスのインデックス
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
				destroyTap(i);
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
			destroyTap(i);
			createEffect(judgeType, tap[i].pos);
			StatePlay.countCombo(judgeType);
			Drawer.playSE(tap[i].type);
			keyJudged[tap[i].pos] = true;
		}
	}

	/**
	 * ホールド開始の判定を行う.
	 *
	 * @param i 判定を行うホールドインスタンスのインデックス
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
			destroyHold(i);
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
	 * @param i 判定を行うホールドインスタンスのインデックス
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
				destroyHold(i);
				createEffect(0, hold[i].pos);
				StatePlay.countCombo(0);
				Drawer.playSE(2);
			}
			return;
		}

		// 遅離し(Good判定)
		if (lag > JUDGE_RANGE_HOLD[2])
		{
			destroyHold(i);
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
			destroyHold(i);
			createEffect(2, hold[i].pos);
			StatePlay.countCombo(2);
			return;
		}

		if (Math.abs(lag) < JUDGE_RANGE_HOLD[judgeType])
		{
			destroyHold(i);
			// ホールド開始の判定結果と比較して,悪い方を全体の判定結果にする.
			int jm = Math.max(judgeType, hold[i].beginJudgement);
			createEffect(jm, hold[i].pos);
			StatePlay.countCombo(jm);
			Drawer.playSE(2);
		}
	}
}