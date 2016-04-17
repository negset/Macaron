package com.negset.macaron;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

/**
 * ホールドの動作・描画を行うクラス.
 * AbstractObjectを継承する.
 *
 * @author negset
 */
public class Hold extends AbstractObject
{
	/** 描画する画像 */
	private Image[] hold = new Image[3];
	/** 位置(0~5) */
	int pos;
	/** 種類(0または1) */
	int type;

	/** 開始・終了のy座標 */
	private float beginY, endY;
	/** 開始・終了の時刻 */
	long beginTime, endTime;
	/** 開始・終了の移動にかける時間 */
	int beginScroll, endScroll;

	/** 長押し中か否かのフラグ */
	boolean pressing;
	/** 終了しているか否かのフラグ */
	boolean ended;
	/** 開始の判定結果 */
	int beginJudgement;

	/**
	 * コンストラクタ
	 */
	Hold(int index)
	{
		this.index = index;
	}

	/**
	 * 動作を規定する.
	 * 1ループにつき1回呼ばれる.
	 */
	@Override
	public void move(int delta)
	{
		// 長押し中でないなら経過時間に応じて開始のy座標を動かす.
		if (!pressing)
		{
			if (beginY < 500)
			{
				long t = StatePlay.getPassTime() - beginTime;
				beginY = -100 + 600 * t / beginScroll;

				// 判定ラインの位置まで来たら止める
				if (beginY > 500)
				{
					beginY = 500;
				}
			}
		}
		// 長押し中なら開始のy座標を判定ラインの位置にする.
		else
		{
			if (beginY != 500)
			{
				beginY = 500;
			}
		}

		// 終了後なら経過時間に応じて終了のy座標を動かす.
		if (ended)
		{
			if (endY < 500)
			{
				long t = StatePlay.getPassTime() - endTime;
				endY = -100 + 600 * t / endScroll;

				// 判定ラインの位置まで来たら止める
				if (endY > 500)
				{
					endY = 500;
				}
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
		int width = hold[0].getWidth();
		int height = hold[0].getHeight();
		hold[1].draw(x - width / 2, endY, width, beginY - endY);
		hold[2].draw(x - width / 2, endY - height / 2);
		hold[0].draw(x - width / 2, beginY - height / 2);
	}

	/**
	 * 終了する.
	 *
	 * @param endTime 終了時刻
	 */
	public void setEnd(long endTime, int scroll)
	{
		this.endTime = endTime;
		endScroll = scroll;
		ended = true;
	}

	/**
	 * インスタンスの有効化を行う.
	 * インスタンスの使い回しをしているので,初期化処理もここで行う.
	 *
	 * @param beginTime 開始時刻
	 * @param pos 位置
	 * @param type 種類
	 */
	public void activate(long beginTime, int scroll, int pos, int type)
	{
		this.beginTime = beginTime;
		beginScroll = scroll;
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

		if (type == 0)
		{
			hold = Drawer.hold_a;
		}
		else
		{
			hold = Drawer.hold_b;
		}

		beginY = -100;
		endY = -100;

		ended = false;
		pressing = false;
		beginJudgement = 0;
	}
}
